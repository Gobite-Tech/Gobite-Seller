package com.example.gobiteseller.ui.login

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.gobiteseller.R
import com.example.gobiteseller.data.local.PreferencesHelper
import com.example.gobiteseller.databinding.ActivityLoginBinding
import com.example.gobiteseller.utils.AppConstants
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()
        setListener()

//        if (!preferencesHelper.oauthId.isNullOrEmpty() && preferencesHelper.id!=-1) {
//            startActivity(Intent(applicationContext, HomeActivity::class.java))
//            finish()
//        }
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val text = "<font color=#000000>Welcome to</font> <br> <font color=#000000>GoBite</font> <font color=#FF4141> Partner</font>"
        binding.textWelcome.text = Html.fromHtml(text)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
    }

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
            loginRequest()
        }
    }

    private fun loginRequest(){
        val phoneNo = binding.editPhone.text.toString()
        if (phoneNo.isNotEmpty() && phoneNo.length==10 && phoneNo.matches(Regex("\\d+"))) {
//            val intent = Intent(applicationContext, OTPActivity::class.java)
//            intent.putExtra(AppConstants.PREFS_SELLER_MOBILE, "+91"+phoneNo)
//            intent.putExtra(AppConstants.SELLER_SHOP,"NULL")
//            startActivity(intent)
        } else {
            Toast.makeText(applicationContext, "Invalid phone number!", Toast.LENGTH_SHORT).show()
        }
    }
}