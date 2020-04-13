package testrefactored;

/*   @author maramartins   */

import core.BaseTest;
import org.junit.Test;
import utils.BarrigaUtils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

public class AccountsTest extends BaseTest {

    @Test
    public void createAccount() {
        given()
                .body("{\"nome\": \"Conta Criada\"}")
                .when()
                .post("/contas")
                .then()
                .statusCode(201)
                .extract().path("id")
        ;
    }

    @Test
    public void updateAccount() {
        Integer ACCOUNT_ID = BarrigaUtils.getIdAccountByName("Conta para alterar");

        given()
                .body("{\"nome\": \"Conta alterada\"}")
                .pathParam("id", ACCOUNT_ID)
                .when()
                .put("/contas/{id}")
                .then()
                .statusCode(200)
                .body("nome", is("Conta alterada"))
        ;
    }

    @Test
    public void creareExistentAccount() {
        given()
                .body("{\"nome\": \"Conta mesmo nome\"}")
                .when()
                .post("/contas")
                .then()
                .statusCode(400)
                .body("error", is("Já existe uma conta com esse nome!"))
        ;
    }
}
