package com.example.gobiteseller.ui.shopProfile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.DeleteIconResponse
import com.example.gobiteseller.data.model.IconResponse
import com.example.gobiteseller.data.model.MenuModel
import com.example.gobiteseller.data.model.Shop
import com.example.gobiteseller.data.model.ShopUpdateRequestTemp
import com.example.gobiteseller.data.model.Shops
import com.example.gobiteseller.data.retrofit.ShopRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
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


    //setIcon
    private val iconRequest = MutableLiveData<Resource<IconResponse>>()
    val iconRequestResponse: LiveData<Resource<IconResponse>>
        get() = iconRequest

    fun uploadIcon(imagePart: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                iconRequest.value = Resource.loading()
                val response = shopRepository.uploadIcon(imagePart)
                Log.e("ic", response.toString())
                if (response.isSuccessful) {
                    iconRequest.value = Resource.success(response.body()!!)
                } else {
                    iconRequest.value = Resource.empty()
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    iconRequest.value = Resource.offlineError()
                } else {
                    iconRequest.value = Resource.error(e)
                }
            }
        }
    }



    //deleteSHopIcon
    private val deleteIconRequest = MutableLiveData<Resource<DeleteIconResponse>>()
    val deleteiconRequestResponse: LiveData<Resource<DeleteIconResponse>>
        get() = deleteIconRequest

    fun deleteShopIcon() {
        viewModelScope.launch {
            try {
                deleteIconRequest.value = Resource.loading()
                val response = shopRepository.deleteShopIcon()
                Log.e("ic", response.toString())
                if (response.isSuccessful) {
                    deleteIconRequest.value = Resource.success(response.body()!!)
                } else {
                    deleteIconRequest.value = Resource.empty()
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    deleteIconRequest.value = Resource.offlineError()
                } else {
                    deleteIconRequest.value = Resource.error(e)
                }
            }
        }
    }


}