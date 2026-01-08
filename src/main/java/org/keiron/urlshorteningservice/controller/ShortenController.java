package org.keiron.urlshorteningservice.controller;

import jakarta.validation.Valid;
import org.keiron.urlshorteningservice.dto.ShortenedUrlRequest;
import org.keiron.urlshorteningservice.dto.ShortenedUrlResponse;
import org.keiron.urlshorteningservice.entity.UrlEntity;
import org.keiron.urlshorteningservice.service.UrlShorteningService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shorten")
public class ShortenController {

    private final UrlShorteningService urlShorteningService;

    private ShortenController(UrlShorteningService urlShorteningService) {
        this.urlShorteningService = urlShorteningService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShortenedUrlResponse createShortUrl(
            @Valid @RequestBody ShortenedUrlRequest request
    ) {
        UrlEntity shortenedUrl = urlShorteningService.createShortUrl(request.url());

        return new ShortenedUrlResponse(
                shortenedUrl.getId(),
                shortenedUrl.getUrl(),
                shortenedUrl.getShortCode(),
                shortenedUrl.getCreatedAt(),
                shortenedUrl.getUpdatedAt()
        );
    }

    @GetMapping("/{shortCode}")
    public ShortenedUrlResponse retrieveOriginalUrl(@PathVariable String shortCode) {
        UrlEntity requestedUrl = urlShorteningService.getUrlEntry(shortCode);

        return new ShortenedUrlResponse(
                requestedUrl.getId(),
                requestedUrl.getUrl(),
                requestedUrl.getShortCode(),
                requestedUrl.getCreatedAt(),
                requestedUrl.getUpdatedAt()
        );
    }

    @PutMapping("/{shortCode}")
    public ShortenedUrlResponse updateShortUrl(
            @PathVariable String shortCode,
            @Valid @RequestBody ShortenedUrlRequest request
    ) {
        UrlEntity updatedUrl = urlShorteningService.updateShortUrl(shortCode, request.url());

        return new ShortenedUrlResponse(
                updatedUrl.getId(),
                updatedUrl.getUrl(),
                updatedUrl.getShortCode(),
                updatedUrl.getCreatedAt(),
                updatedUrl.getUpdatedAt()
        );
    }

    @DeleteMapping("/{shortCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShortUrl(@PathVariable String shortCode) {
        urlShorteningService.deleteShortUrl(shortCode);
    }
}
