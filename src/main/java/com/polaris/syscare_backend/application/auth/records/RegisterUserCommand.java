package com.polaris.syscare_backend.application.auth.records;

import com.polaris.syscare_backend.domain.user.UserRole;

public record RegisterUserCommand(
        String fullName,
        String email,
        String password,
        UserRole role
)
{
}
