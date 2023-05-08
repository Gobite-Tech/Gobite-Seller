package com.example.gobiteseller.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.MenuModel
import com.example.gobiteseller.data.model.Shops
import com.example.gobiteseller.data.retrofit.ItemRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class MenuViewModel(private val itemRepository: ItemRepository) : ViewModel() {


    //getMenu
    private var performMenu= MutableLiveData<Resource<MenuModel>>()
    val performMenuStatus: LiveData<Resource<MenuModel>>
        get() = performMenu

    fun getMenu(){
        viewModelScope.launch {
            try {
                performMenu.value = Resource.loading()
                val response = itemRepository.getShopMenuNew()
                performMenu.value = Resource.success(response.body()!!)
            }catch (e:Exception){
                if (e is UnknownHostException) {
                    performMenu.value = Resource.offlineError()
                } else {
                    performMenu.value = Resource.error(e)
                }
            }
        }
    }

}