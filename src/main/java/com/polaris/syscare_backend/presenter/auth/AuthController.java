package com.polaris.syscare_backend.presenter.auth;


import com.polaris.syscare_backend.application.auth.dto.LoginCommand;
import com.polaris.syscare_backend.application.auth.dto.LoginResponse;
import com.polaris.syscare_backend.application.auth.usecases.LoginUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController
{
    private final LoginUseCase loginUseCase;

    public AuthController(LoginUseCase loginUseCase)
    {
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginCommand command)
    {
        try
        {
            return ResponseEntity.ok(loginUseCase.execute(command));
        } catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
