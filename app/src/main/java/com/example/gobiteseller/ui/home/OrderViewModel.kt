package com.example.gobiteseller.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobiteseller.data.local.PreferencesHelper
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.OrderByIdModel
import com.example.gobiteseller.data.model.OrderByShopIdModel
import com.example.gobiteseller.data.retrofit.OrderRepository
import com.example.gobiteseller.data.retrofit.ShopRepository
import com.example.gobiteseller.data.retrofit.UserRespository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRespository,
    private val shopRepository: ShopRepository,
    private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    private val orderByIdRequest = MutableLiveData<Resource<OrderByIdModel>>()
    val orderByIdResponse: LiveData<Resource<OrderByIdModel>>
        get() = orderByIdRequest

    fun getOrderById(orderId: Int) {
        viewModelScope.launch {
            try {
                orderByIdRequest.value = Resource.loading()
                val response = orderRepository.getOrderById(orderId)
                if (response.isSuccessful)
                    orderByIdRequest.value = Resource.success(response.body()!!)
                else {
                    orderByIdRequest.value = Resource.error(message = response.message())
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    orderByIdRequest.value = Resource.offlineError()
                } else {
                    orderByIdRequest.value = Resource.error(e)
                }
            }
        }
    }

    /*****************************************************************************/

    private val orderByShopId = MutableLiveData<Resource<OrderByShopIdModel>>()
    val orderByShopIdResponse: LiveData<Resource<OrderByShopIdModel>>
        get() = orderByShopId

    fun getOrderByShopId(authID: String) {
        viewModelScope.launch {
            try {
                orderByShopId.value = Resource.loading()
                val response = orderRepository.getOrderByShopId(authID)
                if (response.isSuccessful) {
                    orderByShopId.value = Resource.success(response.body()!!)
                    preferencesHelper.orderStatusChanged = false
                } else {
                    orderByShopId.value = Resource.empty()
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    orderByShopId.value = Resource.offlineError()
                } else {
                    orderByShopId.value = Resource.error(e)
                }
            }
        }
    }

    /*****************************************************************************/

//    private val getShopDetail = MutableLiveData<Resource<ShopConfigurationModel>>()
//    val getShopDetailResponse: LiveData<Resource<ShopConfigurationModel>>
//        get() = getShopDetail
//
//    fun getShopDetail(id: Int) {
//        viewModelScope.launch {
//            try {
//                getShopDetail.value = Resource.loading()
//                val response = shopRepository.getShopDetailsById(id)
//                if (response.code == 1)
//                    getShopDetail.value = Resource.success(response)
//                else
//                    getShopDetail.value = Resource.error(message = response.message)
//            } catch (e: Exception) {
//                println("fetch stats failed ${e.message}")
//                if (e is UnknownHostException) {
//                    getShopDetail.value = Resource.offlineError()
//                } else {
//                    getShopDetail.value = Resource.error(e)
//                }
//            }
//        }
//    }
}