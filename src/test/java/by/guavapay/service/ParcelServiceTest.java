package by.guavapay.service;

import by.guavapay.domain.Delivery;
import by.guavapay.domain.Parcel;
import by.guavapay.domain.Role;
import by.guavapay.domain.User;
import by.guavapay.dto.ParcelCreateDto;
import by.guavapay.dto.ParcelUpdateAddressDto;
import by.guavapay.exception.ParcelNotFoundException;
import by.guavapay.exception.UserNotFoundException;
import by.guavapay.repository.ParcelRepository;
import by.guavapay.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParcelServiceTest {

    @Mock
    ParcelRepository parcelRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ParcelService parcelService;

    @Test
    void create() {
        var dto = new ParcelCreateDto("address 1");
        User creator = User.builder()
                .id(1L)
                .firstName("FirstName")
                .build();
        when(userRepository.findByEmail("creator")).thenReturn(Optional.ofNullable(creator));

        when(parcelRepository.save(Mockito.any()))
                .thenReturn(Parcel.builder()
                        .id(123L)
                        .created(creator)
                        .createdAt(OffsetDateTime.now())
                        .status(Delivery.CREATED)
                        .address(dto.address())
                        .build());

        var actual = parcelService.create(dto, "creator");

        assertThat(actual.created()).isEqualTo("FirstName_1");
        assertThat(actual.createdAt()).isNotNull();
        assertThat(actual.address()).isEqualTo("address 1");
        assertThat(actual.courier()).isNull();
        assertThat(actual.status()).isEqualTo(Delivery.CREATED);

    }

    @Test
    void shouldFailCreateForNotExistedUser() {
        var dto = new ParcelCreateDto("address 1");
        when(userRepository.findByEmail("creator")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parcelService.create(dto, "creator"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void updateAddress() {
        var dto = new ParcelUpdateAddressDto(123L, "new Address");

        Parcel oldParcel = Parcel.builder()
                .id(123L)
                .createdAt(OffsetDateTime.now())
                .status(Delivery.CREATED)
                .address("old address")
                .build();

        when(parcelRepository.findByIdAndCreated_Email(123L, "created"))
                .thenReturn(Optional.ofNullable(oldParcel));
        oldParcel.setAddress("new Address");

        when(parcelRepository.save(oldParcel)).thenReturn(oldParcel);

        var actual = parcelService.updateAddress(dto, "created");

        assertThat(actual.address()).isEqualTo("new Address");
    }

    @Test
    void shouldNotUpdateAddressIfParcelNotExisted() {
        var dto = new ParcelUpdateAddressDto(123L, "new Address");

        when(parcelRepository.findByIdAndCreated_Email(123L, "created"))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> parcelService.updateAddress(dto, "created"))
                .isInstanceOf(ParcelNotFoundException.class);

        verify(parcelRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(names = {"IN_PROGRESS", "DELIVERED", "CANCELED"})
    void shouldNotUpdateAddressIfStatusIncorrect(Delivery incorrectStatus) {
        var dto = new ParcelUpdateAddressDto(123L, "new Address");

        Parcel oldParcel = Parcel.builder()
                .id(123L)
                .createdAt(OffsetDateTime.now())
                .status(incorrectStatus)
                .address("old address")
                .build();

        when(parcelRepository.findByIdAndCreated_Email(123L, "created"))
                .thenReturn(Optional.ofNullable(oldParcel));
        oldParcel.setAddress("new Address");

        assertThatThrownBy(() -> parcelService.updateAddress(dto, "created"))
                .isInstanceOf(IllegalStateException.class);

        verify(parcelRepository, never()).save(any());
    }

    @Test
    void updateStatus() {
        Parcel oldParcel = Parcel.builder()
                .id(123L)
                .createdAt(OffsetDateTime.now())
                .status(Delivery.CREATED)
                .address("old address")
                .build();

        when(parcelRepository.findByIdAndCreated_Email(123L, "created"))
                .thenReturn(Optional.ofNullable(oldParcel));


        var updated = Parcel.builder()
                .id(123L)
                .createdAt(oldParcel.getCreatedAt())
                .status(Delivery.CANCELED)
                .address("old address")
                .build();
        when(parcelRepository.save(updated)).thenReturn(updated);

        var actual = parcelService.updateStatus(123L, "created", Delivery.CANCELED);

        assertThat(actual.status()).isEqualTo(Delivery.CANCELED);
    }

    @Test
    void shouldNotUpdateStatusIfParcelNotExisted() {
        when(parcelRepository.findByIdAndCreated_Email(123L, "created"))
                .thenReturn(Optional.empty());


        assertThatThrownBy(() -> parcelService.updateStatus(123L, "created", Delivery.CANCELED))
                .isInstanceOf(ParcelNotFoundException.class);

        verify(parcelRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(names = {"IN_PROGRESS", "DELIVERED", "CANCELED"})
    void shouldNotUpdateStatusIfParcelStatusIncorrect(Delivery incorrectStatus) {
        Parcel oldParcel = Parcel.builder()
                .id(123L)
                .createdAt(OffsetDateTime.now())
                .status(incorrectStatus)
                .address("old address")
                .build();

        when(parcelRepository.findByIdAndCreated_Email(123L, "created"))
                .thenReturn(Optional.ofNullable(oldParcel));

        assertThatThrownBy(() -> parcelService.updateStatus(123L, "created", Delivery.CANCELED))
                .isInstanceOf(IllegalStateException.class);

        verify(parcelRepository, never()).save(any());
    }

    @Test
    void updateStatusByCourier() {
        User courier = User.builder()
                .id(44L)
                .role(Role.COURIER)
                .firstName("courier Name")
                .build();
        Parcel oldParcel = Parcel.builder()
                .id(144L)
                .createdAt(OffsetDateTime.now())
                .courier(courier)
                .status(Delivery.IN_PROGRESS)
                .address("old address")
                .build();

        when(parcelRepository.findByIdAndCourier_Email(144L, "courier"))
                .thenReturn(Optional.ofNullable(oldParcel));


        var updated = Parcel.builder()
                .id(144L)
                .createdAt(oldParcel.getCreatedAt())
                .courier(courier)
                .status(Delivery.DELIVERED)
                .address("old address")
                .build();
        when(parcelRepository.save(updated)).thenReturn(updated);

        var actual = parcelService.updateStatusByCourier(144L, "courier", Delivery.DELIVERED);

        assertThat(actual.status()).isEqualTo(Delivery.DELIVERED);
    }

    @Test
    void shouldNotUpdateStatusByCourierIfParcelNotExisted() {
        when(parcelRepository.findByIdAndCourier_Email(144L, "courier"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> parcelService.updateStatusByCourier(144L, "courier", Delivery.DELIVERED))
                .isInstanceOf(ParcelNotFoundException.class);

        verify(parcelRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(names = {"CREATED", "IN_PROGRESS", "CANCELED"})
    void shouldNotUpdateStatusByCourierIfStatusIncorrect(Delivery incorrectStatus) {
        User courier = User.builder()
                .id(44L)
                .role(Role.COURIER)
                .firstName("courier Name")
                .build();
        Parcel oldParcel = Parcel.builder()
                .id(144L)
                .createdAt(OffsetDateTime.now())
                .courier(courier)
                .status(Delivery.IN_PROGRESS)
                .address("old address")
                .build();

        when(parcelRepository.findByIdAndCourier_Email(144L, "courier"))
                .thenReturn(Optional.ofNullable(oldParcel));

        assertThatThrownBy(() -> parcelService.updateStatusByCourier(144L, "courier", incorrectStatus))
                .isInstanceOf(IllegalStateException.class);

        verify(parcelRepository, never()).save(any());
    }

    @Test
    void get() {
        //TODO SHOULD BE DONE!!
    }

    @Test
    void getAll() {
        //TODO SHOULD BE DONE!!
    }

    @Test
    void testGetAll() {
        //TODO SHOULD BE DONE!!
    }

    @Test
    void getAllForCourier() {
        //TODO SHOULD BE DONE!!
    }

    @Test
    void getForCourier() {
        //TODO SHOULD BE DONE!!
    }
}