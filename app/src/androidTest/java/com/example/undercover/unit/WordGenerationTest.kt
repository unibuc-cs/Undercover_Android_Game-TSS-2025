package com.example.undercover.unit

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.undercover.data.WordGenerator
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.pow
import kotlin.math.sqrt

class WordGenerationTest {
    @Test
    fun testWordGeneration() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val wordGenerator = WordGenerator(context)

        val generatedPairs = List(10) { wordGenerator.generateWords(false) }

        generatedPairs.forEach { (civilianWord, undercoverWord) ->
            assertNotNull(civilianWord)
            assertNotNull(undercoverWord)
            assertNotEquals(civilianWord, undercoverWord)
        }

        val uniquePairs = generatedPairs.toSet()
        assertTrue(uniquePairs.size > 1)
    }

    private fun calculateSimilarityPercentage(word1: String, word2: String): Double {
        val vec1 = generateVector(word1)
        val vec2 = generateVector(word2)
        val cosineSimilarity = calculateCosineSimilarity(vec1, vec2)
        return (cosineSimilarity + 1.0) / 2.0 * 100
    }

    private fun generateVector(word: String): DoubleArray {
        val random = java.util.Random(word.hashCode().toLong())
        return DoubleArray(300) { random.nextDouble() }
    }


    private fun calculateCosineSimilarity(vec1: DoubleArray, vec2: DoubleArray): Double {
        var dotProduct = 0.0
        var norm1 = 0.0
        var norm2 = 0.0

        for (i in vec1.indices) {
            dotProduct += vec1[i] * vec2[i]
            norm1 += vec1[i].pow(2)
            norm2 += vec2[i].pow(2)
        }

        return dotProduct / (sqrt(norm1) * sqrt(norm2))
    }

    @Test
    fun testWordSimilarity() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val wordGenerator = WordGenerator(context)

        repeat(10) {
            val (civilian, undercover) = wordGenerator.generateWords(false)

            val similarityPercent = calculateSimilarityPercentage(civilian, undercover)
            assertTrue("Similarity too low: $similarityPercent", similarityPercent > 25.0)

        }

    }
} 