package site.pegasis.hoot.server

import org.json.simple.JSONObject

data class DisplayState(
    val activity: DisplayActivity = DisplayActivity.JOIN,
    val users: List<String> = ArrayList()
) {
    fun toJSONObject() = JSONObject().apply {
        this["activity"] = activity.name
        this["users"] = users.toJSONArray()
    }
}

enum class DisplayActivity {
    JOIN,
    GAME,
    SCOREBOARD,
    FINAL
}