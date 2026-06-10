package com.polaris.syscare_backend.infrastructure.persistence.user;

import com.polaris.syscare_backend.domain.user.User;
import com.polaris.syscare_backend.domain.user.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository
{

    private final UserJpaRepository jpa;

    public UserRepositoryImpl(UserJpaRepository jpa)
    {
        this.jpa = jpa;
    }

    @Override
    public Optional<User> findByEmail(String email)
    {
        return jpa.findByEmail(email).map(UserEntity::toDomain);
    }

    @Override
    public void save(User user)
    {
        jpa.save(UserEntity.fromDomain(user));

    }

}
