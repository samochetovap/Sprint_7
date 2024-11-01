import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.Courier;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;


public class CourierCreateTest {

    Courier courier;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        courier = new Courier("someLogin123321", "somePassword123321", "saske");
    }

    @Test
    @Step("Курьера можно создать")
    public void createCourierTest() {
        Response createResponse = createCourier(courier);
        createResponse.then().assertThat().statusCode(201).and().body("ok", is(true));
    }

    @Test
    @Step("Нельзя создать двух курьеров c одним и тем же логином")
    public void cantCreateTwoEqualsCouriersTest() {
        Response firstCreateResponse = createCourier(courier);
        firstCreateResponse.then().assertThat().statusCode(201).and().body("ok", is(true));
        Response secondCreateResponse = createCourier(courier);
        secondCreateResponse.then().assertThat().statusCode(409).and().body("message", is("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @Step("Создание учетной записи без логина или пароля")
    public void checkCreateDataTest() {
        Courier courierWithNullLogin = new Courier(null, "test", "name");
        Response withNullLoginResponse = createCourier(courierWithNullLogin);
        withNullLoginResponse.then().assertThat().statusCode(400).and().body("message", is("Недостаточно данных для создания учетной записи"));
        Courier courierWithNullPassword = new Courier("test", null, "name");
        Response withNullPasswordResponse = createCourier(courierWithNullPassword);
        withNullPasswordResponse.then().assertThat().statusCode(400).and().body("message", is("Недостаточно данных для создания учетной записи"));
        //на всякий случай попытаемся удалить если через api ошибочно создадутся курьеры
        deleteCourierIfExists(courierWithNullLogin);
        deleteCourierIfExists(courierWithNullPassword);
    }

    @Step("Создание курьера")
    public Response createCourier(Courier courier) {
        Response response = given()
                .contentType(ContentType.JSON)
                .and()
                .body(courier)
                .and()
                .post("/api/v1/courier");
        return response;
    }


    @Step("Удаление курьера если он есть")
    public void deleteCourierIfExists(Courier courier) {
        Response response = given()
                .contentType(ContentType.JSON)
                .and()
                .body(courier)
                .and()
                .post("/api/v1/courier/login");
        if (response.statusCode() == 200) {
            String courierId = response.then().extract().body().path("id").toString();
            courier.setId(courierId);
            deleteCourier(courier);
        }
    }

    private Response deleteCourier(Courier courier) {
        Response response = given()
                .contentType(ContentType.JSON)
                .and()
                .body(courier)
                .and()
                .delete("/api/v1/courier/" + courier.getId());
        return response;
    }

    @After
    public void deleteCourierIfExists() {
        deleteCourierIfExists(courier);
    }

}
