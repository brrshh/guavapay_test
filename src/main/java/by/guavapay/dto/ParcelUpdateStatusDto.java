package by.guavapay.dto;

import by.guavapay.domain.Delivery;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ParcelUpdateStatusDto(@NotNull @Positive Long id, @NotNull Delivery status) {
}