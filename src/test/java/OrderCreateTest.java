import io.restassured.RestAssured;
import org.junit.Before;


public class OrderCreateTest {
    @Before
    public void setUp(){
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

}
