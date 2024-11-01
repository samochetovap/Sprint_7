import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import pojo.Order;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreateTest {

    private final List<String> colors;

    public OrderCreateTest(List<String> colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters
    public static Object[][] getSumData() {
        return new Object[][]{
                {List.of("GREY")},
                {List.of("BLACK")},
                {List.of("BLACK", "GREY")},
                {List.of()},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @Step("Создание заказа")
    public void orderCreateTest() {
        Order order = createOrder(colors);
        Response response = given()
                .contentType(ContentType.JSON)
                .and()
                .body(order)
                .and()
                .post("/api/v1/orders");
        response.then().assertThat().statusCode(201).and().body("track", notNullValue());
    }

    private Order createOrder(List<String> colors) {
        Order order = new Order();
        order.setFirstName("Alex");
        order.setLastName("Sam");
        order.setAddress("Moscow, Udalcova, 3");
        order.setMetroStation("5");
        order.setPhone("+7 800 355 35 35");
        order.setRentTime(5);
        order.setDeliveryDate("2020-06-06");
        order.setComment("Please call me before 1 hour");
        order.setColor(colors);
        return order;
    }

}
