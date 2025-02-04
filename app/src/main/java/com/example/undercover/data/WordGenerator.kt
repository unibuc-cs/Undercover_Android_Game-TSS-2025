package com.example.undercover.data

object WordGenerator {
    private val wordPairs = listOf(
        "Laptop" to "Tableta",
        "Călătorie" to "Vacanță",
        "Actor" to "Regizor",
        "Bere" to "Whiskey",
        "Telefon" to "Smartphone",
        "Doctor" to "Asistent",
        "Masă" to "Birou",
        "Pisică" to "Leu"
    )

    fun generateWords(numPlayers: Int): List<String> {
        val selectedPair = wordPairs.random()
        val words = mutableListOf<String>()

        val undercoverIndex = (0 until numPlayers).random()
        repeat(numPlayers) { index ->
            words.add(if (index == undercoverIndex) selectedPair.second else selectedPair.first)
        }
        return words.shuffled()
    }
}
