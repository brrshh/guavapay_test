package by.guavapay.repository;

import by.guavapay.domain.Parcel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, Long> {

    Optional<Parcel> findByIdAndCreated_Email(long id, String created);

    Optional<Parcel> findByIdAndCourier_Email(long id, String created);

    Page<Parcel> findAllByCreated_Email(String created, Pageable pageable);

    Page<Parcel> findAllByCourier_Email(String email, Pageable pageable);
}
