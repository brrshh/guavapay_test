package by.guavapay.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserCreateDto(@NotEmpty @Email String email,
                            @NotEmpty @Size(min = 1, max = 256) String firstName,
                            @NotEmpty @Size(min = 1, max = 256) String lastName,
                            @NotEmpty @Size(min = 1, max = 256) String password) {
}