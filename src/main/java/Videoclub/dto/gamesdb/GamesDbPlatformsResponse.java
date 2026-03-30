package Videoclub.dto.gamesdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GamesDbPlatformsResponse {

    private DataSection data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataSection {
        private Map<String, PlatformItem> platforms;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlatformItem {
        private Integer id;
        private String name;
        private String alias;
        private String overview;
    }
}
