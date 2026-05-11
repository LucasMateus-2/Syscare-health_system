from django.contrib import admin
from django.urls import include, path
from rest_framework.routers import DefaultRouter

from accounts.views import (
    EspecialidadeViewSet,
    MedicoViewSet,
    PacienteViewSet,
    UserViewSet,
)
from appointments.views import ConsultaViewSet
from records.views import ProntuarioViewSet


router = DefaultRouter()
router.register("users", UserViewSet, basename="user")
router.register(
    "especialidades",
    EspecialidadeViewSet,
    basename="especialidade",
)
router.register("medicos", MedicoViewSet, basename="medico")
router.register("pacientes", PacienteViewSet, basename="paciente")
router.register("consultas", ConsultaViewSet, basename="consulta")
router.register("prontuarios", ProntuarioViewSet, basename="prontuario")

urlpatterns = [
    path("admin/", admin.site.urls),
    path("api/v1/auth/", include("accounts.urls")),
    path("api/v1/", include(router.urls)),
]
