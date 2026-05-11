import re

from django.contrib.auth import authenticate, get_user_model
from django.core.exceptions import ObjectDoesNotExist
from django.db import transaction
from rest_framework import serializers
from rest_framework.exceptions import AuthenticationFailed
from rest_framework_simplejwt.tokens import RefreshToken

from accounts.models import Especialidade, Medico, Paciente


User = get_user_model()


class TokenResponseMixin:
    def get_tokens(self, user):
        refresh = RefreshToken.for_user(user)
        return {
            "access": str(refresh.access_token),
            "refresh": str(refresh),
        }


class MedicoRegistrationSerializer(serializers.Serializer):
    email = serializers.EmailField()
    password = serializers.CharField(min_length=8, write_only=True)
    full_name = serializers.CharField(max_length=150)
    crm = serializers.CharField(max_length=20)
    especialidade_id = serializers.IntegerField(required=False)

    def validate_email(self, value):
        if User.objects.filter(email=value).exists():
            raise serializers.ValidationError("Este e-mail já está em uso.")

        return value

    def validate_crm(self, value):
        if Medico.objects.filter(crm=value).exists():
            raise serializers.ValidationError("Este CRM já está em uso.")

        return value

    def validate_especialidade_id(self, value):
        if not Especialidade.objects.filter(id=value).exists():
            raise serializers.ValidationError("Especialidade não encontrada.")

        return value

    def create(self, validated_data):
        with transaction.atomic():
            user = User.objects.create_user(
                email=validated_data["email"],
                password=validated_data["password"],
                full_name=validated_data["full_name"],
                role=User.Role.DOCTOR,
            )
            Medico.objects.create(
                user=user,
                crm=validated_data["crm"],
                especialidade_id=validated_data.get("especialidade_id"),
            )

        return user


class PacienteRegistrationSerializer(serializers.Serializer):
    email = serializers.EmailField()
    password = serializers.CharField(min_length=8, write_only=True)
    full_name = serializers.CharField(max_length=150)
    cpf = serializers.CharField(max_length=14)
    data_nascimento = serializers.DateField()
    tipo_sanguineo = serializers.ChoiceField(
        choices=Paciente.TipoSanguineo.choices,
        required=False,
    )
    alergias = serializers.CharField(
        allow_blank=True,
        required=False,
        style={"base_template": "textarea.html"},
    )

    def validate_email(self, value):
        if User.objects.filter(email=value).exists():
            raise serializers.ValidationError("Este e-mail já está em uso.")

        return value

    def validate_cpf(self, value):
        cpf = re.sub(r"\D", "", value)

        if len(cpf) != 11:
            raise serializers.ValidationError("CPF deve conter 11 dígitos.")

        if Paciente.objects.filter(cpf=cpf).exists():
            raise serializers.ValidationError("Este CPF já está em uso.")

        return cpf

    def create(self, validated_data):
        with transaction.atomic():
            user = User.objects.create_user(
                email=validated_data["email"],
                password=validated_data["password"],
                full_name=validated_data["full_name"],
                role=User.Role.PATIENT,
            )
            Paciente.objects.create(
                user=user,
                cpf=validated_data["cpf"],
                data_nascimento=validated_data["data_nascimento"],
                tipo_sanguineo=validated_data.get("tipo_sanguineo", ""),
                alergias=validated_data.get("alergias", ""),
            )

        return user


class LoginSerializer(serializers.Serializer):
    email = serializers.EmailField()
    password = serializers.CharField(write_only=True)

    def validate(self, attrs):
        user = authenticate(
            request=self.context.get("request"),
            email=attrs["email"],
            password=attrs["password"],
        )

        if user is None:
            raise AuthenticationFailed("Credenciais inválidas.")

        if not user.is_active:
            raise AuthenticationFailed("Usuário inativo.")

        attrs["user"] = user
        return attrs


class MeSerializer(serializers.ModelSerializer):
    profile = serializers.SerializerMethodField()

    class Meta:
        model = User
        fields = ["id", "email", "full_name", "role", "created_at", "profile"]

    def get_profile(self, user):
        try:
            if user.is_doctor:
                medico = user.medico_profile
                especialidade = None

                if medico.especialidade:
                    especialidade = {
                        "id": medico.especialidade.id,
                        "name": medico.especialidade.name,
                    }

                return {
                    "crm": medico.crm,
                    "especialidade": especialidade,
                }

            if user.is_patient:
                paciente = user.paciente_profile
                return {
                    "cpf": paciente.cpf,
                    "data_nascimento": paciente.data_nascimento,
                    "tipo_sanguineo": paciente.tipo_sanguineo,
                }
        except ObjectDoesNotExist:
            return {}

        return {}


class UserSerializer(serializers.ModelSerializer):
    password = serializers.CharField(
        required=False,
        style={"input_type": "password"},
        trim_whitespace=False,
        write_only=True,
    )

    class Meta:
        model = User
        fields = [
            "id",
            "email",
            "password",
            "full_name",
            "role",
            "is_active",
            "created_at",
        ]
        read_only_fields = ["id", "role", "created_at"]

    def create(self, validated_data):
        raise serializers.ValidationError(
            "Use um endpoint de registro específico para criar usuários."
        )

    def update(self, instance, validated_data):
        password = validated_data.pop("password", None)

        for attr, value in validated_data.items():
            setattr(instance, attr, value)

        if password:
            instance.set_password(password)

        instance.save()
        return instance


class EspecialidadeSerializer(serializers.ModelSerializer):
    class Meta:
        model = Especialidade
        fields = "__all__"


class MedicoSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)
    user_id = serializers.PrimaryKeyRelatedField(
        queryset=User.objects.filter(role=User.Role.DOCTOR),
        source="user",
        write_only=True,
    )
    especialidade = EspecialidadeSerializer(read_only=True)
    especialidade_id = serializers.PrimaryKeyRelatedField(
        allow_null=True,
        queryset=Especialidade.objects.all(),
        required=False,
        source="especialidade",
        write_only=True,
    )

    class Meta:
        model = Medico
        fields = [
            "id",
            "user",
            "user_id",
            "crm",
            "especialidade",
            "especialidade_id",
        ]
        read_only_fields = ["id"]


class PacienteSerializer(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)
    user_id = serializers.PrimaryKeyRelatedField(
        queryset=User.objects.filter(role=User.Role.PATIENT),
        source="user",
        write_only=True,
    )

    class Meta:
        model = Paciente
        fields = [
            "id",
            "user",
            "user_id",
            "cpf",
            "data_nascimento",
            "tipo_sanguineo",
            "alergias",
        ]
        read_only_fields = ["id"]
