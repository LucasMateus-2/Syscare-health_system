from rest_framework.viewsets import ModelViewSet

from records.models import Prontuario
from records.serializers import ProntuarioSerializer


class ProntuarioViewSet(ModelViewSet):
    """Manage medical records linked to patients and doctors."""

    queryset = Prontuario.objects.select_related(
        "paciente",
        "paciente__user",
        "medico",
        "medico__user",
        "medico__especialidade",
        "consulta",
    )
    serializer_class = ProntuarioSerializer
    filterset_fields = ["paciente"]
    search_fields = [
        "paciente__cpf",
        "paciente__user__full_name",
        "medico__crm",
        "medico__user__full_name",
        "diagnostico",
    ]
    ordering_fields = ["data_registro"]
