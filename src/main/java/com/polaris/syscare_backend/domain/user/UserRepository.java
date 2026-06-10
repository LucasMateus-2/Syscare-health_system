package com.polaris.syscare_backend.domain.user;

import java.util.Optional;

public interface UserRepository
{
    Optional<User> findByEmail(String email);

    void save(User user);
}