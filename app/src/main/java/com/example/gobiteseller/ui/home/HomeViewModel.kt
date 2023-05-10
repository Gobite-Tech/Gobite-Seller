package com.example.gobiteseller.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobiteseller.data.local.PreferencesHelper
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.LoginResponse
import com.example.gobiteseller.data.model.OrderByIdModel
import com.example.gobiteseller.data.model.OrderModel
import com.example.gobiteseller.data.model.OrderModelNew
import com.example.gobiteseller.data.model.Shops
import com.example.gobiteseller.data.retrofit.OrderRepository
import com.example.gobiteseller.data.retrofit.ShopRepository
import kotlinx.coroutines.launch
import retrofit2.Response
import java.net.UnknownHostException

class HomeViewModel(private val shopRepository: ShopRepository,private val orderRepository: OrderRepository, private val preferencesHelper: PreferencesHelper) : ViewModel() {

    // //update order
    private val updateOrder = MutableLiveData<Resource<OrderByIdModel>>()
    val updateOrderResponse: LiveData<Resource<OrderByIdModel>>
        get() = updateOrder

    fun updateOrder(id : String , orderModel: OrderModelNew){
        viewModelScope.launch {
            try{
                updateOrder.value = Resource.loading()
                val response = orderRepository.updateOrderStatus(id ,orderModel)

                if(response.isSuccessful) {
                    updateOrder.value = Resource.success(response.body()!!)
                    preferencesHelper.orderStatusChanged = true
                }
                else{
                    updateOrder.value = Resource.error(message=response.message())
                }
            }catch (e: Exception){
                if (e is UnknownHostException) {
                    updateOrder.value = Resource.offlineError()
                } else {
                    updateOrder.value = Resource.error(e)
                }
            }
        }
    }

    //getShops
   private var performShop=MutableLiveData<Resource<Shops>>()
    val performShopStatus: LiveData<Resource<Shops>>
        get() = performShop

    fun getShop(){
        viewModelScope.launch {
            try {
                performShop.value = Resource.loading()
                val response = shopRepository.getShopsNew()
                performShop.value = Resource.success(response.body()!!)
            }catch (e:Exception){
                if (e is UnknownHostException) {
                    performShop.value = Resource.offlineError()
                } else {
                    performShop.value = Resource.error(e)
                }
            }
        }
    }


}