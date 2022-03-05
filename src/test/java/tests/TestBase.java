package tests;

import api.helpers.EncodeToken;
import api.helpers.Encoder;
import config.ApiConfig;
import data.DataStorage;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;




import static io.restassured.RestAssured.given;


public class TestBase {


    public static String apiaccessToken = "";
    static ApiConfig api = ConfigFactory.create(ApiConfig.class, System.getProperties());
    static String userToken = System.getProperty("userToken", api.userToken());
    static String clientId = System.getProperty("clientId", api.clientId());
    static String clientSecret = System.getProperty("clientSecret", api.clientSecret());
    static String baseUrl = System.getProperty("baseUrl", api.baseUrl());
    static String basePath = System.getProperty("basePath", api.basePath());
    DataStorage data = new DataStorage();


    @BeforeAll
    public static void authenticationSpotify() {
        RestAssured.baseURI = baseUrl;
        String authToken = EncodeToken.getAuthToken(clientId, clientSecret);
        generateAccessToken(authToken);

    }

    private static void generateAccessToken(String authToken) {
        Response response = given().
                header("Authorization", "Basic " + authToken).
                contentType("application/x-www-form-urlencoded").
                formParam("grant_type", "client_credentials").
                log().all().
                when().
                post("token");

        apiaccessToken = response.jsonPath().get("access_token");
    }


    Encoder encoder = new Encoder();

    @BeforeEach
    public void antesTestes() {
        RestAssured.baseURI = baseUrl;
        RestAssured.basePath = basePath;
    }
}
