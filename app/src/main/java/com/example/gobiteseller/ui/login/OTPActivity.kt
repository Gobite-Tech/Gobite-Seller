package com.example.gobiteseller.ui.login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.gobiteseller.R
import com.example.gobiteseller.data.local.PreferencesHelper
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.DataZ
import com.example.gobiteseller.data.model.EnablexOTPModel
import com.example.gobiteseller.data.model.LoginRequest
import com.example.gobiteseller.databinding.ActivityOtpactivityBinding
import com.example.gobiteseller.ui.home.HomeActivity
import com.example.gobiteseller.ui.signup.SignUpActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class OTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpactivityBinding
    private val viewModel by viewModel<LoginViewModel>()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var number: String? = null
    private var email: String? = null
    private var otp = ""
    lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpactivity)

        getArgs()
        initView()
        setListener()
        setObservers()
        number?.let {
            countDownTimer.start()
            sendOtp(it) }


    }

    private fun getArgs() {
        number = intent.getStringExtra("CUSTOMER_MOBILE")
        email=intent.getStringExtra("CUSTOMER_EMAIL")
        otp = intent.getStringExtra("CUSTOMER_OTP")!!
        Log.e("Number testing", number!!)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_otpactivity)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
//        auth = FirebaseAuth.getInstance()
        binding.textOtpSent.text =
            "Enter the six digit OTP which has been sent to your mobile number: $number"
    }

    private fun sendOtp(number: String) {
        val enablexotp = EnablexOTPModel(
            from = "Blueve",
            to = ArrayList<String>().apply { add(number) },
            type = "sms",
            data_coding = "plain",
            campaign_id = "5622674",
            template_id = "499224922",
            validity = "30",
            data = DataZ(var1 = otp)
        )

        viewModel.sendOTP(enablexotp)
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Cancel process?")
            .setMessage("Are you sure want to cancel the OTP process?")
            .setPositiveButton("Yes") { dialog, which ->
                super.onBackPressed()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
            .show()
    }

    private fun setListener() {
        binding.buttonLogin.setOnClickListener {
            if (binding.editOtp.text.toString()
                    .isNotEmpty() && binding.editOtp.text.toString().length == 6
            ) {
                if(binding.editOtp.text.toString() == otp){
                    viewModel.Login(LoginRequest(email!!,"12345678"))
                }else{
                    Toast.makeText(this,"Invalid OTP",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Invalid OTP",Toast.LENGTH_SHORT).show()
            }
        }
        binding.imageClose.setOnClickListener {
            onBackPressed()
        }

        countDownTimer = object : CountDownTimer(30000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                binding.textResendOtp.setText("Resend OTP (" + millisUntilFinished / 1000 + ")")
            }

            override fun onFinish() {
                binding.textResendOtp.setText("Resend OTP")
                binding.textResendOtp.isEnabled = true
            }
        }

        binding.textResendOtp.setOnClickListener {
            countDownTimer.start()
            binding.textResendOtp.isEnabled = false
            number?.let { it1 -> sendOtp(it1) }
        }
    }

    private fun setObservers(){

        //sendOTP
        viewModel.performSendOTPStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        if (resource.data != null && resource.data.result == 0) {
                            Toast.makeText(this, "OTP sent", Toast.LENGTH_SHORT).show()
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
                        progressDialog.setMessage("Sending OTP...")
                        progressDialog.show()
                    }
                    else -> {}
                }
            }
        })



        //lg
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
                        val intent= Intent(this, SignUpActivity::class.java)
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