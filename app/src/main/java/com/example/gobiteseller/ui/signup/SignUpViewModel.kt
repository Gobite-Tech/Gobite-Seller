package com.example.gobiteseller.ui.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.LoginRequest
import com.example.gobiteseller.data.model.LoginResponse
import com.example.gobiteseller.data.model.SignUpRequest
import com.example.gobiteseller.data.model.SignUpResponse
import com.example.gobiteseller.data.model.otpRequest
import com.example.gobiteseller.data.model.otpResponse
import com.example.gobiteseller.data.retrofit.UserRespository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class SignUpViewModel(private val userRepository: UserRespository): ViewModel() {

    //SIGNUP
    private val performSignUp = MutableLiveData<Resource<SignUpResponse>>()
    val performSignUpStatus: LiveData<Resource<SignUpResponse>>
        get() = performSignUp

    fun signUp(SignUpRequestNew: SignUpRequest) {
        viewModelScope.launch {
            try {
                performSignUp.value = Resource.loading()
                val response = userRepository.signUpNew(SignUpRequestNew)
                performSignUp.value = Resource.success(response.body()!!)
            } catch (e: Exception) {
                Log.e("Signup failed", "${e.message}")
                if (e is UnknownHostException) {
                    performSignUp.value = Resource.offlineError()
                } else {
                    performSignUp.value = Resource.error(e)
                }
            }
        }
    }

    //otp
    private val performOtp = MutableLiveData<Resource<otpResponse>>()
    val performOtpStatus: LiveData<Resource<otpResponse>>
        get() = performOtp
    fun otp(otpReq:otpRequest){
        viewModelScope.launch {
            try {
                performOtp.value = Resource.loading()
                val response = userRepository.otpNew(otpReq)
                performOtp.value = Resource.success(response.body()!!)
            } catch (e: Exception) {
                Log.e("otp failed", "${e.message}")
                if (e is UnknownHostException) {
                    performOtp.value = Resource.offlineError()
                } else {
                    performOtp.value = Resource.error(e)
                }
            }
        }
    }

}