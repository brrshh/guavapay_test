package by.guavapay.repository;

import by.guavapay.AbstractRepositoryTestConfiguration;
import by.guavapay.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserRepositoryTest extends AbstractRepositoryTestConfiguration {

    @Autowired
    UserRepository userRepository;

    @Test
    void updatePasswordOfNotExistedUser() {
        var nonExisted = 1111L;
        assertThatThrownBy(() -> userRepository.updatePassword(nonExisted, "pass"))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @Test
    @Sql(statements = "INSERT INTO users (id, email, first_name, last_name, created, role, password) VALUES (10, 'test@email.cmo', 'Jony', 'Dep', '2023-03-30 09:36:31.000000', 'USER', 'no')")
    @Transactional
    void updatePassword() {
        assertThat(entityManager
                .createNativeQuery("select password from users where id =10 ", String.class)
                .getSingleResult()).hasToString("no");

        assertThat(userRepository.updatePassword(10L, "pass")).isOne();

        assertThat(entityManager
                .createNativeQuery("select password from users where id =10 ", String.class)
                .getSingleResult()).hasToString("pass");
    }

    @Test
    @Sql(statements = "INSERT INTO users (id, email, first_name, last_name, created, role, password) VALUES (11, 'test11@email.cmo', 'Jony', 'Dep', '2023-03-30 09:36:31.000000', 'USER', 'no')")
    void findByEmail() {
        Optional<User> actual = userRepository.findByEmail("test11@email.cmo");
        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(11L);
        assertThat(actual.get().getEmail()).isEqualTo("test11@email.cmo");
    }

    @Test
    void shouldNotFindByNonExistedEmail() {
        Optional<User> actual = userRepository.findByEmail("notExisted");
        assertThat(actual).isEmpty();
    }

    @Test
    @Sql(statements = "INSERT INTO users (id, email, first_name, last_name, created, role, password) VALUES (3, 'test3@email.cmo', 'Jony', 'Dep', '2023-03-30 09:36:31.000000', 'USER', 'ok')")
    void findUserPasswordById() {
        assertThat(userRepository.findUserPasswordById(3L)).isEqualTo("ok");
    }

    @Test
    void shouldNotFindUserPasswordByNonExistedId() {
        assertThat(userRepository.findUserPasswordById(32222222L)).isNull();
    }
}