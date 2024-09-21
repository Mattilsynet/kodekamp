package no.mattilsynet.kodekamp

import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/game")
class GameResource(
    private val gameEngine: GameEngine
) {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun isAlive(): String {
        return "My client is alive"
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    fun input(input: Input): Response {
        return Response.ok(gameEngine.process(input)).build()
    }
}