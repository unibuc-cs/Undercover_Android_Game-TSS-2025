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
        wordPairs.shuffled()
        reader.close()
    }

    fun generateWords(): Pair<String, String> {
        if (wordPairs.isEmpty()) throw IllegalStateException("Word list not loaded")
        var pair = wordPairs.random();

        if ((0..1).random() == 1) {
            pair = pair.second to pair.first
        }

        return pair
    }

}