package by.guavapay.controller;

import by.guavapay.dto.AssignCourierDto;
import by.guavapay.dto.UserCreateDto;
import by.guavapay.service.UserService;
import by.guavapay.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user", description = "The end point of user creation depends on the current user")
    public void createUser(@Valid @RequestBody UserCreateDto dto, Authentication authentication) throws AccessDeniedException {
        if (authentication == null) {
            userService.createUser(dto);
        } else if (SecurityUtils.isUserInRole("SCOPE_ROLE_ADMIN", authentication.getAuthorities())) {
            userService.createCourier(dto);
        } else {
            throw new AccessDeniedException("Permission denied");
        }
    }


    @PreAuthorize("hasRole('SCOPE_ROLE_ADMIN')")
    @PutMapping("/assign")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Assign courier user", description = "The end point of assigning courier user to the parcel")
    public void assignCourier(@Valid @RequestBody AssignCourierDto dto) {
        userService.assignCourier(dto.courierId(), dto.parcelId());
    }
}