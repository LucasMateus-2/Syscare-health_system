from rest_framework import serializers

from accounts.models import Medico, Paciente
from accounts.serializers import MedicoSerializer, PacienteSerializer
from appointments.models import Consulta
from records.models import Prontuario


class ProntuarioSerializer(serializers.ModelSerializer):
    paciente = PacienteSerializer(read_only=True)
    medico = MedicoSerializer(read_only=True)
    consulta = serializers.PrimaryKeyRelatedField(read_only=True)
    paciente_id = serializers.PrimaryKeyRelatedField(
        queryset=Paciente.objects.all(),
        source="paciente",
        write_only=True,
    )
    medico_id = serializers.PrimaryKeyRelatedField(
        queryset=Medico.objects.all(),
        source="medico",
        write_only=True,
    )
    consulta_id = serializers.PrimaryKeyRelatedField(
        allow_null=True,
        queryset=Consulta.objects.all(),
        required=False,
        source="consulta",
        write_only=True,
    )

    class Meta:
        model = Prontuario
        fields = [
            "id",
            "paciente",
            "paciente_id",
            "medico",
            "medico_id",
            "consulta",
            "consulta_id",
            "diagnostico",
            "prescricao",
            "evolucao_clinica",
            "data_registro",
        ]
        read_only_fields = ["id", "data_registro"]
