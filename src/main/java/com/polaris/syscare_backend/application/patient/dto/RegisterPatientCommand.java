package com.polaris.syscare_backend.application.patient.dto;

public record RegisterPatientCommand
        (
                String fullName,
                String cpf,
                String email,
                String phone
        )
{
}
