from django.contrib.auth.models import AbstractUser, BaseUserManager
from django.db import models


class CustomUserManager(BaseUserManager):
    use_in_migrations = True

    def _create_user(self, email, password=None, **extra_fields):
        if not email:
            raise ValueError("O e-mail deve ser informado.")

        email = self.normalize_email(email)
        user = self.model(email=email, **extra_fields)
        user.set_password(password)
        user.save(using=self._db)
        return user

    def create_user(self, email, password=None, **extra_fields):
        extra_fields.setdefault("is_staff", False)
        extra_fields.setdefault("is_superuser", False)
        return self._create_user(email, password, **extra_fields)

    def create_superuser(self, email, password=None, **extra_fields):
        extra_fields.setdefault("role", self.model.Role.STAFF)
        extra_fields.setdefault("is_staff", True)
        extra_fields.setdefault("is_superuser", True)
        extra_fields.setdefault("is_active", True)

        if extra_fields.get("role") != self.model.Role.STAFF:
            raise ValueError("Superusuário deve ter perfil de funcionário.")

        if extra_fields.get("is_staff") is not True:
            raise ValueError("Superusuário deve ter is_staff=True.")

        if extra_fields.get("is_superuser") is not True:
            raise ValueError("Superusuário deve ter is_superuser=True.")

        return self._create_user(email, password, **extra_fields)


UserManager = CustomUserManager


class User(AbstractUser):
    class Role(models.TextChoices):
        PATIENT = "PATIENT", "Paciente"
        DOCTOR = "DOCTOR", "Médico"
        STAFF = "STAFF", "Funcionário"

    username = None
    email = models.EmailField(unique=True)
    full_name = models.CharField(max_length=150)
    role = models.CharField(
        max_length=20,
        choices=Role.choices,
        editable=False,
    )
    is_active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)

    objects = CustomUserManager()

    USERNAME_FIELD = "email"
    REQUIRED_FIELDS = ["full_name"]

    class Meta:
        verbose_name = "usuário"
        verbose_name_plural = "usuários"

    @property
    def is_doctor(self):
        return hasattr(self, "medico_profile")

    @property
    def is_patient(self):
        return hasattr(self, "paciente_profile")

    def __str__(self):
        return f"{self.full_name} <{self.email}>"


class Especialidade(models.Model):
    name = models.CharField(max_length=100, unique=True)
    description = models.TextField(blank=True)

    class Meta:
        ordering = ["name"]
        verbose_name = "especialidade"
        verbose_name_plural = "especialidades"

    def __str__(self):
        return self.name


class Medico(models.Model):
    user = models.OneToOneField(
        User,
        on_delete=models.CASCADE,
        related_name="medico_profile",
    )
    crm = models.CharField(max_length=20, unique=True, db_index=True)
    especialidade = models.ForeignKey(
        Especialidade,
        on_delete=models.SET_NULL,
        null=True,
    )

    class Meta:
        verbose_name = "médico"
        verbose_name_plural = "médicos"

    def __str__(self):
        return f"Dr. {self.user.full_name} ({self.crm})"


class Paciente(models.Model):
    class TipoSanguineo(models.TextChoices):
        A_POSITIVO = "A+", "A+"
        A_NEGATIVO = "A-", "A-"
        B_POSITIVO = "B+", "B+"
        B_NEGATIVO = "B-", "B-"
        AB_POSITIVO = "AB+", "AB+"
        AB_NEGATIVO = "AB-", "AB-"
        O_POSITIVO = "O+", "O+"
        O_NEGATIVO = "O-", "O-"

    user = models.OneToOneField(
        User,
        on_delete=models.CASCADE,
        related_name="paciente_profile",
    )
    cpf = models.CharField(max_length=14, unique=True, db_index=True)
    data_nascimento = models.DateField()
    tipo_sanguineo = models.CharField(
        max_length=3,
        choices=TipoSanguineo.choices,
        blank=True,
    )
    alergias = models.TextField(blank=True)

    class Meta:
        verbose_name = "paciente"
        verbose_name_plural = "pacientes"

    def __str__(self):
        return f"{self.user.full_name} (CPF: {self.cpf})"
