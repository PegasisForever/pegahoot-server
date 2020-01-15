package site.pegasis.hoot.server

import org.json.simple.JSONObject

data class ClientState(
    val activity: ClientActivity = ClientActivity.JOIN,
    val joinBtnErrorText: String? = null,
    val name: String? = null,
    val score: Int = 0,
    val questionIndex: Int? = null,
    val countDownSeconds: Int? = null,
    val questionText: String? = null,
    val questionSentence: String? = null
) {
    fun toJSONObject() = JSONObject().apply {
        this["activity"] = activity.name
        this["joinBtnErrorText"] = joinBtnErrorText
        this["name"] = name
        this["score"] = score
        this["questionIndex"] = questionIndex
        this["countDownSeconds"] = countDownSeconds
        this["questionText"] = questionText
        this["questionSentence"] = questionSentence
    }
}

enum class ClientActivity {
    JOIN,
    WAIT,
    COUNTDOWN,
    GAME,
    GAMEWAIT,
    FINAL
}