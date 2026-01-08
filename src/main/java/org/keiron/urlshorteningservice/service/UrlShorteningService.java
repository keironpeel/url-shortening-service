package org.keiron.urlshorteningservice.service;

import org.keiron.urlshorteningservice.entity.UrlEntity;
import org.keiron.urlshorteningservice.repository.UrlRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UrlShorteningService {

    private final UrlRepository repository;

    public UrlShorteningService(UrlRepository repository) {
        this.repository = repository;
    }

    public UrlEntity createShortUrl(String longUrl) {
        UrlEntity entity = new UrlEntity();
        entity.setUrl(longUrl);
        //TODO: Create random string generation
        entity.setShortCode("abc123");

        return repository.save(entity);
    }

    public UrlEntity getUrlEntry(String shortCode) {
        return repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found"));
    }

    public UrlEntity updateShortUrl(String shortCode, String newLongUrl) {
        UrlEntity entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found"));

        entity.setUrl(newLongUrl);

        return repository.save(entity);
    }
}
