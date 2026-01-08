package org.keiron.urlshorteningservice.controller;

import org.junit.jupiter.api.Test;
import org.keiron.urlshorteningservice.entity.UrlEntity;
import org.keiron.urlshorteningservice.service.UrlShorteningService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShortenController.class)
public class ShortenControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlShorteningService urlService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String LONG_URL = "https://youtube.com";

    private final String UPDATED_LONG_URL = "https://google.com";

    @Test
    void testShortenUrlWithValidUrl() throws Exception {
        UrlEntity mockEntity = new UrlEntity();
        mockEntity.setUrl(LONG_URL);
        mockEntity.setShortCode("abc123");
        ReflectionTestUtils.setField(mockEntity, "id", 1);

        Mockito.when(urlService.createShortUrl(LONG_URL)).thenReturn(mockEntity);

        Map<String, String> body = Map.of("url", LONG_URL);

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.url").value(LONG_URL))
        ;
    }

    @Test
    void testShortenUrlWithNoUrlProvided() throws Exception {
        mockMvc.perform(post("/shorten"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error").value("Required request body is missing"))
        ;
    }

    @Test
    void testShortenUrlWithInvalidUrl() throws Exception {
        Map<String, String> body = Map.of("url", "Hello world!");

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))).andExpect(status().is(400))
                .andExpect(jsonPath("$.url").value("Please provide a valid URL (e.g: https://youtube.com)"))
        ;
    }

    @Test
    void testRetrieveOriginalUrl() throws Exception {
        // Mock Entity
        UrlEntity mockEntity = new UrlEntity();
        mockEntity.setUrl(LONG_URL);
        mockEntity.setShortCode("abc123");
        ReflectionTestUtils.setField(mockEntity, "id", 1);

        Mockito.when(urlService.getUrlEntry("abc123")).thenReturn(mockEntity);

        mockMvc.perform(get("/shorten/abc123"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.url").value(LONG_URL));
    }

    @Test
    void testRetrieveOriginalUrlWithBadShortCode() throws Exception {
        Mockito.when(urlService.getUrlEntry("abc124")).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/shorten/abc124")).andExpect(status().isNotFound());
    }

    @Test
    void testUpdateShortUrl() throws Exception {
        UrlEntity mockEntity = new UrlEntity();
        mockEntity.setUrl(UPDATED_LONG_URL);
        mockEntity.setShortCode("abc123");
        ReflectionTestUtils.setField(mockEntity, "id", 1);

        Mockito.when(urlService.updateShortUrl("abc123", UPDATED_LONG_URL)).thenReturn(mockEntity);

        Map<String, String> body = Map.of("url", UPDATED_LONG_URL);

        mockMvc.perform(put("/shorten/abc123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.url").value(UPDATED_LONG_URL))
        ;
    }

    @Test
    void testUpdateShortUrlWithNoBody() throws Exception {
        mockMvc.perform(put("/shorten/abc123")).andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateShortUrlWithInvalidBody() throws Exception {
        Map<String, String> body = Map.of("url", "Hello world!");

        mockMvc.perform(put("/shorten/abc123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    void testUpdateShortUrlWhenOriginalUrlNotFound() throws Exception {
        Mockito.when(urlService.updateShortUrl("abc124", UPDATED_LONG_URL))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        Map<String, String> body = Map.of("url", UPDATED_LONG_URL);

        mockMvc.perform(put("/shorten/abc124")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteShortUrl() throws Exception {
        mockMvc.perform(delete("/shorten/abc123")).andExpect(status().isNoContent());
    }

    @Test
    void testDeleteShortUrlNotFound() throws Exception {
        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(urlService).deleteShortUrl("abc124");

        mockMvc.perform(delete("/shorten/abc124")).andExpect(status().isNotFound());
    }
}
