package com.polaris.syscare_backend.domain.patient.vo;

import com.polaris.syscare_backend.domain.shared.exception.DomainException;

public record Phone(String value)
{
    public Phone
    {
        if (value == null || value.isBlank())
        {
            throw new DomainException("Telefone não pode ser vazio");
        }
        // Remove caracteres não numéricos para validação
        String digits = value.replaceAll("\\D", "");
        if (digits.length() < 10 || digits.length() > 11)
        {
            throw new DomainException("Telefone inválido (deve ter 10 ou 11 dígitos)");
        }
    }
}
