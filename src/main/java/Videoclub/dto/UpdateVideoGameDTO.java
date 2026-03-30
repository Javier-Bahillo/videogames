package Videoclub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVideoGameDTO {
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
}
