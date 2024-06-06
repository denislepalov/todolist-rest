package lepdv.todolistrest.config;

import lepdv.todolistrest.TodolistRestApplication;
import lepdv.todolistrest.util.AuthUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;


@EnableJpaAuditing
@EnableEnversRepositories(basePackageClasses = TodolistRestApplication.class)
@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of(AuthUser.getAuthUsername());
    }


}
