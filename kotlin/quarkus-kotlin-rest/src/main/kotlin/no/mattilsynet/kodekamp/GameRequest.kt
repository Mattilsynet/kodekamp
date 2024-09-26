package no.mattilsynet.kodekamp

data class GameRequest(

    val turnNumber: String,
    val friendlyUnits: List<FriendlyUnit>,
    val enemyUnits: List<EnemyUnit>,
    val moveActionsAvailable: Int,
    val attackActionsAvailable: Int
)

//"moveActionsAvailable": 2,
//"attackActionsAvailable": 1,