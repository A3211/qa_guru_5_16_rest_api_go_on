import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class CartTests {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://demowebshop.tricentis.com";
        Configuration.startMaximized = true;
        Configuration.baseUrl = "http://demowebshop.tricentis.com";
    }

    @Test
    void addItemToCartAsExistUserTest() {
        //Получаю кол-во товара в корзине
        Response response =
        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("product_attribute_72_5_18=53&product_attribute_72_6_19=54&product_attribute_72_3_20=57&addtocart_72.EnteredQuantity=1")
                .cookie("Nop.customer=c8681ebf-0b37-4901-b1e2-ab96a7682089; ARRAffinity=06e3c6706bb7098b5c9133287f2a8d510a64170f97e4ff5fa919999d67a34a46; __utmc=78382081; __utmz=78382081.1621828895.2.2.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); NopCommerce.RecentlyViewedProducts=RecentlyViewedProductIds=72; __atuvc=1%7C21; __atuvs=60ab5c525ba381ec000; __utma=78382081.380596306.1621788182.1621828895.1621843027.3; __utmt=1; __utmb=78382081.1.10.1621843027")
        .when()
                .post("/addproducttocart/details/72/1")
        .then()
                .statusCode(200)
                .log().body()
                .body("success", is(true))
                .extract().response();

        String valueWithBrackets = response.jsonPath().get("updatetopcartsectionhtml");
        int cartSize = Integer.parseInt(valueWithBrackets.substring(1, valueWithBrackets.length() - 1));

        //Кол-во товара в корзине + 1
        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("product_attribute_72_5_18=53&product_attribute_72_6_19=54&product_attribute_72_3_20=57&addtocart_72.EnteredQuantity=1")
                .cookie("Nop.customer=c8681ebf-0b37-4901-b1e2-ab96a7682089; ARRAffinity=06e3c6706bb7098b5c9133287f2a8d510a64170f97e4ff5fa919999d67a34a46; __utmc=78382081; __utmz=78382081.1621828895.2.2.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not%20provided); NopCommerce.RecentlyViewedProducts=RecentlyViewedProductIds=72; __atuvc=1%7C21; __atuvs=60ab5c525ba381ec000; __utma=78382081.380596306.1621788182.1621828895.1621843027.3; __utmt=1; __utmb=78382081.1.10.1621843027")
        .when()
                .post("/addproducttocart/details/72/1")
        .then()
                .statusCode(200)
                .log().body()
                .body("success", is(true))
                .body("updatetopcartsectionhtml", is("(" + (cartSize + 1) + ")"));
    }

    @Test
    void addItemToCartAsExistUserCheckUiTest() {
        String cookie =
        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("product_attribute_72_5_18=53&product_attribute_72_6_19=54&product_attribute_72_3_20=57&addtocart_72.EnteredQuantity=1")
        .when()
                .post("/addproducttocart/details/72/1")
        .then()
                .statusCode(200)
                .log().body().extract().cookie("Nop.customer");

        open("/build-your-cheap-own-computer");
        getWebDriver().manage().addCookie(new Cookie("Nop.customer", cookie));
        Selenide.refresh();
        $(".cart-qty").shouldHave(Condition.text("(1)"));
    }
}
