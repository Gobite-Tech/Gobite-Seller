package com.example.gobiteseller.ui.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.util.Log.e
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.gobiteseller.R
import com.example.gobiteseller.data.local.PreferencesHelper
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.LoginRequest
import com.example.gobiteseller.databinding.ActivityLoginBinding
import com.example.gobiteseller.ui.home.HomeActivity
import com.example.gobiteseller.ui.signup.SignUpActivity
import com.example.gobiteseller.utils.AppConstants
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.NullPointerException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var progressDialog: ProgressDialog
    private val viewModel :LoginViewModel by inject()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!preferencesHelper.oauthId.isNullOrEmpty() ){
            e("preferencesHelper.oauthId",preferencesHelper.oauthId.toString())
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }
//        preferencesHelper.oauthId="abc"

        initView()
        setObservers()
        setListener()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val text = "<font color=#000000>Welcome to</font> <br> <font color=#000000>Gobite</font> <font color=#FF4141> Partners</font>"
        binding.textWelcome.text = Html.fromHtml(text)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListener() {

        binding.editPhone.setOnEditorActionListener { v, actionId, event ->
            when(actionId){
                EditorInfo.IME_ACTION_DONE -> {
                    loginRequest()
                    true
                }
                else -> false
            }
        }

        binding.buttonLogin.setOnClickListener {
//            loginRequest()
            viewModel.Login(LoginRequest("gupta@gmail.com","12345678"))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loginRequest(){
        val phoneNo = binding.editPhone.text.toString()
        val email=binding.editEmail.text.toString()
        if (phoneNo.isNotEmpty() && phoneNo.length==10 && phoneNo.matches(Regex("\\d+")) && email.isNotEmpty() &&  android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            preferencesHelper.mobile = phoneNo
            var otp:Int = 0
                try {
                    val secureRandom = SecureRandom.getInstanceStrong()
                    otp = secureRandom.nextInt(900000) + 100000
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                }
//                Toast.makeText(this, "Otp is ${otp}", Toast.LENGTH_SHORT).show()
                Log.e("otp","Otp is ${otp}")
                val intent = Intent(this,OTPActivity::class.java)
                intent.putExtra("CUSTOMER_OTP",otp.toString())
                intent.putExtra("CUSTOMER_MOBILE","+91$phoneNo")
                intent.putExtra("CUSTOMER_EMAIL",email)
                startActivity(intent)
                finish()

        } else {
            Toast.makeText(applicationContext, "Invalid phone number/Email!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setObservers() {
        //getOTP
//        viewModel.performGetOTPStatus.observe(this, Observer { resource ->
//            if (resource != null) {
//                when (resource.status) {
//                    Resource.Status.SUCCESS -> {
//                        if (resource.data != null) {
//                            val OtpResult = resource.data
//
//                            Log.e(
//                                "LoginActivity",
//                                " Result - ${resource.data} and ${resource.data.success} and ${resource.data.message}"
//                            )
//                            progressDialog.dismiss()
//                            val authToken = OtpResult.data.auth_token
//                            preferencesHelper.oauthId = authToken
//                            viewModel.Login(LoginRequestNew("OTP",authToken,"456321"))
//
//                        } else {
//                            progressDialog.dismiss()
//                            Toast.makeText(
//                                applicationContext,
//                                "Something went wrong",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                    Resource.Status.OFFLINE_ERROR -> {
//                        progressDialog.dismiss()
//                        Toast.makeText(
//                            applicationContext,
//                            "No Internet Connection",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    Resource.Status.ERROR -> {
//                        progressDialog.dismiss()
//                        Toast.makeText(applicationContext, "Please Sign Up", Toast.LENGTH_SHORT).show()
//                        Log.e("Sign Up needed", "Failed ${resource.data}")
//                        if (resource.error is NullPointerException){
//                            viewModel.SignUpIn(OTPRequest("SIGNUP",preferencesHelper.mobile!!))
//                        }else{
////                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    Resource.Status.LOADING -> {
//                        progressDialog.setMessage("Logging in...")
//                        progressDialog.show()
//                    }
//                    else -> {}
//                }
//            }
//        })
//
//        //SIGN UP
//        viewModel.performSignUpInStatus.observe(this, Observer { resource ->
//            if (resource != null) {
//                when (resource.status) {
//                    Resource.Status.SUCCESS -> {
//                        if (resource.data != null) {
//                            val loginresult = resource.data
//
//                            Log.e(
//                                "SignUp ka",
//                                " Result - ${resource.data} and ${resource.data.success} and ${resource.data.message}"
//                            )
//
//
//
//                            preferencesHelper.oauthId = loginresult.data.auth_token
//                            startActivity(Intent(applicationContext, SignUpActivity::class.java))
//                            finish()
//
//                            progressDialog.dismiss()
//
//                        } else {
//                            Toast.makeText(
//                                applicationContext,
//                                "Something went wrong",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                    Resource.Status.OFFLINE_ERROR -> {
//                        progressDialog.dismiss()
//                        Toast.makeText(
//                            applicationContext,
//                            "No Internet Connection",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    Resource.Status.ERROR -> {
//                        progressDialog.dismiss()
//                        Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
//                    }
//                    Resource.Status.LOADING -> {
//                        progressDialog.setMessage("Registering ...")
//                        progressDialog.show()
//                    }
//                    else -> {}
//                }
//            }
//        })


        //Login
        viewModel.performLoginStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        if (resource.data != null) {
                            val loginresult = resource.data

                            Log.e(
                                "lgin ka",
                                " Result - ${resource.data} and ${resource.data.success} and ${resource.data.message}"
                            )

                            preferencesHelper.oauthId = loginresult.data.token
                            preferencesHelper.mobile= binding.editPhone.text.toString()
                            e("lgn token:", preferencesHelper.mobile!!)

                            Toast.makeText(applicationContext,"Welcome!!",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(applicationContext, HomeActivity::class.java))
                            finish()

                            progressDialog.dismiss()

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong data khali",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "No Internet Connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "User not found\n Sign Up first", Toast.LENGTH_SHORT).show()
                        val intent= Intent(this,SignUpActivity::class.java)
                        startActivity(intent)
                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Logging in...")
                        progressDialog.show()
                    }
                    else -> {}
                }
            }
        })
    }
}