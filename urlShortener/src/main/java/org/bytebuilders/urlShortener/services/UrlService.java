package org.bytebuilders.urlShortener.services;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.bytebuilders.urlShortener.dtos.UrlDto;

import java.util.List;

public interface UrlService {
    List<UrlDto> getAllUrls();

    UrlDto getUrlByShortCode(String shortCode);

    UrlDto incrementClickCount(String shortCode);

    UrlDto createShortUrl(String originalUrl, Integer validityDays);
}
