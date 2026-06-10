package com.polaris.syscare_backend.presenter.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.polaris.syscare_backend.application.auth.dto.LoginCommand;
import com.polaris.syscare_backend.application.auth.dto.LoginResponse;
import com.polaris.syscare_backend.application.auth.usecases.LoginUseCase;
import com.polaris.syscare_backend.domain.shared.exception.GlobalExeptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest
{

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;
    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp()
    {
        loginUseCase = mock(LoginUseCase.class);
        var controller = new AuthController(loginUseCase);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExeptionHandler())
                .build();
    }

    @Test
    void shouldReturnTokenOnValidLogin() throws Exception
    {
        var command = new LoginCommand("ana@email.com", "senha123");
        var response = new LoginResponse("jwt-token-gerado", "ROLE_PATIENT");

        when(loginUseCase.execute(command)).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token-gerado"))
                .andExpect(jsonPath("$.role").value("ROLE_PATIENT"));
    }

    @Test
    void shouldReturn401OnInvalidCredentials() throws Exception
    {
        var command = new LoginCommand("ana@email.com", "senhaerrada");

        when(loginUseCase.execute(command))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isUnauthorized());
    }
}