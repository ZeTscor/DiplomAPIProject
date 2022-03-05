package data;

import lombok.Data;

@Data
public class DataStorage {
    private String artist = "Монгол Шуудан";
    private String artistName = "Mongol Shuudan";
    private String genres = "russian rock";
    private String country = "RU";
    private String idSong = "21WxeFt6mxgD9P8lM9RXN3";
    private String tracksName = "Казачья";
    private String userName = "Zets";
    private String product = "open";
    private String playlistId = "5wqWIF6vcN4MAPDUJCgZl9";
    private String urisTrack = "spotify:track:5KErrSqoZcQRImu0r4z9nc";
    private String body = "{\n" +
            "  \"tracks\": [\n" +
            "    {\n" +
            "      \"uri\": \""+urisTrack+"\",\n" +
            "      \"positions\": [\n" +
            "        0\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

}
