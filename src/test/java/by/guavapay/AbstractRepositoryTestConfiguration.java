package by.guavapay;

import by.guavapay.config.SecurityConfiguration;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration"
})
@MockBean({SecurityConfiguration.class, JwtEncoder.class, PasswordEncoder.class, WebSecurityConfiguration.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Sql(statements = {"DELETE FROM parcel", "DELETE FROM users"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AbstractRepositoryTestConfiguration {

    @Autowired
    protected EntityManager entityManager;

    private final static PostgreSQLContainer postgres;

    static {
        postgres = (PostgreSQLContainer) new PostgreSQLContainer(PostgreSQLContainer.IMAGE)
                .withDatabaseName("guawapay_test")
                .withUsername("guawapay_test")
                .withPassword("guawapay_test")
                .withReuse(true)
                .withExposedPorts(5432);

        postgres.start();
    }

    @DynamicPropertySource
    static void updateProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}