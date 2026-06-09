package com.polaris.syscare_backend.application.patient;

import com.polaris.syscare_backend.application.patient.dto.RegisterPatientCommand;
import com.polaris.syscare_backend.application.patient.gateway.PatientGateway;
import com.polaris.syscare_backend.domain.patient.Patient;
import com.polaris.syscare_backend.domain.shared.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class RegisterPatientUseCaseTest
{

    private PatientGateway gateway;
    private RegisterPatientUseCase useCase;

    @BeforeEach
    void setUp()
    {
        gateway = mock(PatientGateway.class);
        useCase = new RegisterPatientUseCase(gateway);
    }

    @Test
    void shouldRegisterPatientSuccessfully()
    {
        var command = new RegisterPatientCommand(
                "Ana Souza",
                "529.982.247-25",
                "ana@email.com",
                "(11)99999-8888"
        );

        when(gateway.existsByCpf(any())).thenReturn(false);

        useCase.execute(command);

        verify(gateway, times(1)).save(any(Patient.class));
    }

    @Test
    void shouldNotRegisterPatientWithDuplicateCpf()
    {
        var command = new RegisterPatientCommand(
                "Ana Souza",
                "529.982.247-25",
                "ana@email.com",
                "(11)99999-8888"
        );

        when(gateway.existsByCpf(any())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(DomainException.class)
                .hasMessage("CPF já cadastrado");
    }
}
