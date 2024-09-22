package org.foodorder.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;


public record Restaurant(@Schema(accessMode = Schema.AccessMode.READ_ONLY) long id, String name, String address,
                         String cuisineType, String phoneNumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
}