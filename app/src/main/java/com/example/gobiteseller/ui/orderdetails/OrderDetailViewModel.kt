package com.example.gobiteseller.ui.orderdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.OrderByIdModel
import com.example.gobiteseller.data.retrofit.OrderRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class OrderDetailViewModel(private val orderRepository: OrderRepository) : ViewModel() {


    private val orderByIdRequest = MutableLiveData<Resource<OrderByIdModel>>()
    val orderByIdResponse: LiveData<Resource<OrderByIdModel>>
        get() = orderByIdRequest

    fun getOrderById(orderId: String){
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



}