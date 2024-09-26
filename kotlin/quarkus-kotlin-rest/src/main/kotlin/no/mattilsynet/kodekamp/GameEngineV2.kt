package no.mattilsynet.kodekamp

import jakarta.enterprise.context.ApplicationScoped
import kotlin.random.Random

@ApplicationScoped
class GameEngineV2 {

    fun process2(gameRequest: GameRequest): List<Ordre> {
        var ordre: MutableList<Ordre> = mutableListOf<Ordre>()
        ordre.addAll(gameRequest.friendlyUnits.filter { it.kind == "warrior" }.flatMap { it.act(gameRequest) })
        ordre.addAll(gameRequest.friendlyUnits.filter { it.kind == "archer" }.flatMap { it.act(gameRequest) })
        ordre.addAll(gameRequest.friendlyUnits.filter { it.kind == "barbarian" }.flatMap { it.act(gameRequest) })
        ordre.addAll(gameRequest.friendlyUnits.filter { it.kind == "knight" }.flatMap { it.act(gameRequest) })
        ordre.addAll(gameRequest.friendlyUnits.filter { it.kind == "wizard" }.flatMap { it.act(gameRequest) })
        return ordre
    }

}