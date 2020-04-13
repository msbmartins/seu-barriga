package suite;

/*   @author maramartins   */

import core.BaseTest;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import testrefactored.AccountsTest;
import testrefactored.AuthTest;
import testrefactored.BalanceTest;
import testrefactored.TransactionsTest;
import tests.Transactions;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AccountsTest.class,
        BalanceTest.class,
        TransactionsTest.class,
        AuthTest.class
})
public class Runners extends BaseTest {

    @BeforeClass  //once for all tests
    public static void login() {
        Map<String, String> login = new HashMap<String, String>();
        login.put("email", "marasbm@gmail.com");
        login.put("senha", "123456");

        String TOKEN = given()
                .body(login)
                .when()
                .post("/signin")
                .then()
                .statusCode(200)
                .extract().path("token")
                ;

        requestSpecification.header("Authorization", "JWT " + TOKEN);

        RestAssured.get("/reset").then().statusCode(200);
    }
}
