package tests;


import api.lombok.Artist;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import spec.ApiSpec;

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

    @Test
    public void shouldReturnTrackUri() {
        Response response =
                given()
                        .auth().oauth2(this.apiaccessToken)
                        .accept(ContentType.JSON)
                        .queryParam("q", "Казачья,Mongol Shuudan")
                        .queryParam("type", "track,artist")
                        .queryParam("limit", "1")
                        .when()
                        .get("search")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        ArrayList arrayList = response.path("tracks.items.uri");
        System.out.println(arrayList);

    }

    @Test
    public void addTrackToPlaylist() {
        given()
                .contentType("application/json; charset=UTF-8")
                .spec(responseSpecInstance.getRequestSpec())
                .queryParam("playlist_id", "5wqWIF6vcN4MAPDUJCgZl9")
                .queryParam("uris", "spotify:track:5KErrSqoZcQRImu0r4z9nc")
                .header("Authorization", "Bearer " + userToken)
                .when()
                .post("playlists/{playlist_id}/tracks", "5wqWIF6vcN4MAPDUJCgZl9")
                .then()
                .statusCode(201);

        Response itemResponse =
                given()
                        .contentType("application/json; charset=UTF-8")
                        .header("Authorization", "Bearer " + userToken)
                        .queryParam("limit", "1")
                        .when()
                        .get("playlists/{playlist_id}/tracks", "5wqWIF6vcN4MAPDUJCgZl9")
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
        ArrayList arrayList = itemResponse.path("items.track.uri");
        Assertions.assertEquals("spotify:track:5KErrSqoZcQRImu0r4z9nc",arrayList.get(0));

    }
}
