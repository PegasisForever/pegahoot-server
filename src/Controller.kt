package site.pegasis.hoot.server

import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

fun startGame() {
    GlobalScope.launch(block = gameController)
}

const val gameWaitTime = 22
var onAnswerSubmitted: (suspend (name:String, answer:String, index:Int, clientSession: DefaultWebSocketServerSession) -> Unit)? = null
val gameController: suspend CoroutineScope.() -> Unit = {
    questions.forEachIndexed { index, question ->
        //Count down 3s
        repeat(3){
            setDisplayState {copy(
                activity = DisplayActivity.COUNTDOWN,
                countDownSeconds = 3 - it,
                questionIndex = index + 1
            )}
            namedClients.setStates {copy(
                activity = ClientActivity.COUNTDOWN,
                countDownSeconds = 3 - it,
                questionIndex = index + 1
            )}
            delay(1000)
        }

        //Start game
        val gameStartTime = System.currentTimeMillis()
        onAnswerSubmitted = out@{ name, answer, i, clientSession ->
            if (i != index || getDisplayState().answerTimes.find { it.name == name } != null) return@out
            val timeUsed = (System.currentTimeMillis() - gameStartTime) / 1000.0
            val isCorrect = answer.trim().toLowerCase() == question.answer
            val scoreGain = if (isCorrect) ((gameWaitTime - timeUsed) / gameWaitTime * 700 + 300).toInt() else 0

            setDisplayState {copy(
                answerTimes = answerTimes + AnswerTime(name, timeUsed, isCorrect),
                userScoreMap = userScoreMap.addScore(name, scoreGain)
            )}
            clientSession.setClientState {copy(
                activity = ClientActivity.GAMEWAIT,
                score = score + scoreGain,
                isLastAnswerCorrect = isCorrect,
                rank = null
            )}
        }
        namedClients.setStates {copy(
            activity = ClientActivity.GAME,
            questionSentence = question.sentence,
            questionText = question.question,
            isLastAnswerCorrect = false
        )}
        repeat(gameWaitTime){
            setDisplayState {copy(
                activity = DisplayActivity.GAME,
                questionSentence = question.sentence,
                questionText = question.question,
                questionLeftSeconds = gameWaitTime - it
            )}
            delay(1000)
        }
        setDisplayState {copy(
            answerTimes = emptyList()
        )}
        onAnswerSubmitted = null

        //Game end, show score
        setDisplayState {copy(
            activity = DisplayActivity.ANSWER,
            questionAnswer = question.answer
        )}
        val sortedUserScores = sortedUserScores
        namedClients.setStates {
            val userScore = sortedUserScores.find { it.name == name }!!
            val rank = sortedUserScores.indexOf(userScore)
            var followingUser: String? = null
            var scoreBehindFollowingUser: Int? = null
            if (rank != 0) {
                val followingUserScore = sortedUserScores[rank - 1]
                followingUser = followingUserScore.name
                scoreBehindFollowingUser = followingUserScore.score - score
            }
            copy(
                activity = ClientActivity.GAMEWAIT,
                rank = rank+1,
                followingUser = followingUser,
                scoreBehindFollowingUser = scoreBehindFollowingUser
            )
        }
        delay(5000)
    }

    setDisplayState {copy(
        activity = DisplayActivity.FINAL
    )}

    delay(5000)
    exitProcess(0)
}