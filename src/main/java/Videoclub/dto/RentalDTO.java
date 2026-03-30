package Videoclub.dto;

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
public class RentalDTO {
    private UUID id;
    private LocalDate startAt;
    private LocalDate dueAt;
    private LocalDate returnedAt;
    private String status;
    private BigDecimal rentalPriceStart;
    private BigDecimal lateFee;
    private BigDecimal projectedLateFee;
    private boolean overdue;
    private UUID gameCopyId;
    private UUID gameId;
    private String gameTitle;
    private UUID userId;
}