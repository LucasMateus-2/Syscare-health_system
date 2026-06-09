package com.polaris.syscare_backend.application.patient.integrationTests;

import com.polaris.syscare_backend.application.patient.RegisterPatientUseCase;
import com.polaris.syscare_backend.application.patient.dto.RegisterPatientCommand;
import com.polaris.syscare_backend.infrastructure.persistence.user.UserEntity;
import com.polaris.syscare_backend.infrastructure.persistence.user.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Disabled("RegisterPatientUseCase ainda não cria User nem codifica senha")
class RegisterPatientUseCaseIntegrationTest
{

    @Autowired
    private RegisterPatientUseCase useCase;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldEncodePasswordWhenRegisteringPatient()
    {
        var command = new RegisterPatientCommand(
                "Ana Souza",
                "529.982.247-25",
                "ana@email.com",
                "(11)99999-8888"
        );

        useCase.execute(command);

        UserEntity user = userRepository.findByEmail("ana@email.com").orElseThrow();
        assertThat(user.getPasswordHash())
                .isNotEqualTo("secret123")
                .startsWith("$2a$");
    }
}
