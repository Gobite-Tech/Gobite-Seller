package com.example.gobiteseller.data.retrofit

import com.example.gobiteseller.data.model.ItemModel
import retrofit2.Retrofit

class ItemRepository(private val retrofit: Retrofit) {

    val service = retrofit.create(CustomApi::class.java)

    suspend fun getShopMenu(shopId: Int) = service.getShopMenu(shopId);

    suspend fun addItem(item: List<ItemModel>) = service.addItem(item)

    suspend fun updateItem(item: List<ItemModel>) = service.updateItem(item)

    suspend fun deleteItem(itemId: Int) = service.deleteItem(itemId)

    suspend fun unDeleteItem(itemId: Int) = service.unDeleteItem(itemId)
}