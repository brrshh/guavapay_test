package by.guavapay.service;

import by.guavapay.domain.Delivery;
import by.guavapay.domain.Parcel;
import by.guavapay.domain.User;
import by.guavapay.dto.ParcelCreateDto;
import by.guavapay.dto.ParcelDto;
import by.guavapay.dto.ParcelUpdateAddressDto;
import by.guavapay.exception.ParcelNotFoundException;
import by.guavapay.exception.UserNotFoundException;
import by.guavapay.repository.ParcelRepository;
import by.guavapay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParcelService {

    private final ParcelRepository parcelRepository;

    private final UserRepository userRepository;

    @Transactional
    public ParcelDto create(ParcelCreateDto dto, String created) {

        var createdUser = userRepository.findByEmail(created)
                .orElseThrow(() -> new UserNotFoundException("Unable to find user : " + created));

        var createdParcel = parcelRepository.save(Parcel.builder()
                .address(dto.address())
                .createdAt(OffsetDateTime.now())
                .status(Delivery.CREATED)
                .created(createdUser)
                .build());

        return getParcelDto(createdParcel);
    }

    @Transactional
    public ParcelDto updateAddress(ParcelUpdateAddressDto toUpdate, String created) {
        var parcelToUpdate = findByIdAndCreated(toUpdate.id(), created);

        if (Delivery.CREATED != parcelToUpdate.getStatus()) {
            throw new IllegalStateException("Unable to change address for parcel in progress");
        }

        parcelToUpdate.setAddress(toUpdate.address());

        var updated = parcelRepository.save(parcelToUpdate);

        return getParcelDto(updated);
    }

    @Transactional
    public ParcelDto updateStatus(Long id, String created, Delivery newStatus) {
        var parcelToUpdate = findByIdAndCreated(id, created);
        if (parcelToUpdate.getStatus() != Delivery.CREATED || Delivery.CANCELED != newStatus) {
            throw new IllegalStateException("Unable to cancel status for parcel in progress.");
        }

        parcelToUpdate.setStatus(newStatus);

        var updated = parcelRepository.save(parcelToUpdate);

        return getParcelDto(updated);
    }

    @Transactional
    public ParcelDto updateStatusByCourier(Long id, String courier, Delivery newStatus) {
        var parcelToUpdate = findByIdAndCourier(id, courier);

        if (parcelToUpdate.getStatus() != Delivery.IN_PROGRESS || Delivery.DELIVERED != newStatus) {
            throw new IllegalStateException("Unable to set delivery status for parcel which is not in progress.");
        }

        parcelToUpdate.setStatus(newStatus);

        var updated = parcelRepository.save(parcelToUpdate);

        return getParcelDto(updated);
    }

    @Transactional(readOnly = true)
    public ParcelDto get(long id, String name) {
        return getParcelDto(findByIdAndCreated(id, name));
    }

    @Transactional(readOnly = true)
    public Page<ParcelDto> getAll(String name, Pageable pageable) {
        return parcelRepository.findAllByCreated_Email(name, pageable)
                .map(this::getParcelDto);
    }

    private Parcel findByIdAndCreated(Long id, String created) {
        return parcelRepository.findByIdAndCreated_Email(id, created)
                .orElseThrow(() -> new ParcelNotFoundException(id, created));
    }

    @Transactional(readOnly = true)
    public Page<ParcelDto> getAll(Pageable pageable) {
        return parcelRepository.findAll(pageable)
                .map(this::getParcelDto);
    }

    @Transactional
    public Page<ParcelDto> getAllForCourier(String name, Pageable pageable) {
        return parcelRepository.findAllByCourier_Email(name, pageable)
                .map(this::getParcelDto);
    }

    private String getUserIdAndName(User user) {
        return Optional.ofNullable(user)
                .map(u -> u.getFirstName() + "_" + u.getId())
                .orElse(null);
    }

    public ParcelDto getForCourier(long id, String name) {
        return parcelRepository.findByIdAndCourier_Email(id, name)
                .map(this::getParcelDto)
                .orElseThrow(() -> new ParcelNotFoundException(id, name));
    }

    private Parcel findByIdAndCourier(Long id, String courier) {
        return parcelRepository.findByIdAndCourier_Email(id, courier)
                .orElseThrow(() -> new ParcelNotFoundException(id, courier));
    }

    private ParcelDto getParcelDto(Parcel createdParcel) {
        return new ParcelDto(createdParcel.getId(),
                createdParcel.getAddress(),
                getUserIdAndName(createdParcel.getCreated()),
                getUserIdAndName(createdParcel.getCourier()),
                createdParcel.getStatus(),
                createdParcel.getCreatedAt());
    }
}