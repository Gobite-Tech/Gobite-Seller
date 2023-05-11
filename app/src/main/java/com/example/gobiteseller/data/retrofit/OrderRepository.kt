package com.example.gobiteseller.data.retrofit

import com.example.gobiteseller.data.model.OrderModel
import com.example.gobiteseller.data.model.OrderModelNew
import retrofit2.Retrofit

class OrderRepository(private val retrofit: Retrofit) {

    val service =  retrofit.create(CustomApi::class.java)

    suspend fun getOrderById(orderId: Int) = service.getOrderById(orderId)

    suspend fun getOrderByShopId(authToken:String) = service.getOrderByShopId(authToken)

    suspend fun getOrderByPagination(shopId: Int,pageNum: Int,pageCnt: Int) = service.getOrderByPagination(shopId,pageNum,pageCnt)

    suspend fun getOrderBySearchItem(shopId: Int,searchItem: String,pageNum: Int,pageCnt: Int) = service.getOrderBySearchItem(shopId,searchItem,pageNum,pageCnt)

    suspend fun updateOrderStatus(id : String ,order: OrderModelNew) = service.updateOrderStatus(id ,order)
}