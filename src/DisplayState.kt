package site.pegasis.hoot.server

import org.json.simple.JSONArray
import org.json.simple.JSONObject

data class UserScore(val name: String, val score: Int) : JSONObjectAble {
    override fun toJSONObject() = JSONObject().apply {
        this["name"] = name
        this["score"] = score
    }
}

class ScoreMap : HashMap<String, Int>(), JSONArrayAble {
    override fun toJSONArray(): JSONArray {
        return toUserScoreList().toJSONArray()
    }

    fun removeUser(name: String?): ScoreMap {
        if (name == null) return this
        val clone = this.clone()
        clone.remove(name)
        return clone
    }

    fun addScore(name: String, score: Int): ScoreMap {
        val clone = this.clone()
        clone[name] = clone[name]?:0 + score
        return clone
    }

    fun toUserScoreList(): List<UserScore> {
        return this.map { (name, score) ->
            UserScore(name, score)
        }
            .toList()
            .sortedByDescending { it.score }
    }

    override fun clone(): ScoreMap {
        return super.clone() as ScoreMap
    }

    operator fun plus(another: Pair<String, Int>): ScoreMap {
        val clone = this.clone()
        clone[another.first] = another.second
        return clone
    }
}

data class AnswerTime(val name: String, val time: Double, val isCorrect: Boolean) : JSONObjectAble {
    override fun toJSONObject() = JSONObject().apply {
        this["name"] = name
        this["time"] = time
        this["isCorrect"] = isCorrect
    }
}

data class DisplayState(
    val activity: DisplayActivity = DisplayActivity.JOIN,
    val userScoreMap: ScoreMap = ScoreMap(),
    val questionIndex: Int? = null,
    val countDownSeconds: Int? = null,
    val questionText: String? = null,
    val questionSentence: String? = null,
    val questionLeftSeconds: Int? = null,
    val answerTimes: List<AnswerTime> = emptyList(),
    val questionAnswer: String? = null
) {
    fun toJSONObject() = JSONObject().apply {
        this["activity"] = activity.name
        this["userScores"] = userScoreMap.toJSONArray()
        this["questionIndex"] = questionIndex
        this["countDownSeconds"] = countDownSeconds
        this["questionText"] = questionText
        this["questionSentence"] = questionSentence
        this["questionLeftSeconds"] = questionLeftSeconds
        this["answerTimes"] = answerTimes.toJSONArray()
        this["questionAnswer"] = questionAnswer
    }
}

enum class DisplayActivity {
    JOIN,
    COUNTDOWN,
    GAME,
    ANSWER,
    FINAL
}