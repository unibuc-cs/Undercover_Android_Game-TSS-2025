package com.example.undercover.data

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class WordGenerator(context: Context) {
    private var wordPairs: List<Pair<String, String>> = emptyList()

    init {
        loadWordsFromFile(context)
    }

    private fun loadWordsFromFile(context: Context) {
        val inputStream = context.assets.open("similar_words.csv")
        val reader = BufferedReader(InputStreamReader(inputStream))
        wordPairs = reader.readLines().mapNotNull { line ->
            val words = line.split(",")
            if (words.size == 2) words[0].trim() to words[1].trim() else null
        }
        reader.close()
    }

    fun generateWords(numPlayers: Int): Pair<String, String> {
        if (wordPairs.isEmpty()) throw IllegalStateException("Word list not loaded")

        return wordPairs.random()

    }
}