package site.pegasis.hoot.server

import org.json.simple.JSONObject

data class DisplayState(
    val activity: DisplayActivity = DisplayActivity.JOIN,
    val users: List<String> = ArrayList(),
    val questionIndex: Int = 0,
    val countDownSeconds: Int = 0
) {
    fun toJSONObject() = JSONObject().apply {
        this["activity"] = activity.name
        this["users"] = users.toJSONArray()
        this["questionIndex"]=questionIndex
        this["countDownSeconds"] = countDownSeconds
    }
}

enum class DisplayActivity {
    JOIN,
    COUNTDOWN,
    GAME,
    ANSWER,
    SCOREBOARD,
    FINAL
}