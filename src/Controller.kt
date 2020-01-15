package site.pegasis.hoot.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun startGame() {
    GlobalScope.launch(block = gameController)
}

val gameController: suspend CoroutineScope.() -> Unit = {
    questions.forEachIndexed { index, question ->
        repeat(3){
            setDisplayState {copy(
                activity = DisplayActivity.COUNTDOWN,
                countDownSeconds = 3 - it,
                questionIndex = index + 1
            )}
            delay(1000)
        }

        setDisplayState {copy(
            activity = DisplayActivity.GAME
        )}
        delay(5000)
    }
}