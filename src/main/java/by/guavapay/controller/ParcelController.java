package by.guavapay.controller;

import by.guavapay.dto.ParcelCreateDto;
import by.guavapay.dto.ParcelDto;
import by.guavapay.dto.ParcelUpdateAddressDto;
import by.guavapay.dto.ParcelUpdateStatusDto;
import by.guavapay.service.ParcelService;
import by.guavapay.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/parcel/v1")
@RequiredArgsConstructor
public class ParcelController {

    private final ParcelService parcelService;

    @PreAuthorize(value = "hasRole('SCOPE_ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    @Operation(summary = "Create parcel", description = "Endpoint for creating parcel in the system")
    public ParcelDto create(@RequestBody @Valid ParcelCreateDto createDto, Authentication authentication) {
        return parcelService.create(createDto, authentication.getName());
    }

    @PreAuthorize(value = "hasAnyRole('SCOPE_ROLE_USER', 'SCOPE_ROLE_COURIER')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/get")
    @Operation(summary = "Get parcel info", description = "Endpoint for getting single parcel info by id")
    public ParcelDto get(@RequestParam @Positive @Valid Long id, Authentication authentication) {
        if (SecurityUtils.isCourier(authentication.getAuthorities())) {
            return parcelService.getForCourier(id, authentication.getName());
        } else {
            return parcelService.get(id, authentication.getName());
        }
    }

    @PreAuthorize(value = "hasAnyRole('SCOPE_ROLE_USER', 'SCOPE_ROLE_ADMIN', 'SCOPE_ROLE_COURIER')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/getAll")
    @Operation(summary = "Get parcel info array", description = "Endpoint for getting array of parcel info's by user related to the current user")
    public Page<ParcelDto> getAll(@RequestParam(defaultValue = "50") @Positive @Valid int limit,
                                  @RequestParam(defaultValue = "0") @Positive @Valid int page,
                                  Authentication authentication) {
        Pageable pageable = PageRequest.of(page, limit);

        if (SecurityUtils.isAdmin(authentication.getAuthorities())) {
            return parcelService.getAll(pageable);
        } else if (SecurityUtils.isCourier(authentication.getAuthorities())) {
            return parcelService.getAllForCourier(authentication.getName(), pageable);
        } else {
            return parcelService.getAll(authentication.getName(), pageable);
        }
    }


    @PreAuthorize(value = "hasRole('SCOPE_ROLE_USER')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/updateAddress")
    @Operation(summary = "Update parcel address", description = "Endpoint for updating of parcel address")
    public ParcelDto updateAddress(@RequestBody @Valid ParcelUpdateAddressDto toUpdate, Authentication authentication) {
        return parcelService.updateAddress(toUpdate, authentication.getName());
    }

    @PreAuthorize(value = "hasAnyRole('SCOPE_ROLE_USER', 'SCOPE_ROLE_ADMIN', 'SCOPE_ROLE_COURIER')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/updateStatus")
    @Operation(summary = "Update parcel status", description = "Endpoint for updating of parcel status")
    public ParcelDto updateStatus(@RequestBody @Valid ParcelUpdateStatusDto toUpdate, Authentication authentication) {
        if (SecurityUtils.isCourier(authentication.getAuthorities())) {
            return parcelService.updateStatusByCourier(toUpdate.id(), authentication.getName(), toUpdate.status());
        } else {
            return parcelService.updateStatus(toUpdate.id(), authentication.getName(), toUpdate.status());
        }
    }
}