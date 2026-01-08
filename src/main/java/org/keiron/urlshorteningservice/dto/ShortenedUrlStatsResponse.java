package org.keiron.urlshorteningservice.dto;

import java.time.LocalDateTime;

public record ShortenedUrlStatsResponse(
        int id,
        String url,
        String shortCode,
        LocalDateTime createAt,
        LocalDateTime updatedAt,
        int accessCount
) {}
