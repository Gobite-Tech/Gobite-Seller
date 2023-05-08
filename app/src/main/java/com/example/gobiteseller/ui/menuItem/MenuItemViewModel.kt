package com.example.gobiteseller.ui.menuItem

import android.util.Log.e
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobiteseller.data.local.PreferencesHelper
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.AddItemRequest
import com.example.gobiteseller.data.model.AddItemResponse
import com.example.gobiteseller.data.model.AddVariant
import com.example.gobiteseller.data.model.DeleteRequest
import com.example.gobiteseller.data.model.DeleteResponse
import com.example.gobiteseller.data.model.MenuModel
import com.example.gobiteseller.data.model.Variant
import com.example.gobiteseller.data.retrofit.ItemRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class MenuItemViewModel(
    private val itemRepository: ItemRepository, private val preferencesHelper: PreferencesHelper) : ViewModel() {


    //get menu data
    private val menuRequest = MutableLiveData<Resource<MenuModel>>()
    val menuRequestResponse: LiveData<Resource<MenuModel>>
        get() = menuRequest

    fun getMenu() {
        viewModelScope.launch {
            try {
                menuRequest.value = Resource.loading()
                val response = itemRepository.getShopMenuNew()
                if (response.isSuccessful) {
                    menuRequest.value = Resource.success(response.body()!!)
                } else {
                    menuRequest.value = Resource.empty()
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    menuRequest.value = Resource.offlineError()
                } else {
                    menuRequest.value = Resource.error(e)
                }
            }
        }
    }

    //add item

    private val itemRequest = MutableLiveData<Resource<AddItemResponse>>()
    val addItemRequestResponse: LiveData<Resource<AddItemResponse>>
        get() = itemRequest

    fun addItem(addItemRequest: AddItemRequest) {
        viewModelScope.launch {
            try {
                itemRequest.value = Resource.loading()
                val response = itemRepository.addItemNew(addItemRequest)
                if (response.isSuccessful) {
                    itemRequest.value = Resource.success(response.body()!!)
                } else {
                    itemRequest.value = Resource.empty()
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    itemRequest.value = Resource.offlineError()
                } else {
                    itemRequest.value = Resource.error(e)
                }
            }
        }
    }

    //update item

    private val updateItemRequest = MutableLiveData<Resource<AddItemResponse>>()
    val updateItemRequestResponse: LiveData<Resource<AddItemResponse>>
        get() = updateItemRequest

    fun updateItem(itemId:String,addItemRequest: AddItemRequest) {
        viewModelScope.launch {
            try {
                updateItemRequest.value = Resource.loading()
                val response = itemRepository.updateItemNew(itemId,addItemRequest)
                if (response.isSuccessful) {
                    updateItemRequest.value = Resource.success(response.body()!!)
                } else {
                    updateItemRequest.value = Resource.empty()
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    updateItemRequest.value = Resource.offlineError()
                } else {
                    updateItemRequest.value = Resource.error(e)
                }
            }
        }
    }

    //delete item


    private val deleteItemRequest = MutableLiveData<Resource<DeleteResponse>>()
    val deleteItemRequestResponse: LiveData<Resource<DeleteResponse>>
        get() = deleteItemRequest

    fun deleteItem(deleteRequest: DeleteRequest) {
        viewModelScope.launch {
            try {
                deleteItemRequest.value = Resource.loading()
                val response = itemRepository.deleteItem(deleteRequest)
                if (response.isSuccessful) {
                    deleteItemRequest.value = Resource.success(response.body()!!)
                } else {
                    deleteItemRequest.value = Resource.empty()
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    deleteItemRequest.value = Resource.offlineError()
                } else {
                    deleteItemRequest.value = Resource.error(e)
                }
            }
        }
    }

    //add variants

    private val addVariantsRequest = MutableLiveData<Resource<AddItemResponse>>()
    val addVariantsResponse: LiveData<Resource<AddItemResponse>>
        get() = addVariantsRequest


    fun addVariant(itemId:String,addVariantRequest: AddVariant) {
        viewModelScope.launch {
            try {
                addVariantsRequest.value = Resource.loading()

                val response = itemRepository.addVariantNew(itemId,addVariantRequest)
                if (response.isSuccessful) {
                    addVariantsRequest.value = Resource.success(response.body()!!)
                    e("lele","nhi chuda")
                } else {
                    addVariantsRequest.value = Resource.empty()
                    e("lele",response.message())
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    addVariantsRequest.value = Resource.offlineError()
                } else {
                    addVariantsRequest.value = Resource.error(e)
                }
            }
        }
    }




}