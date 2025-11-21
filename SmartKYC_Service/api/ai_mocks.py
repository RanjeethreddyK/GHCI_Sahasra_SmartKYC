# api/ai_mocks.py

import time
import random
from datetime import datetime, timedelta


def mock_document_intelligence(document_type, file_name):
    """
    Simulates the "Document Intelligence Layer" (TrOCR + CNN Forensics).

    This function will:
    1. Add a realistic processing delay (1.5 - 3.5 seconds).
    2. Return mock extracted data based on the document type.
    3. Return mock forensic analysis (tamper check).
    """

    print(f"[AI MOCK]: Processing '{file_name}' as '{document_type}'...")

    # Simulate AI processing time
    processing_time = random.uniform(1.5, 3.5)
    time.sleep(processing_time)

    # --- Mock Model Information ---
    # This directly maps to your project proposal
    model_info = {
        "ocr_model": "mock-trocr-transformer-v1.2",
        "forensics_model": "mock-cnn-tamper-v2.1",
        "processing_time_sec": round(processing_time, 2)
    }

    # --- Mock Data Generation ---
    if document_type == "PASSPORT":
        data = {
            "extracted_data": {
                "first_name": "JANE",
                "last_name": "DOE",
                "document_number": "P01234567",
                "dob": "1990-01-01",
                "expiry_date": (datetime.now() + timedelta(days=1825)).strftime('%Y-%m-%d'),
                "nationality": "USA"
            },
            "forensics": {
                "status": "CLEAR",  # 'CLEAR', 'TAMPERED', 'BLURRY'
                "confidence_score": round(random.uniform(0.95, 0.99), 4),
                "checks_passed": ["hologram_check", "font_analysis", "template_match"]
            },
            "model_info": model_info
        }

    elif document_type == "UTILITY_BILL":
        data = {
            "extracted_data": {
                "name": "JANE DOE",
                "address": "123 MAIN ST, ANYTOWN, USA 12345",
                "issue_date": (datetime.now() - timedelta(days=30)).strftime('%Y-%m-%d'),
                "provider": "City Electric & Gas"
            },
            "forensics": {
                "status": "CLEAR",
                "confidence_score": round(random.uniform(0.92, 0.98), 4),
                "checks_passed": ["logo_match", "address_database_crosscheck", "date_check"]
            },
            "model_info": model_info
        }

    # A special case for testing rejection
    elif document_type == "TAMPERED_EXAMPLE":
        data = {
            "extracted_data": {
                "first_name": "J0HN",  # Deliberate OCR 'error'
                "last_name": "SM1TH",
                "document_number": "T80123456",
                "dob": "1985-02-15",
                "expiry_date": "2025-01-01",
                "nationality": "UKN"
            },
            "forensics": {
                "status": "TAMPERED",  # This will trigger a rejection
                "confidence_score": round(random.uniform(0.98, 0.99), 4),
                "reason": "Digital alteration detected in Date of Birth field.",
                "checks_failed": ["pixel_analysis", "font_analysis"]
            },
            "model_info": model_info
        }

    else:
        # Generic fallback
        data = {
            "extracted_data": {},
            "forensics": {
                "status": "UNSUPPORTED_DOCUMENT",
                "reason": f"Document type '{document_type}' is not supported."
            },
            "model_info": model_info
        }

    print(f"[AI MOCK]: Processing complete. Status: {data['forensics']['status']}")
    return data


def mock_biometric_verification(app_id, file_name, trigger_fail=False):
    """
    Simulates the "Verification Layer" (CNN Face Match + Liveness).

    This function will:
    1. Add a realistic processing delay.
    2. Simulate liveness detection (is it a real person?).
    3. Simulate face matching (does this person match the ID?).
    """

    print(f"[AI MOCK]: Processing selfie '{file_name}' for app '{app_id}'...")

    # Simulate AI processing time
    processing_time = random.uniform(1.0, 2.5)
    time.sleep(processing_time)

    # --- Mock Model Information ---
    model_info = {
        "face_match_model": "mock-cnn-facenet-v3.0",
        "liveness_model": "mock-antispoof-v1.8",
        "processing_time_sec": round(processing_time, 2)
    }

    # --- Mock AI Logic ---

    # Simple way to trigger a failure for testing
    if trigger_fail:
        liveness_status = "REAL"
        face_match_status = "MISMATCH"
        match_score = round(random.uniform(0.30, 0.60), 4)
        overall_status = "REJECTED_MISMATCH"
        reason = "Selfie does not match the photo on the ID document."

    # Simulate a "fake" liveness check
    elif "spoof" in file_name.lower():
        liveness_status = "FAKE"  # e.g., photo of a screen, printed photo
        face_match_status = "NOT_ATTEMPTED"
        match_score = 0.0
        overall_status = "REJECTED_LIVENESS"
        reason = "Liveness check failed. Suspected spoof attempt."

    # The "Good" case
    else:
        liveness_status = "REAL"
        face_match_status = "MATCH"
        match_score = round(random.uniform(0.95, 0.99), 4)  # High confidence match
        overall_status = "CLEAR"
        reason = "Biometric verification successful."

    data = {
        "status": overall_status,
        "reason": reason,
        "liveness_check": {
            "status": liveness_status,
            "confidence": round(random.uniform(0.97, 0.99), 4) if liveness_status == "REAL" else round(
                random.uniform(0.80, 0.99), 4)
        },
        "face_match": {
            "status": face_match_status,
            "match_score": match_score,
            "id_document_face_ref": f"doc_{app_id}_face.jpg",  # Mock reference
            "selfie_face_ref": f"selfie_{app_id}_face.jpg"  # Mock reference
        },
        "model_info": model_info
    }

    print(f"[AI MOCK]: Biometric check complete. Status: {overall_status}")
    return data


def mock_risk_intelligence(application_data):
    """
    Simulates the "Risk Intelligence Layer" (XGBoost) and "Explainability Layer".

    This function will:
    1. Analyze all data collected in the application.
    2. Generate a final risk score.
    3. Make a final decision (APPROVED, REJECTED, MANUAL_REVIEW).
    4. Provide human-readable explanations (XAI).
    """

    app_id = application_data.get('application_id')
    print(f"[AI MOCK]: Running risk analysis for app '{app_id}'...")

    # Simulate AI processing time
    processing_time = random.uniform(0.5, 1.5)
    time.sleep(processing_time)

    # --- Mock Model Information ---
    model_info = {
        "risk_model": "mock-xgboost-classifier-v1.4",
        "xai_model": "mock-shap-explainer-v1.1",
        "processing_time_sec": round(processing_time, 2)
    }

    # --- AI Analysis Logic ---
    risk_score = 0
    explanations = []

    # 1. Analyze Document Forensics
    try:
        id_forensics = application_data['documents']['id_document']['forensics']
        if id_forensics['status'] != 'CLEAR':
            risk_score += 70
            explanations.append(f"ID Document flagged for: {id_forensics['status']}.")

        addr_forensics = application_data['documents']['address_proof']['forensics']
        if addr_forensics['status'] != 'CLEAR':
            risk_score += 40
            explanations.append(f"Address Document flagged for: {addr_forensics['status']}.")

    except Exception:
        # This shouldn't happen if the workflow is correct, but it's a good safeguard
        risk_score += 90
        explanations.append("Critical error: Missing document forensics data.")

    # 2. Analyze Biometric Verification
    try:
        selfie_analysis = application_data['selfie']['ai_analysis']
        if selfie_analysis['status'] != 'CLEAR':
            risk_score += 90
            explanations.append(f"Biometric verification failed: {selfie_analysis['reason']}.")

        match_score = selfie_analysis.get('face_match', {}).get('match_score', 0)
        if match_score < 0.9:  # Lower than our 'good' threshold
            risk_score += (1 - match_score) * 50  # Add points based on mismatch
            explanations.append(f"Low biometric match score ({match_score}).")

    except Exception:
        risk_score += 90
        explanations.append("Critical error: Missing biometric data.")

    # 3. Analyze Extracted Data (Cross-referencing)
    try:
        extracted = application_data.get('extracted_data', {})
        id_name = f"{extracted.get('first_name', '')} {extracted.get('last_name', '')}".strip()
        addr_name = extracted.get('name', '')

        if id_name and addr_name and id_name.lower() != addr_name.lower():
            risk_score += 25
            explanations.append(f"Name mismatch: ID says '{id_name}', Address proof says '{addr_name}'.")

    except Exception:
        explanations.append("Could not perform data cross-reference.")

    # 4. Final Risk Calculation & Decision
    # Add some base "good" factors
    if not explanations:
        risk_score = random.randint(5, 15)  # Base score for a clean application
        explanations.append("All automated checks passed.")
        explanations.append("Data consistent across documents.")
        explanations.append("Biometric match score is high.")

    risk_score = min(max(int(risk_score), 0), 100)  # Clamp score between 0 and 100

    final_decision = "MANUAL_REVIEW"  # Default
    if risk_score < 20:
        final_decision = "APPROVED"
    elif risk_score >= 70:
        final_decision = "REJECTED"
    # Anything between 20 and 69 remains MANUAL_REVIEW

    # Build the final result object
    result = {
        "decision": final_decision,
        "risk_score": risk_score,
        "xai_explanations": explanations,
        "model_info": model_info,
        "analyzed_at": datetime.utcnow().isoformat() + "Z"
    }

    print(f"[AI MOCK]: Risk analysis complete. Decision: {final_decision} (Score: {risk_score})")
    return result