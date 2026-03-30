package Videoclub.dto.gamesdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GamesDbSearchResponse {

    @JsonProperty("data")
    private DataSection data;

    @JsonProperty("include")
    private IncludeSection include;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataSection {
        @JsonProperty("games")
        private List<RawGame> games;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RawGame {
        private Integer id;
        @JsonProperty("game_title")
        private String gameTitle;
        @JsonProperty("release_date")
        private String releaseDate;
        private Integer platform;
        private String overview;
        private String rating;
        private List<Integer> genres;
        private List<Integer> publishers;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IncludeSection {
        private BoxartSection boxart;
        @JsonProperty("platform")
        private PlatformInclude platform;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BoxartSection {
        @JsonProperty("base_url")
        private BoxartBaseUrl baseUrl;
        private Map<String, List<BoxartItem>> data;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BoxartBaseUrl {
        private String original;
        private String small;
        private String thumb;
        @JsonProperty("cropped_center_thumb")
        private String croppedCenterThumb;
        private String medium;
        private String large;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BoxartItem {
        private Integer id;
        private String type;
        private String side;
        private String filename;
        private String resolution;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlatformInclude {
        private Map<String, PlatformItem> data;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlatformItem {
        private Integer id;
        private String name;
        private String alias;
    }
}
