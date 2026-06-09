package com.polaris.syscare_backend.infrastructure.security;

import com.polaris.syscare_backend.infrastructure.persistence.user.UserEntity;
import com.polaris.syscare_backend.infrastructure.persistence.user.UserRepository;
import com.polaris.syscare_backend.infrastructure.persistence.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SyscareUserDetailsServiceTest
{

    private final UserRepository repository = mock(UserRepository.class);
    private final SyscareUserDetailsService service =
            new SyscareUserDetailsService(repository);

    @Test
    void shouldLoadUserByEmail()
    {
        var entity = new UserEntity();
        entity.setId(UUID.randomUUID());
        entity.setEmail("ana@email.com");
        entity.setPasswordHash("hashed");
        entity.setRole(UserRole.PATIENT);
        entity.setActive(true);

        when(repository.findByEmail("ana@email.com"))
                .thenReturn(Optional.of(entity));

        var userDetails = service.loadUserByUsername("ana@email.com");

        assertThat(userDetails.getUsername()).isEqualTo("ana@email.com");
        assertThat(userDetails.getPassword()).isEqualTo("hashed");
        assertThat(userDetails.getAuthorities())
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));
    }

    @Test
    void shouldThrowWhenUserNotFound()
    {
        when(repository.findByEmail("naoexiste@email.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("naoexiste@email.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário não encontrado: naoexiste@email.com");
    }

    @Test
    void shouldThrowWhenUserIsInactive()
    {
        var entity = new UserEntity();
        entity.setEmail("inativo@email.com");
        entity.setPasswordHash("hashed");
        entity.setRole(UserRole.PATIENT);
        entity.setActive(false);

        when(repository.findByEmail("inativo@email.com"))
                .thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.loadUserByUsername("inativo@email.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário inativo: inativo@email.com");
    }
}