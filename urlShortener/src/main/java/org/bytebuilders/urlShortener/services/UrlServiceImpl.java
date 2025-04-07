package org.bytebuilders.urlShortener.services;

import org.bytebuilders.urlShortener.data.models.Url;
import org.bytebuilders.urlShortener.data.repositories.UrlRepository;
import org.bytebuilders.urlShortener.dtos.UrlDto;
import org.bytebuilders.urlShortener.exceptions.UrlNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UrlServiceImpl implements UrlService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    @Autowired
    private UrlRepository urlRepository;


    public UrlDto createShortUrl(String originalUrl, Integer validityDays) {
        String shortCode = generateUniqueShortCode();

        LocalDateTime expiresAt = null;
        if (validityDays != null && validityDays > 0) {
            expiresAt = LocalDateTime.now().plusDays(validityDays);
        }

        Url url = new Url(originalUrl, shortCode, LocalDateTime.now(), expiresAt, 0);

        Url savedUrl = urlRepository.save(url);
        return convertToDto(savedUrl);
    }

    public UrlDto getUrlByShortCode(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for short code: " + shortCode));

        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlNotFoundException("URL has expired");
        }

        return convertToDto(url);
    }

    public UrlDto incrementClickCount(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for short code: " + shortCode));

        url.setClickCount(url.getClickCount() + 1);
        Url updatedUrl = urlRepository.save(url);

        return convertToDto(updatedUrl);
    }

    public List<UrlDto> getAllUrls() {
        return urlRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private String generateUniqueShortCode() {
        String shortCode;
        do {
            shortCode = generateRandomCode();
        } while (urlRepository.existsByShortCode(shortCode));

        return shortCode;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

    private UrlDto convertToDto(Url url) {
        return new UrlDto(
                url.getId(),
                url.getOriginalUrl(),
                url.getShortCode(),
                url.getCreatedAt(),
                url.getExpiresAt(),
                url.getClickCount()
        );
    }
}