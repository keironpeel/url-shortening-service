package org.keiron.urlshorteningservice.controller;

import org.junit.jupiter.api.Test;
import org.keiron.urlshorteningservice.entity.UrlEntity;
import org.keiron.urlshorteningservice.service.UrlShorteningService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShortenController.class)
public class ShortenControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlShorteningService urlService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testShortenUrlWithValidUrl() throws Exception {
        String LONG_URL = "https://youtube.com";

        // Mock Entity
        UrlEntity mockEntity = new UrlEntity();
        mockEntity.setUrl(LONG_URL);
        mockEntity.setShortCode("abc123");
        ReflectionTestUtils.setField(mockEntity, "id", 1);

        Mockito.when(urlService.createShortUrl(anyString())).thenReturn(mockEntity);

        Map<String, String> body = Map.of("url", LONG_URL);

        mockMvc.perform(post("/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body))).andExpect(status().is(201));
    }

    @Test
    void testShortenUrlWithNoUrlProvided() throws Exception {
        mockMvc.perform(post("/shorten")).andExpect(status().is(400));
    }

    @Test
    void testShortenUrlWithInvalidUrl() throws Exception {
        Map<String, String> body = Map.of("url", "Hello world!");

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))).andExpect(status().is(400));
    }
}
