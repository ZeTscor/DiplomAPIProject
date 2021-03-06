package api.lombok;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Artist {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("popularity")
    private int popularity;

    @JsonProperty("genres")
    private List<String> genres;
}

