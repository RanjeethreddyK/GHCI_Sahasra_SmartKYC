# api/views.py

from rest_framework.decorators import api_view, parser_classes
from rest_framework.parsers import MultiPartParser, FormParser
from rest_framework.response import Response
from rest_framework import status
from . import data_manager
from . import ai_mocks


@api_view(['POST'])
def start_application(request):
    """
    Starts a new KYC application process.
    """
    try:
        new_app = data_manager.create_new_application()
        return Response(new_app, status=status.HTTP_201_CREATED)
    except Exception as e:
        return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


@api_view(['GET'])
def get_application_status(request, app_id):
    """
    Retrieves the status and data for a specific KYC application.
    """
    try:
        # Convert app_id from URL (which is UUID object) to string
        app_id_str = str(app_id)

        application = data_manager.get_application(app_id_str)

        if application:
            return Response(application, status=status.HTTP_200_OK)
        else:
            return Response({"error": "Application not found"}, status=status.HTTP_404_NOT_FOUND)

    except Exception as e:
        return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


@api_view(['POST'])
@parser_classes([MultiPartParser, FormParser])  # Tell DRF to handle files
def upload_document(request, app_id):
    """
    Uploads a document for a specific KYC application.
    Receives 'document_type' and 'file' in form-data.

    Valid document_types: PASSPORT, UTILITY_BILL, TAMPERED_EXAMPLE
    """
    try:
        app_id_str = str(app_id)
        # 1. Check if application exists
        application = data_manager.get_application(app_id_str)
        if not application:
            return Response({"error": "Application not found"}, status=status.HTTP_404_NOT_FOUND)

        # 2. Get data from the multipart request
        document_type = request.data.get('document_type')
        file = request.FILES.get('file')

        if not document_type or not file:
            return Response(
                {"error": "Missing 'document_type' or 'file' in form-data"},
                status=status.HTTP_400_BAD_REQUEST
            )

        # 3. Determine where to store this document
        storage_key = None
        if document_type in ["PASSPORT", "DRIVER_LICENSE", "TAMPERED_EXAMPLE"]:
            storage_key = "id_document"
        elif document_type == "UTILITY_BILL":
            storage_key = "address_proof"

        if not storage_key:
            return Response(
                {"error": f"Invalid 'document_type': {document_type}"},
                status=status.HTTP_400_BAD_REQUEST
            )

        # 4. Call our "AI Engine"
        # We are not saving the file, just simulating its processing
        # In a real app, we'd save to S3 and pass the URL
        mock_file_path = f"uploads/{app_id_str}/{file.name}"

        ai_result = ai_mocks.mock_document_intelligence(document_type, file.name)

        # 5. Save results and update workflow
        updated_application = data_manager.save_document_data(
            app_id_str,
            storage_key,
            document_type,
            mock_file_path,
            ai_result
        )

        return Response(updated_application, status=status.HTTP_200_OK)

    except Exception as e:
        return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


@api_view(['POST'])
@parser_classes([MultiPartParser, FormParser])
def upload_selfie(request, app_id):
    """
    Uploads a selfie for biometric and liveness verification.
    Receives 'file' in form-data.
    Can also receive an optional 'trigger_fail' field for testing.
    """
    try:
        app_id_str = str(app_id)
        # 1. Check if application exists
        application = data_manager.get_application(app_id_str)
        if not application:
            return Response({"error": "Application not found"}, status=status.HTTP_404_NOT_FOUND)

        # 2. === Workflow State Check ===
        # Only allow selfie upload if documents are done.
        if application['status'] != "PENDING_SELFIE":
            return Response(
                {"error": f"Cannot upload selfie. Application status is '{application['status']}'."},
                status=status.HTTP_400_BAD_REQUEST
            )

        # 3. Get file from the request
        file = request.FILES.get('file')
        if not file:
            return Response(
                {"error": "Missing 'file' in form-data"},
                status=status.HTTP_400_BAD_REQUEST
            )

        # Check for optional test flag
        trigger_fail = request.data.get('trigger_fail', 'false').lower() == 'true'

        # 4. Call our "AI Engine"
        mock_file_path = f"uploads/{app_id_str}/{file.name}"

        ai_result = ai_mocks.mock_biometric_verification(
            app_id_str,
            file.name,
            trigger_fail=trigger_fail
        )

        # 5. Save results and update workflow
        updated_application = data_manager.save_selfie_data(
            app_id_str,
            mock_file_path,
            ai_result
        )

        return Response(updated_application, status=status.HTTP_200_OK)

    except Exception as e:
        return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


@api_view(['POST'])
def analyze_application(request, app_id):
    """
    Triggers the final 'Risk Intelligence' AI layer to make a
    decision on the application.
    """
    try:
        app_id_str = str(app_id)
        # 1. Check if application exists
        application = data_manager.get_application(app_id_str)
        if not application:
            return Response({"error": "Application not found"}, status=status.HTTP_404_NOT_FOUND)

        # 2. === Workflow State Check ===
        # Only allow analysis if selfie is done.
        if application['status'] != "PENDING_RISK_ANALYSIS":
            return Response(
                {"error": f"Cannot analyze. Application status is '{application['status']}'."},
                status=status.HTTP_400_BAD_REQUEST
            )

        # 3. Call our "AI Engine"
        # This AI needs the *entire* application object to analyze
        ai_result = ai_mocks.mock_risk_intelligence(application)

        # 4. Save results and update workflow
        updated_application = data_manager.save_risk_analysis(
            app_id_str,
            ai_result
        )

        return Response(updated_application, status=status.HTTP_200_OK)

    except Exception as e:
        return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
