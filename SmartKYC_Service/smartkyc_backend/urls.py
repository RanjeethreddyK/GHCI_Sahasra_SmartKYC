# smartkyc_backend/urls.py

from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),

    # Add this line to include all URLs from our 'api' app
    # All our API endpoints will be prefixed with /api/v1/
    path('api/v1/', include('api.urls')),
]
