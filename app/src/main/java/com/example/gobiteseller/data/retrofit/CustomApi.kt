package com.example.gobiteseller.data.retrofit

import com.example.gobiteseller.data.model.AddItemRequest
import com.example.gobiteseller.data.model.AddItemResponse
import com.example.gobiteseller.data.model.AddVariant
import com.example.gobiteseller.data.model.ConfigurationModel
import com.example.gobiteseller.data.model.DeleteRequest
import com.example.gobiteseller.data.model.DeleteResponse
import com.example.gobiteseller.data.model.ItemModel
import com.example.gobiteseller.data.model.LoginRequest
import com.example.gobiteseller.data.model.LoginResponse
import com.example.gobiteseller.data.model.MenuModel
import com.example.gobiteseller.data.model.OrderByIdModel
import com.example.gobiteseller.data.model.OrderByShopIdModel
import com.example.gobiteseller.data.model.OrderItemListModel
import com.example.gobiteseller.data.model.OrderModel
import com.example.gobiteseller.data.model.OrderModelNew
import com.example.gobiteseller.data.model.Shop
import com.example.gobiteseller.data.model.ShopConfigurationModel
import com.example.gobiteseller.data.model.ShopUpdateRequestTemp
import com.example.gobiteseller.data.model.Shops
import com.example.gobiteseller.data.model.SignUpRequest
import com.example.gobiteseller.data.model.SignUpResponse
import com.example.gobiteseller.data.model.UserModel
import com.example.gobiteseller.data.model.UserShopListModel
import com.example.gobiteseller.data.model.Variant
import com.example.gobiteseller.data.model.otpRequest
import com.example.gobiteseller.data.model.otpResponse
import retrofit2.Response
import retrofit2.http.*

interface CustomApi  {

    //new stuff

    @GET("v1/shop")
    suspend fun getShop():Response<Shops>

    @PUT("v1/shop")
    suspend fun updateShop(@Body shop:ShopUpdateRequestTemp):Response<Shops>

    @POST("v1/shop/item/{itemId}/variant")
    suspend fun addVariant(@Path("itemId") itemId: String,@Body addVariantRequest: AddVariant):Response<AddItemResponse>

    @POST("v1/shop/item/delete")
    suspend fun deleteItem(@Body deleteRequest: DeleteRequest): Response<DeleteResponse>

    @PUT("v1/shop/item/{itemId}")
    suspend fun updateItemNew(@Path("itemId") itemId: String,@Body addItemRequest: AddItemRequest):Response<AddItemResponse>

    @POST("v1/shop/item")
    suspend fun addItemNew(@Body addItemRequest: AddItemRequest):Response<AddItemResponse>

    @GET("v1/shop/item/list")
    suspend fun getMenuNew():Response<MenuModel>

    @GET("v1/shop/new")
    suspend fun getShopNew():Response<Shops>

    @POST("v1/auth/signup")
    suspend fun otpNew(@Body otpReq:otpRequest):Response<otpResponse>

    @POST("v1/auth/login")
    suspend fun loginNew(@Body loginModel:LoginRequest):Response<LoginResponse>

    @POST("v1/auth/otp")
    suspend fun signUpNew(@Body signupModel:SignUpRequest):Response<SignUpResponse>


    //old stuff

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



    @DELETE("/menu/undelete/{itemId}")
    suspend fun unDeleteItem(@Path("itemId") itemId: Int): Response<String>


    // Order Repository

    @GET("v1/shop/order/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: Int): Response<OrderByIdModel>

    @GET("/order/seller/{shopId}/{pageNum}/{pageCnt}")
    suspend fun getOrderByPagination(@Path("shopId") shopId: Int,@Path("pageNum") pageNum: Int,@Path("pageCnt") pageCnt: Int): Response<List<OrderItemListModel>>

    @GET("/order/{shopId}/{searchItem}/{pageNum}/{pageCount}")
    suspend fun getOrderBySearchItem(@Path("shopId") shopId: Int, @Path("searchItem") searchItem: String, @Path("pageNum") pageNum: Int, @Path("pageCount") pageCnt: Int): Response<List<OrderItemListModel>>

    @GET("v1/shop/order")
    suspend fun getOrderByShopId(@Header("Authorization")  authToken:String): Response<OrderByShopIdModel>

    @PUT("v1/shop/order/{orderid}")
    suspend fun updateOrderStatus(@Path("orderid") id : String , @Body order: OrderModelNew) : Response<OrderByIdModel>
}