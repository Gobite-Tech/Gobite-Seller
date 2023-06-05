package com.example.gobiteseller.data.retrofit

import android.util.Log.e
import com.example.gobiteseller.data.model.OrderModel
import com.example.gobiteseller.data.model.OrderModelNew
import com.example.gobiteseller.data.model.SmsRequest
import retrofit2.Retrofit

class OrderRepository(private val retrofit: Retrofit) {

    val service =  retrofit.create(CustomApi::class.java)
    suspend fun sendSMS(smsRequest: SmsRequest)=service.sendSMS("https://api.enablex.io/sms/v1/messages/",smsRequest)
    suspend fun getOrderById(orderId: String) = service.getOrderById(orderId)

    suspend fun getOrderByShopId(authToken:String) = service.getOrderByShopId(authToken)

    suspend fun getOrderByPagination(authToken:String,nxtpageToken:String) = service.getOrderByPagination(authToken,25,"DESC",nxtpageToken)

    suspend fun getOrderBySearchItem(shopId: Int,searchItem: String,pageNum: Int,pageCnt: Int) = service.getOrderBySearchItem(shopId,searchItem,pageNum,pageCnt)

    suspend fun updateOrderStatus(id : String ,order: OrderModelNew) = service.updateOrderStatus(id ,order)
}