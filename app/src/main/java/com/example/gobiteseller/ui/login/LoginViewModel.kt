package com.example.gobiteseller.ui.login

import android.util.Log.e
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.EnablexOTPModel
import com.example.gobiteseller.data.model.LoginRequest
import com.example.gobiteseller.data.model.LoginResponse
import com.example.gobiteseller.data.model.SMSResponse
import com.example.gobiteseller.data.retrofit.UserRespository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class LoginViewModel(private val userRepository: UserRespository): ViewModel() {

    //LOGIN
    private val performLogin = MutableLiveData<Resource<LoginResponse>>()
    val performLoginStatus: LiveData<Resource<LoginResponse>>
        get() = performLogin

    fun Login(loginRequestNew: LoginRequest) {
        viewModelScope.launch {
            try {
                performLogin.value = Resource.loading()
                val response = userRepository.loginNew(loginRequestNew)
                e("login response", "${response.body()}")
                performLogin.value = Resource.success(response.body()!!)
                e("login response", "${performLogin.value}")
            } catch (e: Exception) {
                e("login failed" ,"${e.message}")
                if (e is UnknownHostException) {
                    performLogin.value = Resource.offlineError()
                } else {
                    performLogin.value = Resource.error(e)
                }
            }
        }
    }


    //Send OTP
    private val performSendOTP = MutableLiveData<Resource<SMSResponse>>()
    val performSendOTPStatus: LiveData<Resource<SMSResponse>>
        get() = performSendOTP

    fun sendOTP(sendOtpModel: EnablexOTPModel) {
        viewModelScope.launch {
            try {
                performSendOTP.value = Resource.loading()
                val response = userRepository.sendOTP(sendOtpModel)
                performSendOTP.value = Resource.success(response.body()!!)

            } catch (e: Exception) {
                println("login failed ${e.message}")
                if (e is UnknownHostException) {
                    performSendOTP.value = Resource.offlineError()
                } else {
                    performSendOTP.value = Resource.error(e)
                }
            }
        }
    }


}