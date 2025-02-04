package com.example.undercover.data

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream

class AiWordGenerator(context: Context) {
    private var interpreter: Interpreter

    init {
        interpreter = Interpreter(loadModelFile(context))
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("gpt2_model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    fun generateWord(prompt: String): String {
        val inputTensor = ByteBuffer.allocateDirect(512) // Ajustează dimensiunea după nevoie
        inputTensor.put(prompt.toByteArray())

        val outputTensor = ByteBuffer.allocateDirect(512)

        interpreter.run(inputTensor, outputTensor)

        return String(outputTensor.array()).trim() // Conversie la text
    }
}
