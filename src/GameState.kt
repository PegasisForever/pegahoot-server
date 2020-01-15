data class GameState(
    val userScoreMap: Map<String, Int>,
    val questionIndex: Int,
    val questionRankMap: Map<String, Double>
)