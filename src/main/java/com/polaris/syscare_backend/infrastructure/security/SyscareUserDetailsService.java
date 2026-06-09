package com.polaris.syscare_backend.infrastructure.security;

import com.polaris.syscare_backend.infrastructure.persistence.user.UserEntity;
import com.polaris.syscare_backend.infrastructure.persistence.user.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SyscareUserDetailsService implements UserDetailsService
{
    private final UserRepository userRepository;

    public SyscareUserDetailsService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException
    {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        if (!user.isActive())
        {
            var usuarioInativo = "Usuário inativo: %s".formatted(email);
            throw new UsernameNotFoundException(usuarioInativo);
        }
        return User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .roles(user.getRole().name())
                .build();
    }
}
