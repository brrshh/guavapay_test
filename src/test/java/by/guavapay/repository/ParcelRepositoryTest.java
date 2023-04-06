package by.guavapay.repository;

import by.guavapay.AbstractRepositoryTestConfiguration;
import by.guavapay.domain.Delivery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

class ParcelRepositoryTest extends AbstractRepositoryTestConfiguration {

    @Autowired
    private ParcelRepository parcelRepository;

    @Test
    @Sql(statements = {
            "INSERT INTO users (id, email, first_name, last_name, created, role, password) VALUES (9, 'test9@email.cmo', 'Jony', 'Dep', '2023-03-30 09:36:31.000000', 'USER', 'no')",
            "INSERT INTO parcel (id, address, created_id, created_at, status) VALUES (1, 'test address', 9, now(), 'CREATED')"})
    void findByIdAndCreated_Email() {
        var actual = parcelRepository.findByIdAndCreated_Email(1L, "test9@email.cmo");
        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(1L);
        assertThat(actual.get().getCreated().getEmail()).isEqualTo("test9@email.cmo");
        assertThat(actual.get().getStatus()).isEqualTo(Delivery.CREATED);
    }

    @Test
    @Sql(statements = {
            "INSERT INTO users (id, email, first_name, last_name, created, role, password) VALUES (100, 'test100@email.cmo', 'Jony', 'Dep', '2023-03-30 09:36:31.000000', 'USER', 'no')",
            "INSERT INTO parcel (id, address, created_id, created_at, status) VALUES (11, 'test address', 100, now(), 'CREATED')"})
    void shouldNotFindByIdAndCreated_EmailByNotExistedId() {
        assertThat(parcelRepository.findByIdAndCreated_Email(333L, "test100@email.cmo"))
                .isEmpty();
    }

    @Test
    @Sql(statements = {
            "INSERT INTO users (id, email, first_name, last_name, created, role, password) VALUES (33, 'test33@email.cmo', 'Jony', 'Dep', '2023-03-30 09:36:31.000000', 'USER', 'no')",
            "INSERT INTO parcel (id, address, created_id, created_at, status) VALUES (1111, 'test address', 33, now(), 'CREATED')"})
    void shouldNotFindByIdAndCreated_EmailByNotExistedEmail() {
        assertThat(parcelRepository.findByIdAndCreated_Email(1111L, "notexisrted"))
                .isEmpty();
    }

    @Test
    @Sql(statements = {
            "INSERT INTO users (id, email, first_name, last_name, created, role, password) VALUES (10, 'test10@email.cmo', 'Jony', 'Dep', '2023-03-30 09:36:31.000000', 'USER', 'no')",
            "INSERT INTO users (id, email, first_name, last_name, created, role, password) VALUES (56, 'courier11@email.cmo', 'Jony', 'Dep', '2023-03-30 09:36:31.000000', 'COURIER', 'no')",
            "INSERT INTO parcel (id, address, created_id, courier_id, created_at, status) VALUES (111, 'test address', 10, 56, now(), 'IN_PROGRESS')"})
    void findByIdAndCourier_Email() {
        var actual = parcelRepository.findByIdAndCourier_Email(111L, "courier11@email.cmo");
        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(111L);
        assertThat(actual.get().getCourier().getEmail()).isEqualTo("courier11@email.cmo");
        assertThat(actual.get().getStatus()).isEqualTo(Delivery.IN_PROGRESS);
    }

    @Test
    @Sql(statements = {
            "INSERT INTO users (id, email, first_name, last_name, created, role, password) VALUES (12, 'test12@email.cmo', 'Jony', 'Dep', '2023-03-30 09:36:31.000000', 'USER', 'no')",
            "INSERT INTO parcel (id, address, created_id, created_at, status) VALUES (51, 'test address', 12, now(), 'CREATED')",
            "INSERT INTO parcel (id, address, created_id, created_at, status) VALUES (52, 'test address2', 12,now(), 'IN_PROGRESS')"})
    void findAllByCreated_Email() {
        var actual = parcelRepository.findAllByCreated_Email("test12@email.cmo", Pageable.ofSize(50));
        assertThat(actual).hasSize(2);
        assertThat(actual).extracting("id").contains(51L, 52L);
    }

    @Test
    @Sql(statements = {
            "INSERT INTO users (id, email, first_name, last_name, created, role, password) VALUES (13, 'test13@email.cmo', 'Jony', 'Dep', '2023-03-30 09:36:31.000000', 'USER', 'no')",
            "INSERT INTO users (id, email, first_name, last_name, created, role, password) VALUES (14, 'test14@email.cmo', 'Jony', 'Dep', '2023-03-30 09:36:31.000000', 'COURIER', 'no')",
            "INSERT INTO parcel (id, address, created_id, courier_id, created_at, status) VALUES (113, 'test address', 13, 14, now(), 'CREATED')",
            "INSERT INTO parcel (id, address, created_id, courier_id, created_at, status) VALUES (114, 'test address2', 13, 14,now(), 'IN_PROGRESS')"})
    void findAllByCourier_Email() {
        var actual = parcelRepository.findAllByCourier_Email("test14@email.cmo", Pageable.ofSize(50));
        assertThat(actual).hasSize(2);
        assertThat(actual).extracting("id").contains(113L, 114L);
    }
}