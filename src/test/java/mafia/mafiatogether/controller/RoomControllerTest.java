package mafia.mafiatogether.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import mafia.mafiatogether.service.dto.RoomCreateRequest;
import mafia.mafiatogether.service.dto.RoomCreateResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RoomControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }


    @Test
    void 방을_생성할_수_있다() {
        //given
        RoomCreateRequest request = new RoomCreateRequest(5, 1, 1, 1);

        //when
        RoomCreateResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/room")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(RoomCreateResponse.class);

        //then
        Assertions.assertThat(response.code()).isNotBlank();
    }
}
