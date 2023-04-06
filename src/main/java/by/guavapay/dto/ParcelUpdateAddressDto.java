package by.guavapay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ParcelUpdateAddressDto(@NotNull @Positive Long id, @NotBlank @Size(min = 1, max = 256) String address) {
}