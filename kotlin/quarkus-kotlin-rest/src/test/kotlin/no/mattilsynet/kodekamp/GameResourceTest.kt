package no.mattilsynet.kodekamp

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

@QuarkusTest
class GameResourceTest {

    @Test
    fun `The game GET endpoint works`() {
        given()
          .`when`().get("/game")
          .then()
             .statusCode(200)
             .body(`is`("My client is alive"))
    }

    @Test
    fun `The game POST endpoint works`() {
        given()
            .contentType(ContentType.JSON)
            .body(Input("world"))
            .`when`().post("/game")
            .then()
            .statusCode(200)
            .body("message", `is`( "Received world"))
    }

}