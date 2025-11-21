# api/urls.py

from django.urls import path, re_path
from . import views

urlpatterns = [
    # POST /api/v1/applications/start/
    path('applications/start/', views.start_application, name='start_application'),

    # GET /api/v1/applications/<uuid:app_id>/
    # We use re_path for a simple regex, but <uuid:app_id> is cleaner if we use it
    path('applications/<uuid:app_id>/', views.get_application_status, name='get_application_status'),

    path('applications/<uuid:app_id>/document/', views.upload_document, name='upload_document'),
    path('applications/<uuid:app_id>/selfie/', views.upload_selfie, name='upload_selfie'),
    path('applications/<uuid:app_id>/analyze/', views.analyze_application, name='analyze_application'),
]