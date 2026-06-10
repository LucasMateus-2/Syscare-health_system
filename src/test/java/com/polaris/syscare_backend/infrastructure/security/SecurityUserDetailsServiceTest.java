package com.polaris.syscare_backend.infrastructure.security;

import com.polaris.syscare_backend.domain.user.User;
import com.polaris.syscare_backend.domain.user.UserRepository;
import com.polaris.syscare_backend.domain.user.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
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

        var user = new User(
                UUID.randomUUID(),
                "ana@email.com",
                "hashed",
                UserRole.PATIENT,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(repository.findByEmail("ana@email.com"))
                .thenReturn(Optional.of(user));

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

        var user = new User(
                UUID.randomUUID(),
                "inativo@email.com",
                "hashed",
                UserRole.PATIENT,
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(repository.findByEmail("inativo@email.com"))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.loadUserByUsername("inativo@email.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário inativo: inativo@email.com");
    }
}
