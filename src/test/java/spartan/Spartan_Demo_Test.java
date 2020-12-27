package spartan;

import POJO.Spartan;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.junit5.SerenityTest;
import net.serenitybdd.rest.Ensure;

import org.junit.Ignore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import test_Base.SpartanAdminTestBase;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.serenitybdd.rest.SerenityRest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.* ;
import static utility.DB_Utility.*;


@TestMethodOrder(MethodOrderer.DisplayName.class)
@SerenityTest @Ignore
public class Spartan_Demo_Test extends SpartanAdminTestBase {
    @DisplayName("1. Testing GET /Spartan/{id} endpoint")
    @Test
    public void test1spartan(){

        given()
                .spec(adminReqSpec)
                .accept(ContentType.JSON)
                .pathParam("id", 100).
        when()
                .get("/spartans/{id}");

        Ensure.that("Request was successful", thenResponse -> thenResponse.statusCode(is(200)))
                .andThat("Response is in JSON format",thenResponse -> thenResponse.contentType(ContentType.JSON))
                .andThat("Response was within 2 seconds", thenResponse ->  thenResponse.time(lessThan(2L), TimeUnit.SECONDS))
                .andThat("Response has spartan with id = 100", thenResponse -> thenResponse.body("id", is(100)));

    }
    @DisplayName("2. Testing GET /Spartan/100 endpoint")
    @Test
    public void test100spartan() {

        Response response = given()
                .spec(adminReqSpec)
                .accept(ContentType.JSON)
                .pathParam("id", 100).
        when()
                .get("spartans/{id}").
        then()
                .extract()
                .response();

        Ensure.that("Request was successful", thenResponse -> thenResponse.statusCode(200))
                .andThat("Response is in JSON format",thenResponse -> thenResponse.contentType(ContentType.JSON))
                .andThat("Response was within 2 seconds", thenResponse ->  thenResponse.time(lessThan(2L), TimeUnit.SECONDS))
                .andThat("Response has spartan with id = 100", thenResponse -> thenResponse.body("id", is(100)));

        Spartan spartan100 = response.as(Spartan.class);

        runQuery("SELECT * FROM spartans WHERE spartan_id=100 ");

        displayAllData();

        List<String> columnNames = getColumnNames();
        Map<String, String> rowDataAsMap = getRowDataAsMap(1);
        assertThat(rowDataAsMap.get(columnNames.get(1)), equalTo(spartan100.getName()));
        assertThat(rowDataAsMap.get(columnNames.get(2)), equalTo(spartan100.getGender()));
        assertThat(Long.parseLong(rowDataAsMap.get(columnNames.get(3))), equalTo(spartan100.getPhone()));

    }

}
