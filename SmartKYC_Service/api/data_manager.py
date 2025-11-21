# api/data_manager.py

import json
import os
import uuid
import copy
from datetime import datetime

# Define the path to our data file
DATA_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'data')
APP_FILE = os.path.join(DATA_DIR, 'applications.json')


# --- Helper Functions ---

def read_data():
    """Reads the entire applications.json file."""
    # Ensure the file exists
    if not os.path.exists(APP_FILE):
        write_data({})
        return {}

    try:
        with open(APP_FILE, 'r') as f:
            # Use an empty dict if the file is empty
            data = json.load(f)
            return data
    except json.JSONDecodeError:
        return {}


def write_data(data):
    """Writes the entire data object to applications.json."""
    with open(APP_FILE, 'w') as f:
        json.dump(data, f, indent=4)


# --- Core Application Functions ---

def create_new_application():
    """
    Creates a new KYC application entry.
    """
    applications = read_data()

    app_id = str(uuid.uuid4())
    now = datetime.utcnow().isoformat() + "Z"  # ISO 8601 format

    new_app = {
        "application_id": app_id,
        "status": "PENDING_DOCUMENTS",  # Initial status
        "created_at": now,
        "updated_at": now,
        "risk_score": None,
        "explanations": [],
        "documents": {
            "id_document": None,
            "address_proof": None
        },
        "selfie": None,
        "extracted_data": None
    }

    applications[app_id] = new_app
    write_data(applications)

    return new_app


def get_application(app_id):
    """
    Retrieves a specific application by its ID.
    """
    applications = read_data()
    return applications.get(app_id)  # Returns None if not found


def update_application(app_id, updates):
    """
    Updates an existing application.
    """
    applications = read_data()
    if app_id not in applications:
        return None

    # Merge updates
    applications[app_id].update(updates)

    # Update the 'updated_at' timestamp
    applications[app_id]['updated_at'] = datetime.utcnow().isoformat() + "Z"

    write_data(applications)
    return applications[app_id]


def merge_extracted_data(app):
    """
    Fuses extracted data from all processed documents into a single
    top-level 'extracted_data' object.
    """
    print("[Data Manager]: Merging extracted data...")

    # Start with a clean copy or existing data
    fused_data = copy.deepcopy(app.get('extracted_data', {})) or {}

    # 1. Merge ID Document Data
    id_doc_obj = app.get('documents', {}).get('id_document')  # This might be None or a dict
    if id_doc_obj:  # Check if it's not None
        id_data = id_doc_obj.get('extracted_data')
        if id_data:
            fused_data.update(id_data)

    # 2. Merge Address Proof Data
    address_doc_obj = app.get('documents', {}).get('address_proof')  # This will be None on the first pass
    if address_doc_obj:  # Check if it's not None
        address_data = address_doc_obj.get('extracted_data')
        if address_data:
            # Smart merge for 'name' and 'address'
            if 'name' not in fused_data:
                fused_data['name'] = address_data.get('name')
            if 'address' not in fused_data:
                fused_data['address'] = address_data.get('address')

            # Add other address-specific fields
            fused_data['address_issue_date'] = address_data.get('issue_date')
            fused_data['address_provider'] = address_data.get('provider')

    app['extracted_data'] = fused_data
    return app


def save_document_data(app_id, storage_key, document_type, file_path, ai_result):
    """
    Saves the AI processing results to the application and updates its status.
    This acts as our "workflow engine".

    storage_key: 'id_document' or 'address_proof'
    """
    applications = read_data()
    app = applications.get(app_id)
    if not app:
        return None

    # 1. Create the document entry
    forensics = ai_result.get('forensics', {})
    doc_status = "PROCESSED" if forensics.get('status') == 'CLEAR' else f"REJECTED_{forensics.get('status')}"

    document_entry = {
        "file_path": file_path,
        "uploaded_at": datetime.utcnow().isoformat() + "Z",
        "status": doc_status,
        "document_type": document_type,
        "forensics": forensics,
        "extracted_data": ai_result.get('extracted_data'),
        "model_info": ai_result.get('model_info')
    }

    # 2. Save the entry to the application
    app['documents'][storage_key] = document_entry

    # 3. Update the fused data
    app = merge_extracted_data(app)

    # 4. === Workflow Engine Logic ===
    # Update the main application status based on this upload

    # Add an explanation for any rejections
    if doc_status != "PROCESSED":
        app['status'] = f"REJECTED_{storage_key.upper()}"
        explanation = f"{storage_key} was rejected. Reason: {forensics.get('reason', 'See document forensics.')}"
        if explanation not in app['explanations']:
            app['explanations'].append(explanation)

    else:
        # If this document is OK, check what's next
        id_ok = (app['documents'].get('id_document') and
                 app['documents']['id_document']['status'] == 'PROCESSED')

        address_ok = (app['documents'].get('address_proof') and
                      app['documents']['address_proof']['status'] == 'PROCESSED')

        if id_ok and address_ok:
            # Both are done, move to the next step
            app['status'] = "PENDING_SELFIE"
            app['explanations'] = ["All documents processed. Please proceed to liveness check."]
        elif id_ok:
            app['status'] = "PENDING_ADDRESS_PROOF"
            app['explanations'] = ["ID document processed. Please upload proof of address."]
        elif address_ok:
            app['status'] = "PENDING_ID_DOCUMENT"
            app['explanations'] = ["Proof of address processed. Please upload an ID document."]
        else:
            app['status'] = "PENDING_DOCUMENTS"  # Should not happen, but safe

    # 5. Finalize update
    app['updated_at'] = datetime.utcnow().isoformat() + "Z"
    applications[app_id] = app
    write_data(applications)

    return app


def save_selfie_data(app_id, file_path, ai_result):
    """
    Saves the AI biometric/liveness results to the application
    and updates its status.
    """
    applications = read_data()
    app = applications.get(app_id)
    if not app:
        return None

    # 1. Create the selfie entry
    selfie_entry = {
        "file_path": file_path,
        "uploaded_at": datetime.utcnow().isoformat() + "Z",
        "status": ai_result.get('status'),
        "ai_analysis": ai_result
    }

    app['selfie'] = selfie_entry

    # 2. === Workflow Engine Logic ===

    # Add an explanation for this step
    explanation = ai_result.get('reason', 'Selfie processed.')
    if explanation not in app['explanations']:
        app['explanations'].append(explanation)

    if ai_result.get('status') == "CLEAR":
        # Success! Move to the next and final AI step.
        app['status'] = "PENDING_RISK_ANALYSIS"
        # Clean up old explanations if we've moved on
        app['explanations'] = [
            "ID document processed.",
            "Address proof processed.",
            "Biometric verification successful.",
            "Proceeding to final risk analysis."
        ]
    else:
        # Failure.
        app['status'] = f"REJECTED_{ai_result.get('status', 'SELFIE')}"

    # 3. Finalize update
    app['updated_at'] = datetime.utcnow().isoformat() + "Z"
    applications[app_id] = app
    write_data(applications)

    return app


def save_risk_analysis(app_id, ai_result):
    """
    Saves the final risk analysis and sets the final application status.
    """
    applications = read_data()
    app = applications.get(app_id)
    if not app:
        return None

    # 1. Save the analysis data
    app['risk_analysis'] = ai_result
    app['risk_score'] = ai_result.get('risk_score')

    # 2. === Workflow Engine Logic ===
    # This is the final step. Set the main status to the AI's decision.
    final_decision = ai_result.get('decision')
    app['status'] = final_decision

    # Add XAI explanations to the top-level
    app['explanations'] = ai_result.get('xai_explanations', [])

    # 3. Finalize update
    app['updated_at'] = datetime.utcnow().isoformat() + "Z"
    applications[app_id] = app
    write_data(applications)

    return app