package com.example.gobiteseller.data.model

data class DataXXXX(
    val category: String,
    val cover_photos: List<String>,
    val created_at: String,
    val description: String,
    val filterable_fields: List<Any>,
    val icon: String,
    val id: String,
    val item_type: String,
    val meta_data: MetaData,
    val name: String,
    var status: String,
    val updated_at: String,
    val variants: List<Variant>
)