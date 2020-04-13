package testrefactored;

/*   @author maramartins   */

import core.BaseTest;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import tests.Transactions;
import utils.BarrigaUtils;
import utils.DateUtils;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

public class TransactionsTest extends BaseTest {

    @Test
    public void createTransactions() {
        Transactions transaction =  getValidTransaction();

        given()
                .body(transaction)
                .when()
                .post("/transacoes")
                .then()
                .statusCode(201)
        ;
    }

    @Test
    public void createTransactionsMandatoryFieldsValidation() {

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
    public void createTransactionsWithFutureDate() {
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
    public void deleteAccountWithTransactions() {
        Integer ACCOUNT_ID = BarrigaUtils.getIdAccountByName("Conta com movimentacao");
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
    public void deleteTransaction() {
        Integer TRANSACTION_ID = BarrigaUtils.getTransactionIdByDescription("Movimentacao para exclusao");

        given()
                .pathParam("id", TRANSACTION_ID)
                .when()
                .delete("/transacoes/{id}")
                .then()
                .statusCode(204)
        ;
    }



    private Transactions getValidTransaction() {
        Transactions transaction = new Transactions();
        transaction.setConta_id(BarrigaUtils.getIdAccountByName("Conta para movimentacoes"));
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
