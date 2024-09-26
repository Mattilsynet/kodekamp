package no.mattilsynet.kodekamp

data class Ordre(
   val unit: String,
    val action: String,
    val x: Int,
    val y: Int
)

//{
//    "unit": "<unit-id>",
//    "action": "<action>",
//    "x": <x-coordinate>,
//    "y": <y-coordinate>
//}