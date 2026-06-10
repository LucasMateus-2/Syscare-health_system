package com.polaris.syscare_backend.application.auth.services;

import com.polaris.syscare_backend.application.auth.records.RegisterUserCommand;
import com.polaris.syscare_backend.application.auth.usecases.RegisterUserUsecase;
import com.polaris.syscare_backend.domain.shared.exception.DomainException;
import com.polaris.syscare_backend.domain.user.User;
import com.polaris.syscare_backend.domain.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegisterUserService implements RegisterUserUsecase
{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserService(UserRepository userRepository,
                               PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void register(RegisterUserCommand command)
    {

        if (command.role() == null)
        {
            throw new DomainException("Perfil inválido");
        }

        if (userRepository.findByEmail(command.email()).isPresent())
        {
            throw new DomainException("E-mail já cadastrado");
        }

        var now = LocalDateTime.now();

        var user = new User(
                UUID.randomUUID(),
                command.email(),
                passwordEncoder.encode(command.password()),
                command.role(),
                true,
                now,
                now
        );

        userRepository.save(user);
    }
}
