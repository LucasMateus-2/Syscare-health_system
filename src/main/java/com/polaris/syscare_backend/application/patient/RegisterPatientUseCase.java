package com.polaris.syscare_backend.application.patient;

import com.polaris.syscare_backend.application.patient.dto.RegisterPatientCommand;
import com.polaris.syscare_backend.application.patient.gateway.PatientGateway;
import com.polaris.syscare_backend.domain.patient.Patient;
import com.polaris.syscare_backend.domain.patient.vo.CPF;
import com.polaris.syscare_backend.domain.patient.vo.Email;
import com.polaris.syscare_backend.domain.patient.vo.FullName;
import com.polaris.syscare_backend.domain.patient.vo.Phone;
import com.polaris.syscare_backend.domain.shared.exception.DomainException;

public class RegisterPatientUseCase
{
    private final PatientGateway gateway;

    public RegisterPatientUseCase(PatientGateway gateway)
    {
        this.gateway = gateway;
    }

    public void execute(RegisterPatientCommand command)
    {
        var cpf = new CPF(command.cpf());

        if (gateway.existsByCpf(cpf))
        {
            throw new DomainException("CPF já cadastrado");
        }

        var patient = new Patient(
                new FullName(command.fullName()),
                cpf,
                new Email(command.email()),
                new Phone(command.phone())
        );

        gateway.save(patient);
    }
}
