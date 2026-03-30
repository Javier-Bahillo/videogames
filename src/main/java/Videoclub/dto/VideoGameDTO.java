package Videoclub.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoGameDTO {

    private UUID id;
    private boolean active;
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotBlank(message = "Platform is required")
    private String platform;
    @NotBlank(message = "Genre is required")
    private String genre;
    @NotBlank(message = "Producer is required")
    private String producer;
    private String pegi;
    private LocalDate releaseDate;
    @NotNull(message = "Rental price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal rentalPrice;
    @NotBlank(message = "Cover URL is required")
    private String coverUrl;

    @Min(0)
    private int availableCopies;
    @Min(0)
    private int totalCopies;
}
