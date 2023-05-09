package com.example.gobiteseller.ui.shopProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.MenuModel
import com.example.gobiteseller.data.model.Shop
import com.example.gobiteseller.data.model.ShopUpdateRequestTemp
import com.example.gobiteseller.data.model.Shops
import com.example.gobiteseller.data.retrofit.ShopRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class ShopProfileViewModel (private val shopRepository: ShopRepository) : ViewModel(){


    //get shop
    private var performShop= MutableLiveData<Resource<Shops>>()
    val performShopStatus: LiveData<Resource<Shops>>
        get() = performShop

    fun getShop(){
        viewModelScope.launch {
            try {
                performShop.value = Resource.loading()
                val response = shopRepository.getShop()
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

    //update shop

    private var performUpdateShop= MutableLiveData<Resource<Shops>>()
    val performUpdateShopStatus: LiveData<Resource<Shops>>
        get() = performUpdateShop

    fun updateShop(shop: ShopUpdateRequestTemp){
        viewModelScope.launch {
            try {
                performUpdateShop.value = Resource.loading()
                val response = shopRepository.updateShop(shop)
                performUpdateShop.value = Resource.success(response.body()!!)
            }catch (e:Exception){
                if (e is UnknownHostException) {
                    performUpdateShop.value = Resource.offlineError()
                } else {
                    performUpdateShop.value = Resource.error(e)
                }
            }
        }
    }
}