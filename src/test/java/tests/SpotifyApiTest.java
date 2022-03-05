package tests;


import api.filter.CustomLogFilter;
import api.lombok.Artist;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import spec.ApiSpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class SpotifyApiTest extends TestBase {


    @DisplayName("Поиск музыкальной группы")
    @Owner("a.kulakov")
    @Feature("API")
    @Test
    public void shouldReturnArtist() {
        Response response =
                given()
                        .filter(CustomLogFilter.customLogFilter().withCustomTemplates())
                        .auth().oauth2(this.apiaccessToken)
                        .accept(ContentType.JSON)
                        .queryParam("q", data.getArtist())
                        .queryParam("type", "artist")
                        .when()
                        .get("search")
                        .then()
                        .statusCode(200)
                        .body("artists.items.name[0]", equalTo(data.getArtistName()))
                        .extract()
                        .response();
        List<HashMap> respArtist = response.jsonPath().getList("artists.items");
        List<Artist> artistEncoder = encoder.searchForAnArtist(respArtist, data.getArtistName());
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(artistEncoder.get(0).getGenres().get(1)).isEqualTo(data.getGenres());
        softly.assertAll();

    }

    @DisplayName("Получения списка самых популярны треков в стране {RU}")
    @Owner("a.kulakov")
    @Feature("API")
    @Test
    public void shouldReturnTopTenSong() {
        Response responseTopTracks =
                given()
                        .filter(CustomLogFilter.customLogFilter().withCustomTemplates())
                        .spec(responseSpecInstance.getRequestSpec())
                        .auth().oauth2(this.apiaccessToken)
                        .accept(ContentType.JSON)
                        .queryParam("country", data.getCountry())
                        .when()
                        .get("artists/{id}/top-tracks", data.getIdSong())
                        .then()
                        .statusCode(200)
                        .body("tracks", hasSize(10))
                        .extract()
                        .response();
        List<String> music = responseTopTracks.jsonPath().getList("tracks.name");
        System.out.println(music.get(6));
        Assertions.assertEquals(music.get(6), data.getTracksName());

    }

    ApiSpec responseSpecInstance = new ApiSpec();

    @DisplayName("Информация об аккаунте")
    @Owner("a.kulakov")
    @Feature("API")
    @Test
    public void getUserAllDetail() {
        Response response =
                given()
                        .filter(CustomLogFilter.customLogFilter().withCustomTemplates())
                        .contentType("application/json; charset=UTF-8")
                        .spec(responseSpecInstance.getRequestSpec())
                        .when()
                        .header("Authorization", "Bearer " + userToken)
                        .get("/me")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        Assertions.assertEquals(data.getCountry(), response.getBody().jsonPath().getString("country"));
        Assertions.assertEquals(data.getUserName(), response.getBody().jsonPath().getString("display_name"));
        Assertions.assertEquals(data.getProduct(), response.getBody().jsonPath().getString("product"));


    }

    @DisplayName("Получение идентификатора трека ")
    @Disabled
    @Owner("a.kulakov")
    @Feature("API")
    @Test
    public void shouldReturnTrackUri() {
        Response response =
                given()
                        .filter(CustomLogFilter.customLogFilter().withCustomTemplates())
                        .auth().oauth2(this.apiaccessToken)
                        .accept(ContentType.JSON)
                        .queryParam("q", data.getTracksName() + " " + data.getArtistName())
                        .queryParam("type", "track,artist")
                        .queryParam("limit", "1")
                        .when()
                        .get("search")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        ArrayList returnTrackUri = response.path("tracks.items.uri");
        Assertions.assertEquals(data.getUrisTrack(), returnTrackUri.get(0).toString());

    }
    @DisplayName("Добавление трека в плейслист")
    @Owner("a.kulakov")
    @Feature("API")
    @Test
    public void addTrackToPlaylist() {
        given()
                .filter(CustomLogFilter.customLogFilter().withCustomTemplates())
                .contentType("application/json; charset=UTF-8")
                .spec(responseSpecInstance.getRequestSpec())
                .queryParam("playlist_id", data.getPlaylistId())
                .queryParam("uris", data.getUrisTrack())
                .header("Authorization", "Bearer " + userToken)
                .when()
                .post("playlists/{playlist_id}/tracks", data.getPlaylistId())
                .then()
                .statusCode(201);

        Response itemResponse =
                given()
                        .filter(CustomLogFilter.customLogFilter().withCustomTemplates())
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + userToken)
                        .queryParam("limit", "1")
                        .when()
                        .get("playlists/{playlist_id}/tracks", data.getPlaylistId())
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        ArrayList trackUri = itemResponse.path("items.track.uri");
        Assertions.assertEquals(data.getUrisTrack(), trackUri.get(0));

    }
    @DisplayName("Удаления трека из плейлиста")
    @Owner("a.kulakov")
    @Feature("API")
    @Test
    public void removeItemFromPlaylist() throws IOException {
       given().contentType(ContentType.JSON)
                .filter(CustomLogFilter.customLogFilter().withCustomTemplates())
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body(data.getBody())
                .when()
                .delete("playlists/{playlist_id}/tracks", data.getPlaylistId());

        Response itemResponse =
                given()
                        .filter(CustomLogFilter.customLogFilter().withCustomTemplates())
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + userToken)
                        .queryParam("limit", "1")
                        .when()
                        .get("playlists/{playlist_id}/tracks", data.getPlaylistId())
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        ArrayList trackUri = itemResponse.path("items.track.uri");
        Assertions.assertTrue(trackUri.isEmpty());
    }
}
