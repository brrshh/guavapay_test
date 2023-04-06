package by.guavapay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ParcelCreateDto(@NotBlank @Size(min = 1, max = 256) String address) {
}