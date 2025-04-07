package org.bytebuilders.urlShortener.controllers;

import jakarta.validation.Valid;
import org.bytebuilders.urlShortener.dtos.CreateUrlRequest;
import org.bytebuilders.urlShortener.dtos.UrlDto;
import org.bytebuilders.urlShortener.exceptions.UrlNotFoundException;
import org.bytebuilders.urlShortener.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @PostMapping("/api/urls")
    public ResponseEntity<UrlDto> createShortUrl(@Valid @RequestBody CreateUrlRequest request) {
        UrlDto urlDto = urlService.createShortUrl(request.getOriginalUrl(), request.getValidityDays());
        return new ResponseEntity<>(urlDto, HttpStatus.CREATED);
    }

    @GetMapping("/api/urls")
    public ResponseEntity<List<UrlDto>> getAllUrls() {
        List<UrlDto> urls = urlService.getAllUrls();
        return ResponseEntity.ok(urls);
    }

    @GetMapping("/api/urls/{shortCode}")
    public ResponseEntity<UrlDto> getUrlByShortCode(@PathVariable String shortCode) {
        UrlDto urlDto = urlService.getUrlByShortCode(shortCode);
        return ResponseEntity.ok(urlDto);
    }


    @GetMapping("/{shortCode}")
    public RedirectView redirectToOriginalUrl(@PathVariable String shortCode) {
        try {
            UrlDto urlDto = urlService.getUrlByShortCode(shortCode);
            urlService.incrementClickCount(shortCode);

            RedirectView redirectView = new RedirectView();
            redirectView.setUrl(urlDto.getOriginalUrl());
            return redirectView;
        } catch (UrlNotFoundException e) {
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("/error");
            redirectView.setStatusCode(HttpStatus.NOT_FOUND);
            return redirectView;
        }
    }
}