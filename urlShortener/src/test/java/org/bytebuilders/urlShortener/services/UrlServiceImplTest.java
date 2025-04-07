//package org.bytebuilders.urlShortener.services;
//
//import de.flapdoodle.embed.mongo.MongodExecutable;
//import de.flapdoodle.embed.mongo.MongodStarter;
//import de.flapdoodle.embed.mongo.config.MongodConfig;
//import de.flapdoodle.embed.mongo.config.Net;
//import de.flapdoodle.embed.mongo.distribution.Version;
//import de.flapdoodle.embed.process.runtime.Network;
//import org.bytebuilders.urlShortener.data.models.Url;
//import org.bytebuilders.urlShortener.data.repositories.UrlRepository;
//import org.bytebuilders.urlShortener.dtos.UrlDto;
//import org.bytebuilders.urlShortener.exceptions.UrlNotFoundException;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class UrlServiceImplTest {
//
//    private static final String CONNECTION_STRING = "mongodb://localhost:27017/";
//    private static final int PORT = 27017;
//    private static MongodExecutable mongodExecutable;
//
//    @Autowired
//    private UrlService urlService;
//
//    @Autowired
//    private UrlRepository urlRepository;
//
//    @DynamicPropertySource
//    static void setProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.data.mongodb.uri", () -> String.format(CONNECTION_STRING, "localhost", PORT));
//        registry.add("spring.data.mongodb.database", () -> "test-db");
//    }
//
//    @BeforeEach
//    void setUp() throws Exception {
//        MongodStarter starter = MongodStarter.getDefaultInstance();
//        MongodConfig mongodConfig = MongodConfig.builder()
//                .version(Version.Main.V4_4)
//                .net(new Net("localhost", PORT, Network.localhostIsIPv6()))
//                .build();
//
//        mongodExecutable = starter.prepare(mongodConfig);
//        mongodExecutable.start();
//
//        // Clear database before each test
//        urlRepository.deleteAll();
//    }
//
//    @AfterEach
//    void tearDown() {
//        if (mongodExecutable != null) {
//            mongodExecutable.stop();
//        }
//    }
//
//    @Test
//    void testCreateShortUrl() {
//        // Given
//        String originalUrl = "https://www.example.com";
//        Integer validityDays = 7;
//
//        // When
//        UrlDto urlDto = urlService.createShortUrl(originalUrl, validityDays);
//
//        // Then
//        assertNotNull(urlDto);
//        assertEquals(originalUrl, urlDto.getOriginalUrl());
//        assertNotNull(urlDto.getShortCode());
//        assertEquals(6, urlDto.getShortCode().length());
//        assertNotNull(urlDto.getCreatedAt());
//        assertNotNull(urlDto.getExpiresAt());
//        assertTrue(urlDto.getExpiresAt().isAfter(LocalDateTime.now()));
//        assertEquals(0, urlDto.getClickCount());
//
//        // Verify it's stored in the database
//        assertTrue(urlRepository.existsByShortCode(urlDto.getShortCode()));
//    }
//
//    @Test
//    void testCreateShortUrlWithoutExpiration() {
//        // Given
//        String originalUrl = "https://www.example.com";
//        Integer validityDays = null; // No expiration
//
//        // When
//        UrlDto urlDto = urlService.createShortUrl(originalUrl, validityDays);
//
//        // Then
//        assertNotNull(urlDto);
//        assertEquals(originalUrl, urlDto.getOriginalUrl());
//        assertNull(urlDto.getExpiresAt()); // Should be null as no expiration was set
//    }
//
//    @Test
//    void testGetUrlByShortCode() {
//        // Given
//        String originalUrl = "https://www.example.com";
//        UrlDto createdUrl = urlService.createShortUrl(originalUrl, 7);
//        String shortCode = createdUrl.getShortCode();
//
//        // When
//        UrlDto retrievedUrl = urlService.getUrlByShortCode(shortCode);
//
//        // Then
//        assertNotNull(retrievedUrl);
//        assertEquals(originalUrl, retrievedUrl.getOriginalUrl());
//        assertEquals(shortCode, retrievedUrl.getShortCode());
//    }
//
//    @Test
//    void testGetUrlByInvalidShortCode() {
//        // Given
//        String invalidShortCode = "invalid";
//
//        // When & Then
//        assertThrows(UrlNotFoundException.class, () -> {
//            urlService.getUrlByShortCode(invalidShortCode);
//        });
//    }
//
//    @Test
//    void testGetExpiredUrl() {
//        // Given
//        Url expiredUrl = new Url(
//                "https://www.example.com",
//                "expired",
//                LocalDateTime.now().minusDays(10),
//                LocalDateTime.now().minusDays(3), // Expired 3 days ago
//                0
//        );
//        urlRepository.save(expiredUrl);
//
//        // When & Then
//        assertThrows(UrlNotFoundException.class, () -> {
//            urlService.getUrlByShortCode("expired");
//        });
//    }
//
//    @Test
//    void testIncrementClickCount() {
//        // Given
//        UrlDto urlDto = urlService.createShortUrl("https://www.example.com", 7);
//        String shortCode = urlDto.getShortCode();
//        assertEquals(0, urlDto.getClickCount());
//
//        // When
//        UrlDto updatedUrlDto = urlService.incrementClickCount(shortCode);
//
//        // Then
//        assertEquals(1, updatedUrlDto.getClickCount());
//
//        // Verify database was updated
//        assertEquals(1, urlRepository.findByShortCode(shortCode).get().getClickCount());
//
//        // When incremented again
//        updatedUrlDto = urlService.incrementClickCount(shortCode);
//
//        // Then
//        assertEquals(2, updatedUrlDto.getClickCount());
//    }
//
//    @Test
//    void testGetAllUrls() {
//        // Given
//        urlService.createShortUrl("https://www.example1.com", 7);
//        urlService.createShortUrl("https://www.example2.com", 7);
//        urlService.createShortUrl("https://www.example3.com", 7);
//
//        // When
//        List<UrlDto> allUrls = urlService.getAllUrls();
//
//        // Then
//        assertEquals(3, allUrls.size());
//    }
//
//    @Test
//    void testGenerateUniqueShortCode() {
//        // Create multiple URLs to ensure uniqueness
//        int count = 20;
//        for (int i = 0; i < count; i++) {
//            urlService.createShortUrl("https://www.example.com/" + i, 7);
//        }
//
//        // Get all URLs and check for unique short codes
//        List<UrlDto> allUrls = urlService.getAllUrls();
//        assertEquals(count, allUrls.size());
//
//        // Extract short codes and verify uniqueness
//        List<String> shortCodes = allUrls.stream()
//                .map(UrlDto::getShortCode)
//                .distinct()
//                .toList();
//
//        assertEquals(count, shortCodes.size());
//    }
//}