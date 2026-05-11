from django.contrib.auth import get_user_model
from rest_framework import generics, status, views
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework.response import Response
from rest_framework.viewsets import ModelViewSet

from accounts.models import Especialidade, Medico, Paciente
from accounts.serializers import (
    EspecialidadeSerializer,
    LoginSerializer,
    MeSerializer,
    MedicoSerializer,
    MedicoRegistrationSerializer,
    PacienteSerializer,
    PacienteRegistrationSerializer,
    TokenResponseMixin,
    UserSerializer,
)


User = get_user_model()


class MedicoRegistrationView(TokenResponseMixin, generics.CreateAPIView):
    permission_classes = [AllowAny]
    serializer_class = MedicoRegistrationSerializer

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.save()
        medico = user.medico_profile

        return Response(
            {
                "id": user.id,
                "email": user.email,
                "full_name": user.full_name,
                "role": user.role,
                "crm": medico.crm,
                "tokens": self.get_tokens(user),
            },
            status=status.HTTP_201_CREATED,
        )


class PacienteRegistrationView(TokenResponseMixin, generics.CreateAPIView):
    permission_classes = [AllowAny]
    serializer_class = PacienteRegistrationSerializer

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.save()
        paciente = user.paciente_profile

        return Response(
            {
                "id": user.id,
                "email": user.email,
                "full_name": user.full_name,
                "role": user.role,
                "cpf": paciente.cpf,
                "tokens": self.get_tokens(user),
            },
            status=status.HTTP_201_CREATED,
        )


class LoginView(TokenResponseMixin, views.APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        serializer = LoginSerializer(
            data=request.data,
            context={"request": request},
        )
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data["user"]

        return Response(
            {
                "tokens": self.get_tokens(user),
                "user": {
                    "id": user.id,
                    "email": user.email,
                    "full_name": user.full_name,
                    "role": user.role,
                },
            },
            status=status.HTTP_200_OK,
        )


class MeView(generics.RetrieveAPIView):
    permission_classes = [IsAuthenticated]
    serializer_class = MeSerializer

    def get_object(self):
        return self.request.user


class UserViewSet(ModelViewSet):
    """Manage SysCare users and their platform roles."""

    queryset = User.objects.all().order_by("full_name")
    serializer_class = UserSerializer
    filterset_fields = ["role", "is_active"]
    search_fields = ["email", "full_name"]
    ordering_fields = ["full_name", "email", "created_at"]


class EspecialidadeViewSet(ModelViewSet):
    """Manage medical specialties available for doctors."""

    queryset = Especialidade.objects.all()
    serializer_class = EspecialidadeSerializer
    search_fields = ["name", "description"]
    ordering_fields = ["name"]


class MedicoViewSet(ModelViewSet):
    """Manage doctor profiles and their specialties."""

    queryset = Medico.objects.select_related("user", "especialidade")
    serializer_class = MedicoSerializer
    filterset_fields = ["especialidade"]
    search_fields = [
        "crm",
        "user__email",
        "user__full_name",
        "especialidade__name",
    ]
    ordering_fields = ["crm", "user__full_name"]


class PacienteViewSet(ModelViewSet):
    """Manage patient profiles and clinical identification data."""

    queryset = Paciente.objects.select_related("user")
    serializer_class = PacienteSerializer
    search_fields = ["cpf", "user__email", "user__full_name"]
    ordering_fields = ["user__full_name", "data_nascimento"]
