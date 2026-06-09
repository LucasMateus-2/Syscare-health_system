package com.polaris.syscare_backend.domain.patient;

import com.polaris.syscare_backend.domain.patient.vo.CPF;
import com.polaris.syscare_backend.domain.patient.vo.Email;
import com.polaris.syscare_backend.domain.patient.vo.FullName;
import com.polaris.syscare_backend.domain.patient.vo.Phone;
import com.polaris.syscare_backend.domain.shared.exception.DomainException;

import java.time.LocalDateTime;


public class Patient
{
    private final FullName fullName;
    private final CPF cpf;
    private final Email email;
    private final Phone phone;
    private final LocalDateTime createdAt;
    private PatientStatus status;
    private LocalDateTime updatedAt;

    public Patient(FullName fullName, CPF cpf, Email email, Phone phone)
    {
        this.fullName = fullName;
        this.cpf = cpf;
        this.email = email;
        this.phone = phone;
        this.status = PatientStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void inactivate()
    {
        if (this.status == PatientStatus.INACTIVE)
        {
            throw new DomainException("Paciente já está inativo");
        }
        this.status = PatientStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public Phone getPhone()
    {
        return phone;
    }

    public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt()
    {
        return updatedAt;
    }

    public PatientStatus getStatus()
    {
        return status;
    }

    public FullName getFullName()
    {
        return fullName;
    }

    public CPF getCpf()
    {
        return cpf;
    }

    public Email getEmail()
    {
        return email;
    }
}
