package testrefactored;

/*   @author maramartins   */

import core.BaseTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import tests.Transactions;
import utils.DateUtils;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

public class BalanceTest extends BaseTest {

    @Test
    public void balanceValidation() {
        Integer ACCOUNT_ID = getIdAccountByName("Conta para saldo");

        given()
                .when()
                .get("/saldo")
                .then()
                .statusCode(200)
                .body("find{it.conta_id == " +ACCOUNT_ID+"}.saldo", is("534.00"))
        ;
    }

    public Integer getIdAccountByName(String name) {
        return RestAssured.get("/contas?nome="+name).then().extract().path("id[0]");
    }
}
