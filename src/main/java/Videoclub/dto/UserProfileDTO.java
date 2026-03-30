package Videoclub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {
    private UUID id;
    private String username;
    private String name;
    private String surname;
    private String email;
    private String role;
    private String status;
    private LocalDate createdAt;
}
