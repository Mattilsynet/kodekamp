package no.mattilsynet.kodekamp

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Path("/game")
class GameResource(
    private val gameEngine: GameEngine,
    private val objectMapper: ObjectMapper
) {

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

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

    @POST
    @Path("/text")
    @Produces(MediaType.APPLICATION_JSON)
    fun inputText(inputString: String): Response {
        logger.info("Received input: $inputString")
        val input: Input = objectMapper.readValue(inputString, Input::class.java)
        return Response.ok(input).build()
    }

}