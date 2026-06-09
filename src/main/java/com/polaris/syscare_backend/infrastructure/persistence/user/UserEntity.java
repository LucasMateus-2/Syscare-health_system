package com.polaris.syscare_backend.infrastructure.persistence.user;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // ou GenerationType.AUTO
    private UUID id;

    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private boolean active = true;   // não final, com valor padrão

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")      // ← corrigido
    private LocalDateTime updatedAt;
}