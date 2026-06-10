package com.polaris.syscare_backend.infrastructure.security;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JwtServiceTest
{
    private final JwtService jwtService = new JwtService("minha-chave-secreta-de-256-bits-para-teste");

    @Test
    void shouldGenerateToken()
    {
        var token = jwtService.generateToken("ana@email.com", "ROLE_PATIENT");

        assertThat(token).isNotBlank();
    }

    @Test
    void shouldExtractEmailFromToken()
    {
        var token = jwtService.generateToken("ana@email.com", "ROLE_PATIENT");

        assertThat(jwtService.extractEmail(token)).isEqualTo("ana@email.com");
    }

    @Test
    void shouldValidateToken()
    {
        var token = jwtService.generateToken("ana@email.com", "ROLE_PATIENT");

        assertThat(jwtService.isTokenValid(token, "ana@email.com")).isTrue();
    }

    @Test
    void shouldRejectTokenWithWrongEmail()
    {
        var token = jwtService.generateToken("ana@email.com", "ROLE_PATIENT");

        assertThat(jwtService.isTokenValid(token, "outro@email.com")).isFalse();
    }
}