package site.pegasis.hoot.server

import org.json.simple.JSONObject

class ScoreMap : HashMap<String, Int>(), JSONObjectAble {
    override fun toJSONObject() = JSONObject().apply {
        putAll(this@ScoreMap)
    }

    fun removeUser(name:String?):ScoreMap{
        if (name==null) return this
        val clone=this.clone() as ScoreMap
        clone.remove(name)
        return clone
    }

    operator fun plus(another:Pair<String,Int>):ScoreMap{
        val clone=this.clone() as ScoreMap
        clone[another.first]=another.second
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
    val answerTimes: ArrayList<AnswerTime> = ArrayList()
) {
    fun toJSONObject() = JSONObject().apply {
        this["activity"] = activity.name
        this["userScoreMap"] = userScoreMap.toJSONObject()
        this["questionIndex"] = questionIndex
        this["countDownSeconds"] = countDownSeconds
        this["questionText"] = questionText
        this["questionSentence"] = questionSentence
        this["questionLeftSeconds"] = questionLeftSeconds
        this["answerTimes"]=answerTimes.toJSONArray()
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