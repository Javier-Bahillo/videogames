package Videoclub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateRentalRequest {
    @NotNull(message = "gameId is required")
    private UUID gameId;
}
