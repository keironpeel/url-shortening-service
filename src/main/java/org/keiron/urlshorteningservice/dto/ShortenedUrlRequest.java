package org.keiron.urlshorteningservice.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record ShortenedUrlRequest(
        @NotBlank(message = "URL is required")
        @URL(message = "Please provide a valid URL (e.g: https://youtube.com)")
        String url
) {}
