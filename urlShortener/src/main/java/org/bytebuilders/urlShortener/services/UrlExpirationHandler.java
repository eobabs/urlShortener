package org.bytebuilders.urlShortener.services;

import org.bytebuilders.urlShortener.data.models.Url;
import org.bytebuilders.urlShortener.data.repositories.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@EnableScheduling
public class UrlExpirationHandler {

    @Autowired
    private UrlRepository urlRepository;


    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredUrls() {
        LocalDateTime now = LocalDateTime.now();

        List<Url> expiredUrls = urlRepository.findAll().stream()
                .filter(url -> url.getExpiresAt() != null && url.getExpiresAt().isBefore(now))
                .collect(Collectors.toList());

        if (!expiredUrls.isEmpty()) {
            urlRepository.deleteAll(expiredUrls);
        }
    }
}