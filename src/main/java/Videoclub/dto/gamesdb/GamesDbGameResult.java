package Videoclub.dto.gamesdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GamesDbGameResult {
    private int id;
    private String title;
    private String platform;
    private String releaseDate;
    private String coverUrl;
    private String overview;
    private String rating;
    private String genre;
    private String producer;
}
