package com.example.gobiteseller.ui.home

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.amulyakhare.textdrawable.TextDrawable
import com.example.gobiteseller.R
import com.example.gobiteseller.data.local.PreferencesHelper
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.Shop
import com.example.gobiteseller.data.model.ShopConfigurationModel
import com.example.gobiteseller.databinding.ActivityHomeBinding
import com.example.gobiteseller.databinding.HeaderLayoutBinding
import com.example.gobiteseller.ui.login.LoginActivity
import com.example.gobiteseller.ui.menu.MenuActivity
import com.example.gobiteseller.ui.orderHistory.OrderHistoryActivity
import com.example.gobiteseller.ui.profile.ProfileActivity
import com.example.gobiteseller.ui.shopProfile.ShopProfileActivity
import com.example.gobiteseller.ui.signup.SignUpActivity
import com.example.gobiteseller.utils.AppConstants
import com.example.gobiteseller.utils.EventBus
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
//    private val viewModel: OrderViewModel by viewModel()
    private val homeViewModel: HomeViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var headerLayout: HeaderLayoutBinding
    private lateinit var drawer: Drawer
    private lateinit var progressDialog: ProgressDialog
    private lateinit var errorSnackBar: Snackbar
    private var shopConfig: ShopConfigurationModel? = null
    private lateinit var mShopDetails : Shop
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
         progressDialog = ProgressDialog(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        headerLayout = DataBindingUtil.inflate(
            LayoutInflater.from(applicationContext),
            R.layout.header_layout,
            null,
            false
        )

        binding.imageMenu.setOnClickListener {
            drawer.openDrawer()
        }

        setupMaterialDrawer()
         setObservers()
        setListeners()
         getShopDetails()

    }

    private fun updateHeaderLayoutUI() {
        if(preferencesHelper.name.isNullOrEmpty()){
            headerLayout.textCustomerName.text = "Gobite Partner"
//            headerLayout.textEmail.text = preferencesHelper.name
            headerLayout.imageProfilePic.setImageResource(R.mipmap.ic_launcher)
        }else{
            e("updateHeaderLayoutUI",preferencesHelper.name.toString())
            headerLayout.textCustomerName.text = preferencesHelper.name.toString()
//            headerLayout.textEmail.text = preferencesHelper.email.toString()
            val textDrawable = TextDrawable.builder()
                .buildRound(
                    preferencesHelper.name?.get(0).toString().capitalize(),
                    ContextCompat.getColor(this, R.color.colorAccent)
                )
            headerLayout.imageProfilePic.setImageDrawable(textDrawable)
        }


    }
    private fun initView() {

        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(com.mikepenz.materialize.R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, com.mikepenz.materialize.R.color.accent))
//        binding.imageMenu.setOnClickListener(this)
        binding.textShopName.text = preferencesHelper.name


        var fragment: Fragment
        binding.tabs.visibility = View.VISIBLE
        fragment = NewOrdersFragment()


        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
            .commit()

        setStatusBarHeight()

        if(mShopDetails.icon.isNotEmpty()){
            Picasso.get().load(mShopDetails.icon).placeholder(R.drawable.ic_shop)
                .into(binding.imageCompany)
        }
        updateHeaderLayoutUI()

    }

    private fun setStatusBarHeight() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rectangle = Rect()
                val window = window
                window.decorView.getWindowVisibleDisplayFrame(rectangle)
                val statusBarHeight = rectangle.top
                val layoutParams = headerLayout.statusbarSpaceView.layoutParams
                layoutParams.height = statusBarHeight
                headerLayout.statusbarSpaceView.layoutParams = layoutParams
                Log.d("Home", "status bar height $statusBarHeight")
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun setListeners() {
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        val fragment = NewOrdersFragment()
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                            .commit()
                    }
                    1 -> {
                        val fragment = PreparingFragment()
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                            .commit()
                    }
                    2 -> {
                        val fragment = ReadyFragment()
                        supportFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.fade_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.fade_out
                            )
                            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                            .commit()
                    }
                }
            }
        })

    }

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

                            if(shopResult.data.shop.name !=null){
                                preferencesHelper.currentShop =
                                    shopResult.data.shop.id

                                preferencesHelper.name=shopResult.data.shop.name
                                preferencesHelper.id=shopResult.data.shop.id
                                preferencesHelper.email=shopResult.data.shop.email
                                preferencesHelper.mobile=shopResult.data.shop.mobile
                                mShopDetails=shopResult.data.shop

                                e("preferencesHelper.name",preferencesHelper.currentShop.toString())

                                progressDialog.dismiss()

                                initView()
                            }else{
                                homeViewModel.getShopCurrent()
                            }

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

        homeViewModel.performShopCurrentStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        if (resource.data != null) {
                            val shopResult = resource.data

                            Log.e(
                                "shop",
                                " Result - ${resource.data} and ${resource.data.success} and ${resource.data.message}"
                            )
                                preferencesHelper.currentShop =
                                    shopResult.data.shop.id

                                preferencesHelper.name=shopResult.data.shop.name
                                preferencesHelper.id=shopResult.data.shop.id
                                preferencesHelper.email=shopResult.data.shop.email
                                preferencesHelper.mobile=shopResult.data.shop.mobile
                                mShopDetails=shopResult.data.shop

                                e("preferencesHelper.name",preferencesHelper.currentShop.toString())

                                progressDialog.dismiss()

                                initView()


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

    private fun setupMaterialDrawer() {
        var identifier = 0L
        val profileItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("My Profile")
            .withIcon(R.drawable.ic_drawer_user)
        val shopProfileItem =
            PrimaryDrawerItem().withIdentifier(++identifier).withName("Shop Profile")
                .withIcon(R.drawable.ic_home)
        val ordersItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Past Orders")
            .withIcon(R.drawable.ic_drawer_past_rides)
        val menuItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Shop Menu")
            .withIcon(R.drawable.ic_menu)
//        val contactUsItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Contact Us")
//            .withIcon(R.drawable.ic_drawer_mail)
        val signOutItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Sign out")
            .withIcon(R.drawable.ic_drawer_log_out)
//        val contributorsItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Contributors")
//            .withIcon(R.drawable.ic_drawer_info)

        drawer = DrawerBuilder()
            .withActivity(this)
            .withDisplayBelowStatusBar(false)
            .withHeader(headerLayout.root)
            .withTranslucentStatusBar(true)
            .withCloseOnClick(true)
            .withSelectedItem(-1)
            .addDrawerItems(
//                profileItem,
                shopProfileItem,
                ordersItem,
                menuItem,
//                contributorsItem,
//                contactUsItem,
                DividerDrawerItem(),
                signOutItem
            )
            .withOnDrawerItemClickListener { _, _, drawerItem ->
//                if (profileItem.identifier == drawerItem.identifier) {
//                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
//                }
                if (shopProfileItem.identifier == drawerItem.identifier) {
                    startActivity(Intent(applicationContext, ShopProfileActivity::class.java))
                }
                if (ordersItem.identifier == drawerItem.identifier) {
                    startActivity(Intent(applicationContext, OrderHistoryActivity::class.java))
                }
                if (menuItem.identifier == drawerItem.identifier) {
                    startActivity(Intent(applicationContext, MenuActivity::class.java))
                }
//                if (contributorsItem.identifier == drawerItem.identifier) {
//                    startActivity(Intent(applicationContext, ContributorsActivity::class.java))
//                }
//                if (contactUsItem.identifier == drawerItem.identifier) {
//                    startActivity(Intent(applicationContext,ContactUsActivity::class.java))
//                }
                if (signOutItem.identifier == drawerItem.identifier) {
                    MaterialAlertDialogBuilder(this@HomeActivity)
                        .setTitle("Confirm Sign Out")
                        .setMessage("Are you sure want to sign out?")
                        .setPositiveButton("Yes") { _, _ ->
//                            FirebaseAuth.getInstance().signOut()
//                            preferencesHelper.getShop()?.forEach {
//                                FirebaseMessaging.getInstance()
//                                    .unsubscribeFromTopic(
//                                        AppConstants.NOTIFICATION_TOPIC_SHOP_ZINGER + it.shopModel.id
//                                    );
//                            }
//                            FirebaseMessaging.getInstance()
//                                .unsubscribeFromTopic(AppConstants.NOTIFICATION_TOPIC_GLOBAL);
                            preferencesHelper.clearPreferences()
                            startActivity(Intent(applicationContext, LoginActivity::class.java))
                            finish()
                        }
                        .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
                true
            }
            .build()

//        preferencesHelper.role?.let { role ->
//            if (role == AppConstants.ROLE.DELIVERY.name) {
//                drawer.removeItem(shopProfileItem.identifier)
//                drawer.removeItem(menuItem.identifier)
//            }
//        }
    }


    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this@HomeActivity)
            .setTitle("Exit app?")
            .setMessage("Are you sure want to exit the app?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                startActivity(intent)
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onResume() {
        super.onResume()

        //todo order wale
        e("resume","hua")

//        initView()
        binding.textShopName.text=preferencesHelper.name
        updateHeaderLayoutUI()
    }


}