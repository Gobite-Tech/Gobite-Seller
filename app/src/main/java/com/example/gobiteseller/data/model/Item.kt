package com.example.gobiteseller.data.model

data class Item(
    val category: String,
    val cover_photos: List<Any>,
    val created_at: String,
    val description: String,
    val filterable_fields: List<Any>,
    val icon: String?,
    val id: String,
    val item_type: String,
    val meta_data: MetaDataX,
    val name: String,
    val status: String,
    val updated_at: String,
    val variants: List<Variant>
)