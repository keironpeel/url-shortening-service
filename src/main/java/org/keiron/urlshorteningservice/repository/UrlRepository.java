package org.keiron.urlshorteningservice.repository;

import org.keiron.urlshorteningservice.entity.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlEntity, Integer> {
    Optional<UrlEntity> findByShortCode(String shortCode);
}
