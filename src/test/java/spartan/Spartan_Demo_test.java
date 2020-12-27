package spartan;

import POJO.Spartan;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.* ;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.* ;
import static utility.DB_Utility.*;

public class Spartan_Demo_test {

    @BeforeAll
    public static void setUp(){
        baseURI = "http://100.25.34.245:8000";
        basePath = "/api";
        createConnection();
    }

    @AfterAll
    public static void destroy(){
        reset();
        tearDown();
    }


    @DisplayName("Testing GET /Spartan/{id} endpoint")
    @Test
    public void test1spartan(){

        given()
                .auth().basic("admin","admin")
                .accept(ContentType.JSON)
                .log().all().
        when()
                .get("/spartans/100").
        then()
                .log().all()
                .assertThat()
                .statusCode(is(200))
                .contentType(ContentType.JSON);

    }
    @DisplayName("Testing GET /Spartan/100 endpoint")
    @Test
    public void test100spartan() {

        Response response = given()
                .auth().basic("admin", "admin")
                .accept(ContentType.JSON)
                .pathParam("id", 100)
                .log().all().
                        when()
                .get("spartans/{id}").
                        then()
                .log().all()
                .assertThat()
                .statusCode(is(200))
                .contentType(ContentType.JSON)
                .extract()
                .response();

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
