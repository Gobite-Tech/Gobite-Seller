package com.example.gobiteseller.data.retrofit

import com.example.gobiteseller.data.model.AddItemRequest
import com.example.gobiteseller.data.model.AddVariant
import com.example.gobiteseller.data.model.DeleteRequest
import com.example.gobiteseller.data.model.ItemModel
import com.example.gobiteseller.data.model.Variant
import retrofit2.Retrofit

class ItemRepository(private val retrofit: Retrofit) {

    val service = retrofit.create(CustomApi::class.java)

    //new
    suspend fun addVariantNew(itemId:String,addVariantRequest: AddVariant)=service.addVariant(itemId,addVariantRequest)

    suspend fun updateItemNew(itemId:String,addItemRequest: AddItemRequest)=service.updateItemNew(itemId,addItemRequest)
    suspend fun addItemNew(addItemRequest: AddItemRequest)=service.addItemNew(addItemRequest)
    suspend fun getShopMenuNew()=service.getMenuNew()
    suspend fun deleteItem(deleteRequest: DeleteRequest) = service.deleteItem(deleteRequest)

    //old

    suspend fun getShopMenu(shopId: Int) = service.getShopMenu(shopId);

    suspend fun addItem(item: List<ItemModel>) = service.addItem(item)

    suspend fun updateItem(item: List<ItemModel>) = service.updateItem(item)



    suspend fun unDeleteItem(itemId: Int) = service.unDeleteItem(itemId)
}