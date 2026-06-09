package com.polaris.syscare_backend.domain.patient.vo;

import com.polaris.syscare_backend.domain.shared.exception.DomainException;

import java.util.regex.Pattern;

public record Email(String value)
{
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email
    {
        if (value == null || !EMAIL_PATTERN.matcher(value).matches())
        {
            throw new DomainException("Email inválido");
        }
    }
}
