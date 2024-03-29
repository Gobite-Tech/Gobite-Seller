package com.example.gobiteseller.ui.orderHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.OrderByShopIdModel
import com.example.gobiteseller.data.retrofit.OrderRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class OrderHistoryViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    private val orderByShopId = MutableLiveData<Resource<OrderByShopIdModel>>()
    val orderByShopIdResponse: LiveData<Resource<OrderByShopIdModel>>
        get() = orderByShopId

    fun getOrderByShopId(authID: String,nxtID: String) {
        viewModelScope.launch {
            try {
                orderByShopId.value = Resource.loading()
                val response = orderRepository.getOrderByShopId(authID)
                if (response.isSuccessful) {
                    orderByShopId.value = Resource.success(response.body()!!)
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

    //pagination

    private val pageOrderByShopId = MutableLiveData<Resource<OrderByShopIdModel>>()
    val pageOrderByShopIdResponse: LiveData<Resource<OrderByShopIdModel>>
        get() = pageOrderByShopId

    fun getPageOrderByShopId(authID: String,nxtID: String) {
        viewModelScope.launch {
            try {
                pageOrderByShopId.value = Resource.loading()
                val response = orderRepository.getOrderByPagination(authID,nxtID)
                if (response.isSuccessful) {
                    pageOrderByShopId.value = Resource.success(response.body()!!)
                } else {
                    orderByShopId.value = Resource.empty()
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    pageOrderByShopId.value = Resource.offlineError()
                } else {
                    pageOrderByShopId.value = Resource.error(e)
                }
            }
        }
    }

}