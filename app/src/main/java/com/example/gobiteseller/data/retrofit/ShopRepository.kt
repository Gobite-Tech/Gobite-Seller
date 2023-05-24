package com.example.gobiteseller.data.retrofit

import com.example.gobiteseller.data.model.ConfigurationModel
import com.example.gobiteseller.data.model.Shop
import com.example.gobiteseller.data.model.ShopUpdateRequestTemp
import okhttp3.MultipartBody
import retrofit2.Retrofit

class ShopRepository(private val retrofit: Retrofit) {

    val service =retrofit.create(CustomApi::class.java)

    suspend fun uploadIcon(imagePart: MultipartBody.Part)=service.uploadIcon(imagePart)

    suspend fun deleteShopIcon()=service.deleteShopIcon()

    suspend fun getShop()=service.getShop()
    suspend fun getShopsNew()=service.getShopNew()

    suspend fun updateShop(shop: ShopUpdateRequestTemp)=service.updateShop(shop)

    suspend fun updateShopConfiguration(shopConfigRequest: ConfigurationModel) = service.updateShopConfiguration(shopConfigRequest)

    suspend fun getShopDetailsById(shopId: Int) = service.getShopDetailsById(shopId = shopId)

}