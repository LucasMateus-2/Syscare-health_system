from accounts.models import Especialidade, Medico, Paciente, User
from appointments.models import Consulta
from records.models import Prontuario


__all__ = [
    "Consulta",
    "Especialidade",
    "Medico",
    "Paciente",
    "Prontuario",
    "User",
]
