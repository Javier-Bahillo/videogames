package Videoclub.dto.gamesdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GamesDbPublishersResponse {

    private DataSection data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataSection {
        private Map<String, PublisherItem> publishers;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PublisherItem {
        private Integer id;
        private String name;
    }
}
