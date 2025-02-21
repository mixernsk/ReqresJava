import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserCreateTest {

    SoftAssertions softAssertions;

    @Builder
    @Getter
    @Setter
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserCreateResponse {
        private String name;
        private String job;
        private String id;
        private String createdAt;
    }

    @Builder
    @Getter
    @Setter
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDto {
        private String name;
        private String job;
    }

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://reqres.in";
        RestAssured.useRelaxedHTTPSValidation();
    }

    @BeforeEach
    public void setupAssert () {
        this.softAssertions = new SoftAssertions();
    }

    @AfterEach
    public void assertAll () {
        this.softAssertions.assertAll();
    }

    // 1. Позитивный сценарий: успешное создание пользователя
    @Test
    public void createValidDataTest() {
        UserDto userDto = new UserDto();
        userDto.setName("Ilya Khruschev");
        userDto.setJob("QA Engineer");

        UserCreateResponse response = given()
                .contentType(ContentType.JSON)
                .body(userDto)
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract()
                .as(UserCreateResponse.class);

        this.softAssertions.assertThat(userDto.getName())
                .withFailMessage("Expected: %s, actual: %s", userDto.getName(), response.getName())
                .isEqualTo(response.getName());
        this.softAssertions.assertThat(userDto.getJob())
                .withFailMessage("Expected: %s, actual: %s", userDto.getJob(), response.getJob())
                .isEqualTo(response.getJob());
    }

    // 2. Позитивный сценарий: успешное создание пользователя с минимальными данными
    @Test
    public void createWithMinimalLengthTest() {
        UserDto userDto = new UserDto();
        userDto.setName("A");
        userDto.setJob("F");

        UserCreateResponse response = given()
                .contentType(ContentType.JSON)
                .body(userDto)
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract()
                .as(UserCreateResponse.class);

        this.softAssertions.assertThat(userDto.getName())
                .withFailMessage("Expected: %s, actual: %s", userDto.getName(), response.getName())
                .isEqualTo(response.getName());
        this.softAssertions.assertThat(userDto.getJob())
                .withFailMessage("Expected: %s, actual: %s", userDto.getJob(), response.getJob())
                .isEqualTo(response.getJob());
    }

    // 3. Позитивный сценарий: проверка ненулевого ID и даты создания
    @Test
    public void idAndDateCreatedTest() {
        UserDto userDto = new UserDto();
        userDto.setName("Ilya Khruschev");
        userDto.setJob("QA Engineer");

        UserCreateResponse response = given()
                .contentType(ContentType.JSON)
                .body(userDto)
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract()
                .as(UserCreateResponse.class);

        this.softAssertions.assertThat(response.getId())
                .withFailMessage("Expected: %s is not NULL", response.getId())
                .isNotNull();
        this.softAssertions.assertThat(response.getCreatedAt())
                .withFailMessage("Expected: %s is not NULL", response.getCreatedAt())
                .isNotNull();
    }

    // 4. Негативный сценарий: некорретное тело запроса
    @Test
    public void invalidReqBodyTest() {
        String invalidRequest = "This is not a JSON in the first place";

        given()
                .contentType(ContentType.JSON)
                .body(invalidRequest)
                .post("/api/users")
                .then()
                .statusCode(400);
    }

    // 5. Негативный сценарий: некорректный метод запроса
    @Test
    public void noJobCreateTest() {
        UserDto userDto = new UserDto();
        userDto.setName("toomany".repeat(1000));
        userDto.setJob("QA Engineer");

        given()
                .contentType(ContentType.JSON)
                .body(userDto)
                .options("/api/users")
                .then()
                .statusCode(204);
    }
}
