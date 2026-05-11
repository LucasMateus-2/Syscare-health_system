from django.db import models

from accounts.models import Medico, Paciente
from appointments.models import Consulta


class Prontuario(models.Model):
    paciente = models.ForeignKey(
        Paciente,
        on_delete=models.CASCADE,
    )
    medico = models.ForeignKey(
        Medico,
        on_delete=models.PROTECT,
    )
    consulta = models.ForeignKey(
        Consulta,
        on_delete=models.SET_NULL,
        null=True,
    )
    diagnostico = models.TextField()
    prescricao = models.TextField(blank=True)
    evolucao_clinica = models.TextField(blank=True)
    data_registro = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ["-data_registro"]
        verbose_name = "prontuário"
        verbose_name_plural = "prontuários"

    def __str__(self):
        return (
            f"Prontuário de {self.paciente} em "
            f"{self.data_registro:%d/%m/%Y}"
        )
