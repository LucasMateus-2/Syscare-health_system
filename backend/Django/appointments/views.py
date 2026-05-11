from django.db import transaction
from rest_framework import viewsets, filters, status
from rest_framework.decorators import action
from rest_framework.exceptions import ValidationError
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from django_filters.rest_framework import DjangoFilterBackend

from accounts.models import Especialidade, Medico, Paciente
from .models import Consulta
from .serializers import (
    ConsultaSerializer,
    ConsultaStatusSerializer,
    EspecialidadeSerializer,
)
from .filters import ConsultaFilter


class EspecialidadeViewSet(viewsets.ModelViewSet):
    """
    CRUD completo de especialidades médicas.
    Acessível por qualquer usuário autenticado.
    """
    queryset = Especialidade.objects.all()
    serializer_class = EspecialidadeSerializer
    permission_classes = [IsAuthenticated]
    filter_backends = [filters.SearchFilter]
    search_fields = ["name"]


class MedicoViewSet(viewsets.ReadOnlyModelViewSet):
    """
    Listagem e detalhe de médicos.
    Criação ocorre exclusivamente via /api/v1/auth/register/medico/.
    """
    queryset = Medico.objects.select_related("user", "especialidade").all()
    permission_classes = [IsAuthenticated]
    filter_backends = [DjangoFilterBackend, filters.SearchFilter]
    filterset_fields = ["especialidade"]
    search_fields = ["crm", "user__full_name"]

    def get_serializer_class(self):
        # ✏️ ALTERE AQUI: importe MedicoNestedSerializer de serializers.py
        from .serializers import MedicoNestedSerializer
        return MedicoNestedSerializer


class PacienteViewSet(viewsets.ReadOnlyModelViewSet):
    """
    Listagem e detalhe de pacientes.
    Criação ocorre exclusivamente via /api/v1/auth/register/paciente/.
    """
    queryset = Paciente.objects.select_related("user").all()
    permission_classes = [IsAuthenticated]
    filter_backends = [DjangoFilterBackend, filters.SearchFilter]
    filterset_fields = ["cpf"]
    search_fields = ["cpf", "user__full_name"]

    def get_serializer_class(self):
        from .serializers import PacienteNestedSerializer
        return PacienteNestedSerializer


class ConsultaViewSet(viewsets.ModelViewSet):
    """
    CRUD de consultas + ações de mudança de status.

    Ações extras:
      POST /consultas/{id}/concluir/  → marca como CONCLUIDO
      POST /consultas/{id}/cancelar/  → marca como CANCELADO
    """
    permission_classes = [IsAuthenticated]
    serializer_class = ConsultaSerializer
    filter_backends = [DjangoFilterBackend, filters.OrderingFilter]
    filterset_class = ConsultaFilter
    ordering_fields = ["data_hora", "created_at"]
    ordering = ["-data_hora"]

    def get_queryset(self):
        return Consulta.objects.select_related(
            "medico__user",
            "medico__especialidade",
            "paciente__user",
        ).all()

    @action(detail=True, methods=["post"], url_path="concluir")
    def concluir(self, request, pk=None):
        """Marca a consulta como CONCLUÍDA. Só válido se status for AGENDADO."""
        consulta = self.get_object()
        if consulta.status != Consulta.Status.AGENDADO:
            raise ValidationError(
                f"Não é possível concluir uma consulta com status '{consulta.status}'."
            )
        with transaction.atomic():
            consulta.status = Consulta.Status.CONCLUIDO
            consulta.save(update_fields=["status", "updated_at"])
        return Response(
            ConsultaStatusSerializer(consulta).data,
            status=status.HTTP_200_OK,
        )

    @action(detail=True, methods=["post"], url_path="cancelar")
    def cancelar(self, request, pk=None):
        """Cancela a consulta. Não é possível cancelar uma consulta já concluída."""
        consulta = self.get_object()
        if consulta.status == Consulta.Status.CONCLUIDO:
            raise ValidationError(
                "Não é possível cancelar uma consulta já concluída."
            )
        with transaction.atomic():
            consulta.status = Consulta.Status.CANCELADO
            consulta.save(update_fields=["status", "updated_at"])
        return Response(
            ConsultaStatusSerializer(consulta).data,
            status=status.HTTP_200_OK,
        )
