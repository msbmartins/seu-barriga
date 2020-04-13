package testrefactored;

/*   @author maramartins   */

import core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.core.Is.is;

public class AuthTest extends BaseTest {

    @Test
    public void unauthorizedAccessWithoutToken() {
        //Remove header with authorization
        FilterableRequestSpecification req = (FilterableRequestSpecification) requestSpecification;
        req.removeHeader("Authorization");

        given()
                .when()
                .get("/contas")
                .then()
                .statusCode(401)
        ;
    }
}
