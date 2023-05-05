package com.example.gobiteseller.data.retrofit

import com.example.gobiteseller.data.model.ConfigurationModel
import com.example.gobiteseller.data.model.ItemModel
import com.example.gobiteseller.data.model.OrderItemListModel
import com.example.gobiteseller.data.model.OrderModel
import com.example.gobiteseller.data.model.ShopConfigurationModel
import com.example.gobiteseller.data.model.UserModel
import com.example.gobiteseller.data.model.UserShopListModel
import retrofit2.Response
import retrofit2.http.*

interface CustomApi  {

    // user repository
    @POST("/user/seller")
    suspend fun login(@Body userModel: UserModel): Response<UserShopListModel>

    @PATCH("/user")
    suspend fun updateProfile(@Body userModel: UserModel): Response<String>

    @PATCH("/user/notif")
    suspend fun updateFcmToken(@Body userModel: UserModel): Response<String>

    // shop repository
    @PATCH("/shop/config")
    suspend fun updateShopConfiguration(@Body shopConfigRequest: ConfigurationModel): Response<String>

    @GET("/shop/{shopId}")
    suspend fun getShopDetailsById(@Path("shopId") shopId: Int): Response<ShopConfigurationModel>

    // Item Repository
    @GET("/menu/shop/{shopId}")
    suspend fun getShopMenu(@Path("shopId") shopId: Int): Response<List<ItemModel>>

    @POST("/menu")
    suspend fun addItem(@Body itemModelList: List<ItemModel>): Response<String>

    @PATCH("/menu")
    suspend fun updateItem(@Body item: List<ItemModel>): Response<String>

    @DELETE("/menu/delete/{itemId}")
    suspend fun deleteItem(@Path("itemId") itemId: Int): Response<String>

    @DELETE("/menu/undelete/{itemId}")
    suspend fun unDeleteItem(@Path("itemId") itemId: Int): Response<String>


    // Order Repository

    @GET("/order/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: Int): Response<OrderItemListModel>

    @GET("/order/seller/{shopId}/{pageNum}/{pageCnt}")
    suspend fun getOrderByPagination(@Path("shopId") shopId: Int,@Path("pageNum") pageNum: Int,@Path("pageCnt") pageCnt: Int): Response<List<OrderItemListModel>>

    @GET("/order/{shopId}/{searchItem}/{pageNum}/{pageCount}")
    suspend fun getOrderBySearchItem(@Path("shopId") shopId: Int, @Path("searchItem") searchItem: String, @Path("pageNum") pageNum: Int, @Path("pageCount") pageCnt: Int): Response<List<OrderItemListModel>>

    @GET("/order/seller/{shopId}")
    suspend fun getOrderByShopId(@Path("shopId") shopId: Int): Response<List<OrderItemListModel>>

    @PATCH("/order/status")
    suspend fun updateOrderStatus(@Body order: OrderModel):Response<String>


}