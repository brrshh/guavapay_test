package by.guavapay.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AssignCourierDto(@NotNull @Positive Long parcelId,
                               @NotNull @Positive Long courierId) {
}