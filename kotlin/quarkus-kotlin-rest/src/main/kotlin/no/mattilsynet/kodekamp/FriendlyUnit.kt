package no.mattilsynet.kodekamp

data class FriendlyUnit(
    var y: Int,
    var moves: Int,
    val maxHealth: Int,
    val attackStrength: Int,
    val id: String,
    val kind: String,
    val health: Int,
    val side: String,
    val armor: Int,
    var x: Int,
    val attacks: Int
) {

    fun move(x: Int, y: Int): Ordre {
        this.y = y
        this.x = x
        this.moves--
        return Ordre(id, "move", x, y)
    }


}

//
//"y": 2,
//"moves": 2,
//"maxHealth": 7,
//"attackStrength": 3,
//"id": "unit-0",
//"kind": "warrior",
//"health": 7,
//"side": "player-1",
//"armor": 1,
//"x": 1,
//"attacks": 1