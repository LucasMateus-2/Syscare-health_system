package com.polaris.syscare_backend.application.user;


import com.polaris.syscare_backend.application.auth.records.RegisterUserCommand;
import com.polaris.syscare_backend.application.auth.services.RegisterUserService;
import com.polaris.syscare_backend.application.auth.usecases.RegisterUserUsecase;
import com.polaris.syscare_backend.domain.shared.exception.DomainException;
import com.polaris.syscare_backend.domain.user.User;
import com.polaris.syscare_backend.domain.user.UserRepository;
import com.polaris.syscare_backend.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;


class RegisterUserFlowTest
{

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RegisterUserUsecase registerUserUseCase;

    @BeforeEach
    void setUp()
    {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        registerUserUseCase = new RegisterUserService(userRepository, passwordEncoder);
    }

    @Test
    void shouldRegisterUserSuccessfully()
    {
        var command = new RegisterUserCommand(
                "João Silva",
                "joao@empresa.com",
                "senha123",
                UserRole.PROFESSIONAL
        );

        when(userRepository.findByEmail("joao@empresa.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("senha123"))
                .thenReturn("encodedPassword");

        registerUserUseCase.register(command);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldNotRegisterUserWhenEmailAlreadyExists()
    {
        var command = new RegisterUserCommand(
                "João Silva",
                "joao@empresa.com",
                "senha123",
                UserRole.RECEPTIONIST
        );

        var existingUser = new User(
                UUID.randomUUID(),
                "joao@empresa.com",
                "hash",
                UserRole.RECEPTIONIST,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(userRepository.findByEmail("joao@empresa.com"))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> registerUserUseCase.register(command))
                .isInstanceOf(DomainException.class)
                .hasMessage("E-mail já cadastrado");

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldNotRegisterUserWithInvalidRole()
    {
        var command = new RegisterUserCommand(
                "João Silva",
                "joao@empresa.com",
                "senha123",
                null
        );

        assertThatThrownBy(() -> registerUserUseCase.register(command))
                .isInstanceOf(DomainException.class)
                .hasMessage("Perfil inválido");

        verify(userRepository, never()).save(any());
    }
}