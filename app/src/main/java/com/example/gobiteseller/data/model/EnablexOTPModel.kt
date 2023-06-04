package com.example.gobiteseller.data.model

data class EnablexOTPModel(
    val campaign_id: String,
    val `data`: DataZ,
    val data_coding: String,
    val from: String,
    val template_id: String,
    val to: List<String>,
    val type: String,
    val validity : String
)