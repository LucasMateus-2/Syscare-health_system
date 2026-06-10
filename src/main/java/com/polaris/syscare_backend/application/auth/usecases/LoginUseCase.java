package com.polaris.syscare_backend.application.auth.usecases;

import com.polaris.syscare_backend.application.auth.dto.LoginCommand;
import com.polaris.syscare_backend.application.auth.dto.LoginResponse;

public interface LoginUseCase
{
    LoginResponse execute(LoginCommand command);
}
