package Videoclub.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoGameCopyDTO {
    private UUID id;
    @NotBlank(message = "Status is required")
    private String status;

    public void setGameId(UUID gameId) {
    }
    public void setCopyNumber(int i) {
    }
}
