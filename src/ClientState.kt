import org.json.simple.JSONObject

data class ClientState(
    val activity: ClientActivity = ClientActivity.JOIN,
    val joinBtnErrorText: String? = null,
    val name: String? = null,
    val score: Int? = null
) {
    fun toJSONObject() = JSONObject().apply {
        this["activity"] = activity.name
        this["joinBtnErrorText"] = joinBtnErrorText
        this["name"] = name
        this["score"] = score
    }
}

enum class ClientActivity {
    JOIN,
    WAIT,
    GAME,
    GAMEWAIT,
    FINAL
}