package api.helpers;

import api.lombok.Artist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Encoder {
    public List<Artist> searchForAnArtist(List<HashMap> respArtist, String artistsSearch) {
        List<Artist> artists = new ArrayList<>();
        for (HashMap respArtis : respArtist) {
            Artist artist = new Artist();
            if (artistsSearch.equals(respArtis.get("name").toString())) {
                artist.setName(respArtis.get("name").toString());
                artist.setId(String.valueOf(respArtis.get("id")));
                artist.setPopularity((Integer) respArtis.get("popularity"));
                artists.add(artist);
                artist.setGenres((List<String>) respArtis.get("genres"));
            }
        }
        return artists;
    }
}
