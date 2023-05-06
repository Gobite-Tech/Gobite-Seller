package com.example.gobiteseller.ui.home

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.gobiteseller.R
import com.example.gobiteseller.data.local.PreferencesHelper
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.ShopConfigurationModel
import com.example.gobiteseller.databinding.ActivityHomeBinding
import com.example.gobiteseller.ui.signup.SignUpActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.mikepenz.materialdrawer.Drawer
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
//    private val viewModel: OrderViewModel by viewModel()
    private val homeViewModel: HomeViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
//    private lateinit var headerLayout: HeaderLayoutBinding
    private lateinit var drawer: Drawer
    private lateinit var progressDialog: ProgressDialog
    private lateinit var errorSnackBar: Snackbar
    private var shopConfig: ShopConfigurationModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
         progressDialog = ProgressDialog(this)

         initView()
         setObservers()
         getShopDetails()
    }

    private fun initView() {

    }

//    private fun setListeners() {
//        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabReselected(tab: TabLayout.Tab?) {}
//            override fun onTabUnselected(tab: TabLayout.Tab?) {}
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                when (tab?.position) {
//                    0 -> {
//                        val fragment = NewOrdersFragment()
//                        supportFragmentManager.beginTransaction()
//                            .setCustomAnimations(
//                                R.anim.fade_in,
//                                R.anim.fade_out,
//                                R.anim.fade_in,
//                                R.anim.fade_out
//                            )
//                            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
//                            .commit()
//                    }
//                    1 -> {
//                        val fragment = PreparingFragment()
//                        supportFragmentManager.beginTransaction()
//                            .setCustomAnimations(
//                                R.anim.fade_in,
//                                R.anim.fade_out,
//                                R.anim.fade_in,
//                                R.anim.fade_out
//                            )
//                            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
//                            .commit()
//                    }
//                    2 -> {
//                        val fragment = ReadyFragment()
//                        supportFragmentManager.beginTransaction()
//                            .setCustomAnimations(
//                                R.anim.fade_in,
//                                R.anim.fade_out,
//                                R.anim.fade_in,
//                                R.anim.fade_out
//                            )
//                            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
//                            .commit()
//                    }
//                }
//            }
//        })
//
//    }

    private fun setObservers() {


        homeViewModel.performShopStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        if (resource.data != null) {
                            val shopResult = resource.data

                            Log.e(
                                "shop",
                                " Result - ${resource.data} and ${resource.data.success} and ${resource.data.message}"
                            )

                            progressDialog.dismiss()

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong shop khali",
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
                        Toast.makeText(applicationContext, "shop not found\n Sign Up first", Toast.LENGTH_SHORT).show()
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

    private fun getShopDetails() {
        homeViewModel.getShop()
    }
}