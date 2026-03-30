package Videoclub.dto.gamesdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GamesDbGenresResponse {

    private DataSection data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataSection {
        private Map<String, GenreItem> genres;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GenreItem {
        private Integer id;
        private String name;
    }
}
