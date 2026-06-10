package com.polaris.syscare_backend.domain.user;

import java.time.LocalDateTime;
import java.util.UUID;

public record User(UUID id, String email, String passwordHash, UserRole role, boolean active, LocalDateTime createdAt,
                   LocalDateTime updatedAt)
{

}