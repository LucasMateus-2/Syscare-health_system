import django_filters
from .models import Consulta


class ConsultaFilter(django_filters.FilterSet):
    """
    Filtros disponíveis para o endpoint /consultas/:

      ?medico=1
      ?paciente=2
      ?status=AGENDADO
      ?data=2025-06-01              (dia exato)
      ?data_inicio=2025-06-01       (a partir de)
      ?data_fim=2025-06-30          (até)
    """
    data = django_filters.DateFilter(field_name="data_hora", lookup_expr="date")
    data_inicio = django_filters.DateFilter(field_name="data_hora", lookup_expr="date__gte")
    data_fim = django_filters.DateFilter(field_name="data_hora", lookup_expr="date__lte")

    class Meta:
        model = Consulta
        fields = ["medico", "paciente", "status"]
