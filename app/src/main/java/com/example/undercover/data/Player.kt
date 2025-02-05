package com.example.undercover.data

import com.google.gson.annotations.SerializedName

data class Player(
    @SerializedName("name") val name: String,
    @SerializedName("role") val role: String,
    @SerializedName("word") val word: String
)