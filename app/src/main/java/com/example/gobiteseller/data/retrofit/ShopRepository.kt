package com.example.gobiteseller.data.retrofit

import com.example.gobiteseller.data.model.ConfigurationModel
import retrofit2.Retrofit

class ShopRepository(private val retrofit: Retrofit) {

    val service =retrofit.create(CustomApi::class.java)

    suspend fun getShopsNew()=service.getShopNew()

    suspend fun updateShopConfiguration(shopConfigRequest: ConfigurationModel) = service.updateShopConfiguration(shopConfigRequest)

    suspend fun getShopDetailsById(shopId: Int) = service.getShopDetailsById(shopId = shopId)

}