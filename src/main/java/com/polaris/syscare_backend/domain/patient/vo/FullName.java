package com.polaris.syscare_backend.domain.patient.vo;

import com.polaris.syscare_backend.domain.shared.exception.DomainException;

public record FullName(String value)
{
    public FullName
    {
        if (value == null || value.isBlank())
        {
            throw new DomainException("Nome inválido");
        }
        String trimmed = value.trim();
        String[] parts = trimmed.split("\\s+");

        if (parts.length < 2)
        {
            throw new DomainException("Nome completo obrigatório");
        }
        if (trimmed.matches(".*\\d.*"))
        {
            throw new DomainException("Nome não pode conter números");
        }
    }
}
