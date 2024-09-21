package no.mattilsynet.kodekamp

import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class GameEngine {

    fun process(input: Input): Output {
        return Output("Received ${input.message}")
    }
}