package tests;


import api.lombok.Artist;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import spec.ApiSpec;

import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;

public class SpotifyApiTest extends TestBase {


    @DisplayName("Поиск музыкальной группы")
    @Owner("a.kulakov")
    @Feature("API")
    @Test
    public void shouldReturnArtist() {
        Response response =
                given()
                        .auth().oauth2(this.accessToken)
                        .accept(ContentType.JSON)
                        .queryParam("q", "Монгол Шуудан")
                        .queryParam("type", "artist")
                        .when()
                        .get("search")
                        .then()
                        .statusCode(200)
                        .body("artists.items.name[0]", equalTo("Mongol Shuudan"))
                        .extract()
                        .response();
        List<HashMap> respArtist = response.jsonPath().getList("artists.items");
        List<Artist> artistEncoder = encoder.searchForAnArtist(respArtist, "Mongol Shuudan");
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(artistEncoder.get(0).getGenres().get(1)).isEqualTo("russian rock");
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
                        .auth().oauth2(this.accessToken)
                        .accept(ContentType.JSON)
                        .queryParam("country", "RU")
                        .when()
                        .get("artists/{id}/top-tracks", "21WxeFt6mxgD9P8lM9RXN3")
                        .then()
                        .statusCode(200)
                        .body("tracks", hasSize(10))
                        .extract()
                        .response();
        List<String> music = responseTopTracks.jsonPath().getList("tracks.name");
        System.out.println(music.get(6));
        assertThat(music.contains("Москва"));

    }
    ApiSpec responseSpecInstance = new ApiSpec();
    @Test
    public void getCurrentUserProfile() {
        Response responseTracks =  given()
                .spec(responseSpecInstance.getRequestSpec())
                .auth().oauth2(this.accessToken)
                .when()
                .header("Authorization" , "Bearer" + accessToken)
                .header("Content-Type" , "application/json")
                .get("/tracks/11dFghVXANMlKmJXsNCbNl")
                .then()
                .statusCode(200)
                .assertThat()
                .extract()
                .response();


    }


}
