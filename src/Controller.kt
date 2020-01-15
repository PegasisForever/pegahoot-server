package site.pegasis.hoot.server

import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun startGame() {
    GlobalScope.launch(block = gameController)
}

const val gameWaitTime = 15
var onAnswerSubmitted: (suspend (name:String, answer:String, index:Int, clientSession: DefaultWebSocketServerSession) -> Unit)? = null
val gameController: suspend CoroutineScope.() -> Unit = {
    questions.forEachIndexed { index, question ->
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

        val gameStartTime = System.currentTimeMillis()
        onAnswerSubmitted = out@{ name, answer, i, clientSession ->
            if (i!=index) return@out
            val timeUsed = (System.currentTimeMillis() - gameStartTime) / 1000.0
            val isCorrect = answer.trim().toLowerCase() == question.awnser
            val scoreGain = if (isCorrect) ((gameWaitTime - timeUsed) / gameWaitTime * 1000).toInt() else 0

            setDisplayState {copy(
                answerTimes = answerTimes + AnswerTime(name, timeUsed, isCorrect),
                userScoreMap = userScoreMap.addScore(name, scoreGain)
            )}
            clientSession.setClientState {copy(
                activity = ClientActivity.GAMEWAIT,
                score = score + scoreGain
            )}
        }
        namedClients.setStates {copy(
            activity = ClientActivity.GAME,
            questionSentence = question.sentence,
            questionText = question.question
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
    }
}