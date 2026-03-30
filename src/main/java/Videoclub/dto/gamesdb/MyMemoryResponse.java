package Videoclub.dto.gamesdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MyMemoryResponse {

    private ResponseData responseData;
    private Integer responseStatus;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseData {
        private String translatedText;
    }
}
