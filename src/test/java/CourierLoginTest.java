import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.Courier;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {
    Courier courier;

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
        courier = new Courier("someLogin123321", "somePassword123321", "saske");
    }

    @Test
    @Step("Курьер может авторизоваться")
    public void courierLoginTest(){
        Response createResponse = createCourier(courier);
        createResponse.then().assertThat().statusCode(201).and().body("ok", is(true));
        Response loginResponse = loginCourier(courier);
        loginResponse.then().assertThat().statusCode(200);
    }

    @Test
    @Step("Проверка полей для авторизации")
    public void courierRequiredFieldsLoginTest(){
        Response response = given()
                .contentType(ContentType.JSON)
                .and()
                .body("{\"password\": \"somePassword123321\"}")
                .and()
                .post("/api/v1/courier/login");
        response.then().assertThat().statusCode(400).and().body("message", is("Недостаточно данных для входа"));
    }

    @Test
    @Step("Авторизация под несуществующим пользователем")
    public void nonExistentLoginTest(){
        Courier courier1 = new Courier(UUID.randomUUID().toString(),UUID.randomUUID().toString(),UUID.randomUUID().toString());
        Response response = given()
                .contentType(ContentType.JSON)
                .and()
                .body(courier1)
                .and()
                .post("/api/v1/courier/login");
        response.then().assertThat().statusCode(404).and().body("message", is("Учетная запись не найдена"));
    }

    @Test
    @Step("Возвращение id при успешном запросе")
    public void getIdTest(){
        Response createResponse = createCourier(courier);
        createResponse.then().assertThat().statusCode(201).and().body("ok", is(true));
        Response response = given()
                .contentType(ContentType.JSON)
                .and()
                .body(courier)
                .and()
                .post("/api/v1/courier/login");
        response.then().assertThat().statusCode(200).and().body("id", notNullValue());
    }

    @Step("Создание курьера")
    public Response createCourier(Courier courier){
        Response response = given()
                .contentType(ContentType.JSON)
                .and()
                .body(courier)
                .and()
                .post("/api/v1/courier");
        return response;
    }

    @Step("Авторизация курьера")
    public Response loginCourier(Courier courier){
        return given()
                .contentType(ContentType.JSON)
                .and()
                .body(courier)
                .and()
                .post("/api/v1/courier/login");
    }

    @Step("Удаление курьера если он есть")
    public void deleteCourierIfExists(Courier courier){
        Response response = given()
                .contentType(ContentType.JSON)
                .and()
                .body(courier)
                .and()
                .post("/api/v1/courier/login");
        if(response.statusCode() == 200){
            String courierId = response.then().extract().body().path("id").toString();
            courier.setId(courierId);
            deleteCourier(courier);
        }
    }

    private Response deleteCourier(Courier courier){
        Response response = given()
                .contentType(ContentType.JSON)
                .and()
                .body(courier)
                .and()
                .delete("/api/v1/courier/" + courier.getId());
        return response;
    }

    @After
    public void deleteCourierIfExists(){
        deleteCourierIfExists(courier);
    }
}
