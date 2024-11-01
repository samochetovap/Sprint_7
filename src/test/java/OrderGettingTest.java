import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderGettingTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @Step("В тело ответа возвращается список заказов.")
    public void gettingOrdersTest() {
        Response response = given()
                .get("/api/v1/orders?limit=5&page=0");
        List<String> list = response.then().extract().body().path("orders");
        Assert.assertNotNull("orders не является списком", list);
    }
}
