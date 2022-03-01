package spec;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static tests.TestBase.accessToken;

public class ApiSpec {
    public RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                //.addHeader("Authorization", "Bearer " +accessToken)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }
}
