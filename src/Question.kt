package site.pegasis.hoot.server

data class Question(val question: String, val sentence: String, val awnser: String)

val questions = arrayOf(
    Question("Fill in the blank:", "Pega _ xhx", "loves"),
    Question("Fillllllll:", "Pegasis _", "forever")
)