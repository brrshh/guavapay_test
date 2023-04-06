package by.guavapay.controller;

import by.guavapay.AbstractWebTestConfiguration;
import by.guavapay.domain.Delivery;
import by.guavapay.dto.ParcelCreateDto;
import by.guavapay.dto.ParcelDto;
import by.guavapay.dto.ParcelUpdateAddressDto;
import by.guavapay.dto.ParcelUpdateStatusDto;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ParcelControllerTest extends AbstractWebTestConfiguration {

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_USER", username = "user1")
    void create() throws Exception {
        ParcelCreateDto createParcel = new ParcelCreateDto("www to address");

        when(parcelService.create(createParcel, "user1"))
                .thenReturn(ParcelDto.builder()
                        .id(123)
                        .created("user1")
                        .address("www to address")
                        .status(Delivery.CREATED)
                        .createdAt(OffsetDateTime.now())
                        .build());

        mockMvc.perform(post("/parcel/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\": \"www to address\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(123)))
                .andExpect(jsonPath("$.address", is("www to address")))
                .andExpect(jsonPath("$.created", is("user1")))
                .andExpect(jsonPath("$.courier", is(emptyOrNullString())))
                .andExpect(jsonPath("$.status", is("CREATED")))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void shouldNotCreateByAdmin() throws Exception {
        ParcelCreateDto createParcel = new ParcelCreateDto("www to address");
        reset(parcelService);

        mockMvc.perform(post("/parcel/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(createParcel)))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(parcelService);
    }

    @Test
    void shouldNotCreateByAnonymous() throws Exception {
        mockMvc.perform(post("/parcel/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"address\": \"www to address\"}"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(parcelService);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_USER", username = "test1")
    void getByUser() throws Exception {

        when(parcelService.get(1L, "test1")).thenReturn(
                ParcelDto.builder()
                        .id(1)
                        .address("address")
                        .createdAt(OffsetDateTime.now())
                        .created("test1")
                        .status(Delivery.IN_PROGRESS)
                        .build());

        mockMvc.perform(get("/parcel/v1/get?id=1"))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.address", is("address")))
                .andExpect(jsonPath("$.created", is("test1")))
                .andExpect(jsonPath("$.courier", is(emptyOrNullString())))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_COURIER", username = "courier")
    void getByCourier() throws Exception {

        when(parcelService.getForCourier(1L, "courier")).thenReturn(
                ParcelDto.builder()
                        .id(1)
                        .address("address")
                        .createdAt(OffsetDateTime.now())
                        .created("test1")
                        .courier("courier")
                        .status(Delivery.IN_PROGRESS)
                        .build());

        mockMvc.perform(get("/parcel/v1/get?id=1"))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.address", is("address")))
                .andExpect(jsonPath("$.created", is("test1")))
                .andExpect(jsonPath("$.courier", is("courier")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN", username = "admin")
    void shouldFailedGetByAdmin() throws Exception {
        mockMvc.perform(get("/parcel/v1/get?id=1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(parcelService);
    }

    @Test
    void shouldFailedGetByAnonymous() throws Exception {
        mockMvc.perform(get("/parcel/v1/get?id=1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(parcelService);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_USER", username = "test1")
    void getAllByUser() throws Exception {

        when(parcelService.getAll("test1", Pageable.ofSize(50).withPage(0)))
                .thenReturn(
                        new PageImpl<>(List.of(ParcelDto.builder()
                                .id(1)
                                .address("address")
                                .createdAt(OffsetDateTime.now())
                                .created("test1")
                                .status(Delivery.IN_PROGRESS)
                                .build())));

        mockMvc.perform(get("/parcel/v1/getAll"))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].address", is("address")))
                .andExpect(jsonPath("$.content[0].created", is("test1")))
                .andExpect(jsonPath("$.content[0].courier", is(emptyOrNullString())))
                .andExpect(jsonPath("$.content[0].status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.content[0].createdAt").isNotEmpty());

        verify(parcelService, never()).getAll(any());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN", username = "admin")
    void getAllByAdmin() throws Exception {
        when(parcelService.getAll(Pageable.ofSize(50).withPage(0)))
                .thenReturn(new PageImpl<>(
                        List.of(ParcelDto.builder()
                                .id(1)
                                .address("address")
                                .createdAt(OffsetDateTime.now())
                                .created("test1")
                                .status(Delivery.IN_PROGRESS)
                                .build())));

        mockMvc.perform(get("/parcel/v1/getAll"))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].address", is("address")))
                .andExpect(jsonPath("$.content[0].created", is("test1")))
                .andExpect(jsonPath("$.content[0].courier", is(emptyOrNullString())))
                .andExpect(jsonPath("$.content[0].status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.content[0].createdAt").isNotEmpty());

        verify(parcelService, never()).getAll(any(), any());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_COURIER", username = "courier")
    void getAllByCourier() throws Exception {
        when(parcelService.getAllForCourier("courier", Pageable.ofSize(50).withPage(0)))
                .thenReturn(new PageImpl<>(
                        List.of(ParcelDto.builder()
                                .id(1)
                                .address("address")
                                .createdAt(OffsetDateTime.now())
                                .created("courier")
                                .status(Delivery.IN_PROGRESS)
                                .build())));

        mockMvc.perform(get("/parcel/v1/getAll"))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].address", is("address")))
                .andExpect(jsonPath("$.content[0].created", is("courier")))
                .andExpect(jsonPath("$.content[0].courier", is(emptyOrNullString())))
                .andExpect(jsonPath("$.content[0].status", is("IN_PROGRESS")))
                .andExpect(jsonPath("$.content[0].createdAt").isNotEmpty());

        verify(parcelService, never()).getAll(any(), any());
        verify(parcelService, never()).getAll(any());
    }

    @Test
    void shouldFailedGetAllByAnonymous() throws Exception {
        mockMvc.perform(get("/parcel/v1/getAll"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(parcelService);
    }

    @Test
    void shouldNotUpdateAddressByAnonymous() throws Exception {
        mockMvc.perform(put("/parcel/v1/updateAddress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(parcelService);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN")
    void shouldNotUpdateAddressByAdmin() throws Exception {
        var dto = new ParcelUpdateAddressDto(234L, "updated address");

        mockMvc.perform(put("/parcel/v1/updateAddress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(dto)))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(parcelService);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_COURIER")
    void shouldNotUpdateAddressByCourier() throws Exception {
        var dto = new ParcelUpdateAddressDto(234L, "updated address");

        mockMvc.perform(put("/parcel/v1/updateAddress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(dto)))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(parcelService);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_USER", username = "testNameUpdAddr")
    void updateAddressByUser() throws Exception {
        var dto = new ParcelUpdateAddressDto(234L, "updated address");

        when(parcelService.updateAddress(dto, "testNameUpdAddr")).thenReturn(
                ParcelDto.builder()
                        .id(234)
                        .address("updated address")
                        .createdAt(OffsetDateTime.now())
                        .created("testNameUpdAddr")
                        .status(Delivery.CREATED)
                        .build());

        mockMvc.perform(put("/parcel/v1/updateAddress")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(234)))
                .andExpect(jsonPath("$.address", is("updated address")))
                .andExpect(jsonPath("$.created", is("testNameUpdAddr")))
                .andExpect(jsonPath("$.courier", is(emptyOrNullString())))
                .andExpect(jsonPath("$.status", is("CREATED")))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void shouldNotUpdateStatusByAnonymous() throws Exception {
        mockMvc.perform(put("/parcel/v1/updateStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(parcelService);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_USER", username = "testNameUpdStatus")
    void updateStatusByUser() throws Exception {
        var dto = new ParcelUpdateStatusDto(234L, Delivery.CANCELED);

        when(parcelService.updateStatus(dto.id(), "testNameUpdStatus", dto.status())).thenReturn(
                ParcelDto.builder()
                        .id(234)
                        .address("address")
                        .createdAt(OffsetDateTime.now())
                        .created("testNameUpdStatus")
                        .status(Delivery.CANCELED)
                        .build());

        mockMvc.perform(put("/parcel/v1/updateStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(234)))
                .andExpect(jsonPath("$.address", is("address")))
                .andExpect(jsonPath("$.created", is("testNameUpdStatus")))
                .andExpect(jsonPath("$.courier", is(emptyOrNullString())))
                .andExpect(jsonPath("$.status", is("CANCELED")))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_COURIER", username = "courier")
    void updateStatusByCourier() throws Exception {
        var dto = new ParcelUpdateStatusDto(234L, Delivery.DELIVERED);

        when(parcelService.updateStatusByCourier(dto.id(), "courier", dto.status())).thenReturn(
                ParcelDto.builder()
                        .id(234)
                        .address("address")
                        .createdAt(OffsetDateTime.now())
                        .created("testNameUpdStatus")
                        .courier("courier")
                        .status(Delivery.DELIVERED)
                        .build());

        mockMvc.perform(put("/parcel/v1/updateStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(234)))
                .andExpect(jsonPath("$.address", is("address")))
                .andExpect(jsonPath("$.created", is("testNameUpdStatus")))
                .andExpect(jsonPath("$.courier", is("courier")))
                .andExpect(jsonPath("$.status", is("DELIVERED")))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @WithMockUser(authorities = "SCOPE_ROLE_ADMIN", username = "testNameUpdStatus")
    void updateStatusByAdmin() throws Exception {
        var dto = new ParcelUpdateStatusDto(333L, Delivery.CANCELED);

        when(parcelService.updateStatus(dto.id(), "testNameUpdStatus", dto.status())).thenReturn(
                ParcelDto.builder()
                        .id(333)
                        .address("address")
                        .createdAt(OffsetDateTime.now())
                        .created("testNameUpdStatus")
                        .status(Delivery.CANCELED)
                        .build());

        mockMvc.perform(put("/parcel/v1/updateStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(333)))
                .andExpect(jsonPath("$.address", is("address")))
                .andExpect(jsonPath("$.created", is("testNameUpdStatus")))
                .andExpect(jsonPath("$.courier", is(emptyOrNullString())))
                .andExpect(jsonPath("$.status", is("CANCELED")))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }
}