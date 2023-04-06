package by.guavapay.service;

import by.guavapay.domain.Delivery;
import by.guavapay.domain.Parcel;
import by.guavapay.domain.Role;
import by.guavapay.domain.User;
import by.guavapay.dto.UserCreateDto;
import by.guavapay.exception.ParcelNotFoundException;
import by.guavapay.exception.UserNotFoundException;
import by.guavapay.repository.ParcelRepository;
import by.guavapay.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;

import static by.guavapay.domain.Role.COURIER;
import static by.guavapay.domain.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ParcelRepository parcelRepository;

    UserService userService;
    @Captor
    ArgumentCaptor<User> savedUserCaptor;

    @Captor
    ArgumentCaptor<Parcel> savedParcelCaptor;


    @BeforeEach
    void initMocks() {
        userService = new UserService(userRepository, parcelRepository, NoOpPasswordEncoder.getInstance());
    }

    @Test
    void createUser() {
        var toCreate = new UserCreateDto("test@emqil.com", "firstName", "lastName", "password");
        when(userRepository.save(any(User.class))).thenReturn(User.builder()
                .id(123L)
                .build());

        userService.createUser(toCreate);

        verify(userRepository, atLeastOnce())
                .save(savedUserCaptor.capture());
        var actual = savedUserCaptor.getValue();
        assertThat(actual.getCreated()).isBefore(OffsetDateTime.now()).isAfter(OffsetDateTime.now().minusMinutes(1));
        assertThat(actual.getId()).isNull();
        assertThat(actual.getEmail()).isEqualTo(toCreate.email());
        assertThat(actual.getFirstName()).isEqualTo(toCreate.firstName());
        assertThat(actual.getLastName()).isEqualTo(toCreate.lastName());
        assertThat(actual.getRole()).isEqualTo(USER);

        verify(userRepository, atLeastOnce()).updatePassword(123L, toCreate.password());
    }

    @Test
    void createCourier() {
        var toCreate = new UserCreateDto("test@emqil.com", "courier", "lastName", "password");
        when(userRepository.save(any(User.class))).thenReturn(User.builder()
                .id(123L)
                .build());

        userService.createCourier(toCreate);

        verify(userRepository, atLeastOnce())
                .save(savedUserCaptor.capture());
        var actual = savedUserCaptor.getValue();
        assertThat(actual.getCreated()).isBefore(OffsetDateTime.now()).isAfter(OffsetDateTime.now().minusMinutes(1));
        assertThat(actual.getId()).isNull();
        assertThat(actual.getEmail()).isEqualTo(toCreate.email());
        assertThat(actual.getFirstName()).isEqualTo(toCreate.firstName());
        assertThat(actual.getLastName()).isEqualTo(toCreate.lastName());
        assertThat(actual.getRole()).isEqualTo(COURIER);

        verify(userRepository, atLeastOnce()).updatePassword(123L, toCreate.password());
    }

    @Test
    void assignCourier() {
        var courier = User.builder()
                .id(1L)
                .role(COURIER)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(courier));

        when(parcelRepository.findById(2L)).thenReturn(Optional.ofNullable(Parcel.builder()
                .status(Delivery.CREATED)
                .id(2L)
                .build()));

        userService.assignCourier(1L, 2L);
        verify(parcelRepository, atLeastOnce()).save(savedParcelCaptor.capture());

        var actual = savedParcelCaptor.getValue();
        assertThat(actual.getId()).isEqualTo(2L);
        assertThat(actual.getStatus()).isEqualTo(Delivery.IN_PROGRESS);
        assertThat(actual.getCourier()).isEqualTo(courier);
    }

    @ParameterizedTest
    @EnumSource(names = {"IN_PROGRESS", "DELIVERED", "CANCELED"})
    void shouldNotAssignCourierForIncorrectParcelStatus(Delivery wrongStatus) {
        var courier = User.builder()
                .id(1L)
                .role(COURIER)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(courier));

        when(parcelRepository.findById(2L)).thenReturn(Optional.ofNullable(Parcel.builder()
                .status(wrongStatus)
                .id(2L)
                .build()));

        assertThatThrownBy(() -> userService.assignCourier(1L, 2L))
                .isInstanceOf(IllegalStateException.class);

        verify(parcelRepository, never()).save(any());
    }

    @Test
    void shouldNotAssignCourierIfParcelNotFound() {
        var courier = User.builder()
                .id(1L)
                .role(COURIER)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(courier));

        when(parcelRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.assignCourier(1L, 2L))
                .isInstanceOf(ParcelNotFoundException.class);

        verify(parcelRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(names = {"ADMIN", "USER"})
    void shouldNotAssignCourierIfUserIsNotCourier(Role wrongRole) {
        var courier = User.builder()
                .id(1L)
                .role(wrongRole)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(courier));

        assertThatThrownBy(() -> userService.assignCourier(1L, 2L))
                .isInstanceOf(IllegalArgumentException.class);

        verify(parcelRepository, never()).save(any());
    }

    @Test
    void shouldNotAssignCourierIfCourierIsNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.assignCourier(1L, 2L))
                .isInstanceOf(UserNotFoundException.class);

        verify(parcelRepository, never()).save(any());
    }
}