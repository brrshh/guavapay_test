package by.guavapay.dto;

import by.guavapay.domain.Delivery;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record ParcelDto(long id,
                        String address,
                        String created,
                        String courier,
                        Delivery status,
                        OffsetDateTime createdAt) {
}