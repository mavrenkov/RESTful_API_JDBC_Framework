package test_Base;

import io.restassured.specification.RequestSpecification;
import net.serenitybdd.junit5.SerenityTest;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import utility.ConfigurationReader;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.reset;
import static utility.DB_Utility.createConnection;
import static utility.DB_Utility.tearDown;

@SerenityTest
public class SpartanAdminTestBase {


    public static RequestSpecification adminReqSpec ;

    @BeforeAll
    public static void setUp() {
        createConnection();
        baseURI = ConfigurationReader.getProperty("spartan.base_url");
        basePath = "/api";
        adminReqSpec = given().log().all()
                .auth().
                        basic(ConfigurationReader.getProperty("spartan.admin.username"),
                                ConfigurationReader.getProperty("spartan.admin.password")  ) ;
    }

    @AfterAll
    public static void cleanUp(){
        reset();
        tearDown();
        SerenityRest.clear();
    }

}
