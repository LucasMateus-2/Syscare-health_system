from rest_framework import serializers

from accounts.models import Especialidade, Medico, Paciente
from .models import Consulta


# ─── Nested helpers ───────────────────────────────────────────────────────────

class UserMinimalSerializer(serializers.Serializer):
    """Representação mínima do usuário para leitura aninhada."""
    id = serializers.IntegerField(read_only=True)
    full_name = serializers.CharField(read_only=True)
    email = serializers.EmailField(read_only=True)


class EspecialidadeSerializer(serializers.ModelSerializer):
    class Meta:
        model = Especialidade
        fields = ["id", "name", "description"]


class MedicoNestedSerializer(serializers.ModelSerializer):
    user = UserMinimalSerializer(read_only=True)
    especialidade = EspecialidadeSerializer(read_only=True)

    class Meta:
        model = Medico
        fields = ["id", "crm", "user", "especialidade"]


class PacienteNestedSerializer(serializers.ModelSerializer):
    user = UserMinimalSerializer(read_only=True)

    class Meta:
        model = Paciente
        fields = ["id", "cpf", "data_nascimento", "tipo_sanguineo", "user"]


# ─── Consulta ─────────────────────────────────────────────────────────────────

class ConsultaSerializer(serializers.ModelSerializer):
    # Leitura: objetos aninhados
    medico = MedicoNestedSerializer(read_only=True)
    paciente = PacienteNestedSerializer(read_only=True)

    # Escrita: apenas IDs
    medico_id = serializers.PrimaryKeyRelatedField(
        queryset=Medico.objects.select_related("user"),
        source="medico",
        write_only=True,
    )
    paciente_id = serializers.PrimaryKeyRelatedField(
        queryset=Paciente.objects.select_related("user"),
        source="paciente",
        write_only=True,
    )

    # Status não pode ser alterado diretamente no update — use /concluir/ ou /cancelar/
    status = serializers.CharField(read_only=True)

    class Meta:
        model = Consulta
        fields = [
            "id",
            "medico",
            "medico_id",
            "paciente",
            "paciente_id",
            "data_hora",
            "status",
            "observacoes",
            "created_at",
            "updated_at",
        ]
        read_only_fields = ["created_at", "updated_at"]

    def validate_data_hora(self, value):
        """Impede agendamentos no passado."""
        from django.utils import timezone
        if value < timezone.now():
            raise serializers.ValidationError(
                "Não é possível agendar uma consulta em data/hora passada."
            )
        return value


class ConsultaStatusSerializer(serializers.ModelSerializer):
    """Usado apenas nas actions concluir/cancelar para retorno."""
    medico = MedicoNestedSerializer(read_only=True)
    paciente = PacienteNestedSerializer(read_only=True)

    class Meta:
        model = Consulta
        fields = ["id", "medico", "paciente", "data_hora", "status", "updated_at"]
