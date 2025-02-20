import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserCreateTest {

    @Builder
    @Getter
    @Setter
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class UserCreateResponse {
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
    public class UserDto {
        private String name;
        private String job;
    }

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://reqres.in";
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
                .statusCode(201)
                .extract()
                .as(UserCreateResponse.class);

        assertEquals(userDto.getName(), response.getName());
        assertEquals(userDto.getJob(), response.getJob());
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
                .statusCode(201)
                .extract()
                .as(UserCreateResponse.class);

        assertEquals(userDto.getName(), response.getName());
        assertEquals(userDto.getJob(), response.getJob());
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
                .statusCode(201)
                .extract()
                .as(UserCreateResponse.class);

        assertNotNull(response.getId());
        assertNotNull(response.getCreatedAt());
    }

    // 4. Негативный сценарий: отсутствие поля name в запросе
    @Test
    public void noNameCreateTest() {
        UserDto userDto = new UserDto();
        userDto.setJob("QA Engineer");

        given()
                .contentType(ContentType.JSON)
                .body(userDto)
                .post("/api/users")
                .statusCode(400)
                .extract()
                .as(UserCreateResponse.class);
    }

    // 5. Негативный сценарий: отсутствие поля job в запросе
    @Test
    public void noJobCreateTest() {
        UserDto userDto = new UserDto();
        userDto.setName("Ilya Khruschev");

        given()
                .contentType(ContentType.JSON)
                .body(userDto)
                .post("/api/users")
                .statusCode(400)
                .extract()
                .as(UserCreateResponse.class);
    }
}
