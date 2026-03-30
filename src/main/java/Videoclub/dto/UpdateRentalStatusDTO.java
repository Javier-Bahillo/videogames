package Videoclub.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class UpdateRentalStatusDTO {
    @NotNull(message = "Local date should be filled")
    private LocalDate status;
}
