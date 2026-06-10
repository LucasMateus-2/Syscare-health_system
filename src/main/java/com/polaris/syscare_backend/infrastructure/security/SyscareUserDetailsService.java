package com.polaris.syscare_backend.infrastructure.security;

import com.polaris.syscare_backend.domain.user.User;
import com.polaris.syscare_backend.domain.user.UserRepository;
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

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        if (!user.active())
        {
            throw new UsernameNotFoundException("Usuário inativo: " + email);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.email())
                .password(user.passwordHash())
                .roles(user.role().name())
                .disabled(!user.active())
                .build();
    }
}
