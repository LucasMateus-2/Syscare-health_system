package com.polaris.syscare_backend.infrastructure.security;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig
{
    static
    {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("DB_USER", dotenv.get("DB_USER"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
        System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
    }
}
