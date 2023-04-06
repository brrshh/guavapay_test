package by.guavapay.service;

import by.guavapay.domain.Delivery;
import by.guavapay.domain.Role;
import by.guavapay.domain.User;
import by.guavapay.dto.UserCreateDto;
import by.guavapay.exception.ParcelNotFoundException;
import by.guavapay.exception.UserNotFoundException;
import by.guavapay.repository.ParcelRepository;
import by.guavapay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final ParcelRepository parcelRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(UserCreateDto dto) {
        createUserWithRole(dto, Role.USER);
    }

    @Transactional
    public void createCourier(UserCreateDto dto) {
        createUserWithRole(dto, Role.COURIER);
    }

    @Transactional
    public void assignCourier(Long courierId, Long parcelId) {
        var courier = userRepository.findById(courierId)
                .orElseThrow(() -> new UserNotFoundException(courierId));
        if (Role.COURIER != courier.getRole()) {
            throw new IllegalArgumentException("Unable to assign non COURIER user");
        }

        var parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> new ParcelNotFoundException(parcelId));

        if (Delivery.CREATED != parcel.getStatus()) {
            throw new IllegalStateException("Unable to assign courier for for parcel in progress.");
        }

        parcel.setStatus(Delivery.IN_PROGRESS);
        parcel.setCourier(courier);

        parcelRepository.save(parcel);
    }

    void updatePassword(long userId, String password) {
        userRepository.updatePassword(userId, passwordEncoder.encode(password));
    }

    private void createUserWithRole(UserCreateDto dto, Role role) {
        User saved = userRepository.save(User.builder()
                .email(dto.email())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .role(role)
                .created(OffsetDateTime.now())
                .build());
        updatePassword(saved.getId(), dto.password());
    }
}
