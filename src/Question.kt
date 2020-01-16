package site.pegasis.hoot.server

data class Question(val question: String, val sentence: String, val answer: String)

val questions = listOf(
    Question("Use the verb VOULOIR:", "Je __ regarder la television.", "veux"),
    Question("Use the verb VOULOIR:", "Il __ voir un film.", "veut"),
    Question("Use the verb VOULOIR:", "Elles __ aller fair les courses.", "veulent"),
    Question("Use the verb VOULOIR:", "Tu __ de l'eau?", "veux"),
    Question("Use the verb VOULOIR:", "Nous __ une nouvelle voiture.", "voulons"),
    Question("Use the verb VOULOIR:", "Vous __ une glace?", "voulez"),

    Question("Use the verb POUVOIR:", "Mes amis __ aller à la plage.", "peuvent"),
    Question("Use the verb POUVOIR:", "Tu __ jouer avec moi.", "peux"),
    Question("Use the verb POUVOIR:", "Vous __ rester ici.", "pouvez"),
    Question("Use the verb POUVOIR:", "Nous __ faire du ski.", "pouvons"),
    Question("Use the verb POUVOIR:", "Je __ manger une pizza entière.", "peux"),

    Question("Use the verb AVOIR:", "Elle __ acheté la robe.", "a"),
    Question("Use the verb AVOIR:", "Je les __ achetés pour toi.", "ai"),
    Question("Use the verb AVOIR:", "J’ai vu deux chats dans le jardin.", "ai"),
    Question("Use the verb AVOIR:", "Tu __ une sœur.", "as"),
    Question("Use the verb AVOIR:", "Vous __ les mêmes yeux.", "avez"),
    Question("Use the verb AVOIR:", "Nous __ deux chats.", "avons"),
    Question("Use the verb AVOIR:", "Ils __ un bébé.", "ont"),

    Question("Use the verb ÊTRE:", "Je __ étudiant.", "suis"),
    Question("Use the verb ÊTRE:", "Tu __ très aimable.", "es"),
    Question("Use the verb ÊTRE:", "Elle __ à Paris.", "est"),
    Question("Use the verb ÊTRE:", "Nous __ fatigués.", "sommes"),
    Question("Use the verb ÊTRE:", "Vous __ en retard.", "êtes"),
    Question("Use the verb ÊTRE:", "Elles __ très intelligentes.", "sont"),

    Question("Use the verb ALLER:", "Je __ a la banque.", "vais"),
    Question("Use the verb ALLER:", "Elle __ a une école.", "va"),
    Question("Use the verb ALLER:", "Nous __ avec Marie.", "allons"),
    Question("Use the verb ALLER:", "Tu __ à la fête?", "vas"),
    Question("Use the verb ALLER:", "Ils __ dans le bureau.", "vont"),
    Question("Use the verb ALLER:", "Vous __ au centre commercial.", "allez"),

    Question("Use the verb FAIRE:", "Je __ la vaisselle.", "fais"),
    Question("Use the verb FAIRE:", "Il __ chaud.", "fait"),
    Question("Use the verb FAIRE:", "Tu __ de la photo.", "fais"),
    Question("Use the verb FAIRE:", "Elle __ du jogging.", "fait"),
    Question("Use the verb FAIRE:", "Nous __ du vélo.", "faisons"),
    Question("Use the verb FAIRE:", "Vous __ de l'aerobic.", "faites")
).shuffled()