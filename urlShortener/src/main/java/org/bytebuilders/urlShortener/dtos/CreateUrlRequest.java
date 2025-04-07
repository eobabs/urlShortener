package org.bytebuilders.urlShortener.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUrlRequest {
    @NotBlank(message = "Original URL is required")
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "URL must start with http://, https://, or ftp://")
    private String originalUrl;

    private Integer validityDays;
}