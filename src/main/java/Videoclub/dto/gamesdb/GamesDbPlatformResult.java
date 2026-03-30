package Videoclub.dto.gamesdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamesDbPlatformResult {
    private int id;
    private String name;
    private String alias;
}
