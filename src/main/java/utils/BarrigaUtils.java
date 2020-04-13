package utils;

/*   @author maramartins   */

import io.restassured.RestAssured;

public class BarrigaUtils {

    public static Integer getIdAccountByName(String name) {
        return RestAssured.get("/contas?nome="+name).then().extract().path("id[0]");
    }

    public static Integer getTransactionIdByDescription(String desc) {
        return RestAssured.get("/transacoes?descricai="+desc).then().extract().path("id[0]");
    }
}
