package com.example.gobiteseller.ui.signup

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.gobiteseller.R
import com.example.gobiteseller.data.local.PreferencesHelper
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.LoginRequest
import com.example.gobiteseller.data.model.SignUpRequest
import com.example.gobiteseller.data.model.otpRequest
import com.example.gobiteseller.data.model.otpResponse
import com.example.gobiteseller.databinding.ActivityLoginBinding
import com.example.gobiteseller.ui.home.HomeActivity
import com.example.gobiteseller.ui.login.LoginActivity
import com.example.gobiteseller.ui.login.LoginViewModel
import org.koin.android.ext.android.inject

class SignUpActivity : AppCompatActivity() {

    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var progressDialog: ProgressDialog
    private val viewModel : SignUpViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        setObservers()

        findViewById<Button>(R.id.button_register).setOnClickListener {
            signUpRequest()
        }


    }

    private fun signUpRequest() {
        val phoneNo = findViewById<EditText>(R.id.edit_number).text.toString()
        val email=findViewById<EditText>(R.id.edit_email_signup).text.toString()
        if (phoneNo.isNotEmpty() && phoneNo.length==10 && phoneNo.matches(Regex("\\d+")) && email.isNotEmpty() &&  android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            viewModel.signUp(SignUpRequest(email,phoneNo,"SIGNUP"))
        } else {
            Toast.makeText(applicationContext, "Invalid phone number/Email!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setObservers() {
        //getOTP
        viewModel.performOtpStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        if (resource.data != null) {
                            val OtpResult = resource.data

                            Log.e(
                                "otp",
                                " Result - ${resource.data} and ${resource.data.success} and ${resource.data.message}"
                            )
                            progressDialog.dismiss()
                            val authToken = OtpResult.data.token
                            preferencesHelper.oauthId = authToken

                            startActivity(Intent(applicationContext, LoginActivity::class.java))
                            finish()


                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong",
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
                        Toast.makeText(applicationContext, " Sign Up failed", Toast.LENGTH_SHORT).show()
                        Log.e("Sign Up needed", "Failed ${resource.data}")

                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("pls wait...")
                        progressDialog.show()
                    }
                    else -> {}
                }
            }
        })
//
        //SIGN UP
        viewModel.performSignUpStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        if (resource.data != null) {
                            val loginresult = resource.data

                            Log.e(
                                "SignUp ka",
                                " Result - ${resource.data} and ${resource.data.success} and ${resource.data.message}"
                            )

                            preferencesHelper.oauthId = loginresult.data.auth_token

                            val name= findViewById<EditText>(R.id.edit_name).text.toString()
                            viewModel.otp(otpRequest(loginresult.data.auth_token,name,"769036","12345678","12345678"))

//                            startActivity(Intent(applicationContext, LoginActivity::class.java))
//                            finish()

                            progressDialog.dismiss()

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong",
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
                        Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Registering ...")
                        progressDialog.show()
                    }
                    else -> {}
                }
            }
        })



    }
}