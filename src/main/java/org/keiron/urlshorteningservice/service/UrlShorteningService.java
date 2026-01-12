package org.keiron.urlshorteningservice.service;

import org.keiron.urlshorteningservice.util.RandomStringGenerator;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    public UrlEntity createShortUrl(String longUrl) {
        String shortCode;

        do {
            shortCode = RandomStringGenerator.generate();
        } while (repository.existsByShortCode(shortCode));

        UrlEntity entity = new UrlEntity();
        entity.setUrl(longUrl);
        entity.setShortCode(shortCode);

        return repository.save(entity);
    }

    @Cacheable("urlEntryByShortCode")
    @Transactional(readOnly = true)
    public UrlEntity getUrlEntry(String shortCode) {
        return repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found"));
    }

    @Transactional
    public UrlEntity updateShortUrl(String shortCode, String newLongUrl) {
        UrlEntity entity = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found"));

        entity.setUrl(newLongUrl);

        return repository.save(entity);
    }

    @Transactional
    public void deleteShortUrl(String shortCode) {
        if (!repository.existsByShortCode(shortCode)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found");
        }

        repository.deleteByShortCode(shortCode);
    }
}
