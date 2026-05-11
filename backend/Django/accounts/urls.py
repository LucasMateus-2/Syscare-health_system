from django.urls import path
from rest_framework_simplejwt.views import TokenRefreshView

from accounts.views import (
    LoginView,
    MedicoRegistrationView,
    MeView,
    PacienteRegistrationView,
)


urlpatterns = [
    path(
        "register/medico/",
        MedicoRegistrationView.as_view(),
        name="register-medico",
    ),
    path(
        "register/paciente/",
        PacienteRegistrationView.as_view(),
        name="register-paciente",
    ),
    path("login/", LoginView.as_view(), name="login"),
    path("token/refresh/", TokenRefreshView.as_view(), name="token-refresh"),
    path("me/", MeView.as_view(), name="me"),
]
