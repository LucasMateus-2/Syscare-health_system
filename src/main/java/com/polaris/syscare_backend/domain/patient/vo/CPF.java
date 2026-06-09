package com.polaris.syscare_backend.domain.patient.vo;

import com.polaris.syscare_backend.domain.shared.exception.DomainException;

public record CPF(String value)
{
    public CPF
    {
        value = value.replaceAll("[.\\-]", "");
        if (!isValid(value))
        {
            throw new DomainException("CPF inválido");
        }
    }

    private static boolean isValid(String cpf)
    {
        if (cpf == null || !cpf.matches("\\d{11}")) return false;
        if (cpf.chars().distinct().count() == 1) return false; // 00000000000

        int sum = 0;
        for (int i = 0; i < 9; i++) sum += (cpf.charAt(i) - '0') * (10 - i);
        int first = 11 - (sum % 11);
        if (first >= 10) first = 0;

        sum = 0;
        for (int i = 0; i < 10; i++) sum += (cpf.charAt(i) - '0') * (11 - i);
        int second = 11 - (sum % 11);
        if (second >= 10) second = 0;

        return first == (cpf.charAt(9) - '0') && second == (cpf.charAt(10) - '0');
    }
}
