package by.guavapay.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("by.guavapay.repository")
public class JPAConfiguration {
}