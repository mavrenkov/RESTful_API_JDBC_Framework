package spartan;

import POJO.Spartan;
import POJO.SpartanRead;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.serenitybdd.junit5.SerenityTest;
import net.serenitybdd.rest.Ensure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import test_Base.SpartanAdminTestBase;
import utility.Spartan_Util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.serenitybdd.rest.SerenityRest.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.* ;
import static utility.DB_Utility.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
@SerenityTest
public class Spartan_CRUD_Test extends SpartanAdminTestBase {

    private SpartanRead spartanResponsePOJO;
    private Spartan randomSpartan;

    @Test
    @DisplayName("1. Create Spartan From POJO and Test POST /spartans")
    public void createSpartanFromPOJO(){

        randomSpartan = Spartan_Util.getRandomSpartanPOJO_Payload();

        JsonPath SpartanPath = given()
                .spec(adminReqSpec)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(randomSpartan).
        when()
                .post("/spartans").jsonPath();

        spartanResponsePOJO = SpartanPath.getObject("data",SpartanRead.class);

        Ensure.that("Request was successful", thenResponse -> thenResponse.statusCode(201))
                .andThat("Response is in JSON format",thenResponse -> thenResponse.contentType(ContentType.JSON))
                .andThat("Response was within 2 seconds", thenResponse ->  thenResponse.time(lessThan(2L), TimeUnit.SECONDS))
                .andThat("Response has spartan with id ", thenResponse -> thenResponse.body("data.id", is(spartanResponsePOJO.getId())));

        assertThat("Test if created Spartan is equal to Generated Spartan",randomSpartan.getName(),is(spartanResponsePOJO.getName()));
        assertThat("Test if created Spartan is equal to Generated Spartan",randomSpartan.getGender(),is(spartanResponsePOJO.getGender()));
        assertThat("Test if created Spartan is equal to Generated Spartan",randomSpartan.getPhone(),is(spartanResponsePOJO.getPhone()));
    }

    @Test
    @DisplayName("2. Read Spartan From POJO and Test Get /spartans/{id}")
    public void readSpartanPOJO(){

        given()
                .spec(adminReqSpec)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .pathParam("id", spartanResponsePOJO.getId()).
        when()
                .get("/spartans/{id}");

        Ensure.that("Request was successful", thenResponse -> thenResponse.statusCode(200))
                .andThat("Response is in JSON format",thenResponse -> thenResponse.contentType(ContentType.JSON))
                .andThat("Response was within 2 seconds", thenResponse ->  thenResponse.time(lessThan(2L), TimeUnit.SECONDS))
                .andThat("Response has spartan with id ", thenResponse -> thenResponse.body("data.id", is(spartanResponsePOJO.getId())));

        runQuery("SELECT * FROM spartans WHERE spartan_id=" + spartanResponsePOJO.getId());

        displayAllData();

        List<String> columnNames = getColumnNames();
        Map<String, String> rowDataAsMap = getRowDataAsMap(1);
        assertThat(rowDataAsMap.get(columnNames.get(1)), equalTo(spartanResponsePOJO.getName()));
        assertThat(rowDataAsMap.get(columnNames.get(2)), equalTo(spartanResponsePOJO.getGender()));
        assertThat(Long.parseLong(rowDataAsMap.get(columnNames.get(3))), equalTo(spartanResponsePOJO.getPhone()));

    }


}
