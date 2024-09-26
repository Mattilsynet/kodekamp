package no.mattilsynet.kodekamp

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/game")
class GameResource(
    private val gameEngine: GameEngineV2,
    private val objectMapper: ObjectMapper
) {

    @POST
    @Path("/text")
    @Produces(MediaType.APPLICATION_JSON)
    fun inputText(inputString: String): Response {
        val gameRequest: GameRequest = objectMapper.readValue(inputString, GameRequest::class.java)
        return Response.ok(gameEngine.process2(gameRequest)).build()
    }

}