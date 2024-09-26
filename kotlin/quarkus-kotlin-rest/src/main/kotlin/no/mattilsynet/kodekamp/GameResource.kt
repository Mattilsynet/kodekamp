package no.mattilsynet.kodekamp

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.quarkus.logging.Log
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

    private val mapper = ObjectMapper().apply {
        this.registerModule(KotlinModule.Builder().build())
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun isAlive(): String {
        return "My client is alive"
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    fun input(gameBoard: GameBoard): Response {
        return Response.ok(gameEngine.process(gameBoard)).build()
    }


}