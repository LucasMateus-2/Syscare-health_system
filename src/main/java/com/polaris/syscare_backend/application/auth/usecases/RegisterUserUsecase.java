package com.polaris.syscare_backend.application.auth.usecases;

import com.polaris.syscare_backend.application.auth.records.RegisterUserCommand;

public interface RegisterUserUsecase
{
    void register(RegisterUserCommand command);
}
