from django.db import models

from accounts.models import Medico, Paciente


class Consulta(models.Model):
    class Status(models.TextChoices):
        AGENDADO = "AGENDADO", "Agendado"
        CONCLUIDO = "CONCLUIDO", "Concluído"
        CANCELADO = "CANCELADO", "Cancelado"

    medico = models.ForeignKey(
        Medico,
        on_delete=models.PROTECT,
    )
    paciente = models.ForeignKey(
        Paciente,
        on_delete=models.PROTECT,
    )
    data_hora = models.DateTimeField()
    status = models.CharField(
        max_length=20,
        choices=Status.choices,
        default=Status.AGENDADO,
    )
    observacoes = models.TextField(blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        ordering = ["-data_hora"]
        unique_together = [("medico", "data_hora")]
        verbose_name = "consulta"
        verbose_name_plural = "consultas"

    def __str__(self):
        return (
            f"{self.paciente} com {self.medico} em "
            f"{self.data_hora:%d/%m/%Y %H:%M}"
        )
