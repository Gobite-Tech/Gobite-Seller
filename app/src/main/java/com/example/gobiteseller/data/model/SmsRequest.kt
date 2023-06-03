package com.example.gobiteseller.data.model

data class SmsRequest(
    var campaign_id: String,
    var data_coding: String,
    var from: String,
    var template_id: String,
    var to: List<String>,
    var type: String,
)