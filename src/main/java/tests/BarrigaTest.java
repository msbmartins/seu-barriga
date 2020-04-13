package tests;

/*   @author maramartins   */

import core.BaseTest;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import utils.DateUtils;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {

    private static String NAME_ACCOUNT = "Account" + System.nanoTime();
    private static Integer ACCOUNT_ID;
    private static Integer TRANSACTION_ID;

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
    }

    @Test
    public void t01_createAccount() {
        ACCOUNT_ID = given()
                .body("{\"nome\": \""+NAME_ACCOUNT+"\"}")
        .when()
                .post("/contas")
        .then()
                .statusCode(201)
                .extract().path("id")
        ;
    }

    @Test
    public void t02_updateAccount() {
        given()
                .body("{\"nome\": \""+NAME_ACCOUNT+" alterada\"}")
                .pathParam("id", ACCOUNT_ID)
        .when()
                .put("/contas/{id}")
        .then()
                .statusCode(200)
                .body("nome", is(NAME_ACCOUNT+" alterada"))
        ;
    }

    @Test
    public void t03_creareExistentAccount() {
        given()
                .body("{\"nome\": \""+NAME_ACCOUNT+" alterada\"}")        .when()
                .when()
                .post("/contas")
                .then()
                .statusCode(400)
                .body("error", Matchers.is("Já existe uma conta com esse nome!"))
        ;
    }

    @Test
    public void t04_createTransactions() {
        Transactions transaction =  getValidTransaction();

        TRANSACTION_ID = given()
                .body(transaction)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(201)
                .extract().path("id")
        ;
    }

    @Test
    public void t05_createTransactionsMandatoryFieldsValidation() {

        given()
                .body("{}")
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(8))
                .body("msg", Matchers.hasItems(
                        "Data da Movimentação é obrigatório",
                        "Data do pagamento é obrigatório",
                        "Descrição é obrigatório",
                        "Interessado é obrigatório",
                        "Valor é obrigatório",
                        "Valor deve ser um número",
                        "Conta é obrigatório",
                        "Situação é obrigatório"
                ))
        ;
    }

    @Test
    public void t06_createTransactionsWithFutureDate() {
        Transactions transaction = getValidTransaction();
        transaction.setData_transacao(DateUtils.getDateDifferenceDays(2));

        given()
                .body(transaction)
        .when()
                .post("/transacoes")
        .then()
                .statusCode(400)
                .body("$", hasSize(1))
                .body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual") )
        ;
    }

    @Test
    public void t07_deleteAccountWithTransactions() {

        given()
                .pathParam("id", ACCOUNT_ID)
        .when()
                .delete("/contas/{id}")
        .then()
                .statusCode(500)
                .body("constraint", Matchers.is("transacoes_conta_id_foreign"))
        ;
    }

    @Test
    public void t08_balanceValidation() {

        given()
        .when()
                .get("/saldo")
        .then()
                .statusCode(200)
                .body("find{it.conta_id == " +ACCOUNT_ID+"}.saldo", is("100.00"))
        ;
    }

    @Test
    public void t09_deleteTransaction() {

        given()
                .pathParam("id", TRANSACTION_ID)
        .when()
                .delete("/transacoes/{id}")
        .then()
                .statusCode(204)
        ;
    }

    @Test
    public void t10_unauthorizedAccessWithoutToken() {
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

    private Transactions getValidTransaction() {
        Transactions transaction = new Transactions();
        transaction.setConta_id(ACCOUNT_ID);
        transaction.setDescricao("transaction description");
        transaction.setEnvolvido("client abc");
        transaction.setTipo("REC");
        transaction.setData_transacao(DateUtils.getDateDifferenceDays(-1));
        transaction.setData_pagamento(DateUtils.getDateDifferenceDays(5));
        transaction.setValor(100f);
        transaction.setStatus(true);
        return  transaction;
    }
}


