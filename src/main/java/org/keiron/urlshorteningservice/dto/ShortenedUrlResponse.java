package org.keiron.urlshorteningservice.dto;

import java.time.LocalDateTime;

public record ShortenedUrlResponse(
        int id,
        String url,
        String shortCode,
        LocalDateTime createAt,
        LocalDateTime updatedAt
) {}
