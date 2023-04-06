package by.guavapay.repository;

import by.guavapay.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query(value = "update users set password = :password where id = :id", nativeQuery = true)
    int updatePassword(@Param("id") long id, @Param("password") String password);

    Optional<User> findByEmail(String email);

    @Query(value = "select password from users where id = ?", nativeQuery = true)
    String findUserPasswordById(long userId);
}