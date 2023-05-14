package com.example.gobiteseller.ui.shopProfile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gobiteseller.R
import com.example.gobiteseller.data.local.PreferencesHelper
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.ConfigurationModel
import com.example.gobiteseller.data.model.CoverPhoto
import com.example.gobiteseller.data.model.Shop
import com.example.gobiteseller.data.model.ShopConfigurationModel
import com.example.gobiteseller.data.model.ShopUpdateRequestTemp
import com.example.gobiteseller.databinding.ActivityShopProfileBinding
import com.example.gobiteseller.utils.AppConstants
import com.example.gobiteseller.utils.CommonUtils
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ShopProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopProfileBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: ShopProfileViewModel by viewModel()
    private lateinit var progressDialog: ProgressDialog
//    private var shopConfig: ShopConfigurationModel? = null
//    private var shopCoverImageAdapter: ShopCoverImageAdapter? = null
    private var imageList: ArrayList<String> = ArrayList()
    private var isShopLogoClicked = false
    private var isShopCoverImageClicked = false
    private var mStorageRef: StorageReference? = null
    private lateinit var updateConfigurationModel: ConfigurationModel
    private lateinit var successSnackbar: Snackbar
    private lateinit var mShop: Shop
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_profile)
        initView()
        setListener()
        setObserver()
        viewModel.getShop()

    }

    private fun initView() {
//        mStorageRef = FirebaseStorage.getInstance().reference
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_profile)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)

        successSnackbar = Snackbar.make(binding.root, " ", Snackbar.LENGTH_INDEFINITE)
    }

    private fun updateUI() {
//        shopConfig = preferencesHelper.getShop()?.firstOrNull {
//            it.shopModel.id == preferencesHelper.currentShop
//        }

//        imageList.clear()
//        shopConfig?.shopModel?.coverUrls?.let { imageList.addAll(it) }

//        shopCoverImageAdapter =
//            ShopCoverImageAdapter(
//                imageList,
//                preferencesHelper.role,
//                object : ShopCoverImageAdapter.OnItemClickListener {
//                    override fun onItemClick(item: List<String>?, position: Int) {
//                        val intent = Intent(applicationContext, DisplayActivity::class.java)
//                        intent.putExtra(AppConstants.DISPLAY_IMAGE_DETAIL, imageList[position])
//                        startActivity(intent)
//                    }
//                    override fun onDeleteClick(item: List<String>?, position: Int) {
//                        imageList.removeAt(position)
//                        shopCoverImageAdapter?.notifyDataSetChanged()
//                    }
//
//                })
//        binding.recyclerCoverPhoto.layoutManager =
//            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        binding.recyclerCoverPhoto.adapter = shopCoverImageAdapter
        binding.editName.setText(mShop.name)
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
        val sdf2 = SimpleDateFormat("hh:mm a", Locale.US)
        binding.textOpeningTime.text = mShop.opening_time
        binding.textClosingTime.text = mShop.closing_time
        Picasso.get().load(mShop.icon).placeholder(R.drawable.ic_shop)
            .into(binding.imageLogo)
        binding.switchOrders.isChecked = mShop.status == "ACTIVE"
        binding.editAccountHolder.setText((mShop.payment.account_holder))
        binding.editAccountIfsc.setText((mShop.payment.account_ifsc))
        binding.editAccountNumber.setText((mShop.payment.account_number))
//        binding.switchDelivery.isChecked = shopConfig?.configurationModel?.isDeliveryAvailable == 1

        if (binding.switchOrders.isChecked)
            binding.switchOrders.thumbTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.switchSelected
                )
            )
        else
            binding.switchOrders.thumbTintList =
                ColorStateList.valueOf(ContextCompat.getColor(applicationContext,com.mikepenz.materialize.R.color.accent))

        if (binding.switchDelivery.isChecked)
            binding.switchDelivery.thumbTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.switchSelected
                )
            )
        else
            binding.switchDelivery.thumbTintList =
                ColorStateList.valueOf(ContextCompat.getColor(applicationContext, com.mikepenz.materialize.R.color.accent))


//        binding.editMerchantId.setText(shopConfig?.configurationModel?.merchantId.toString())
        binding.editName.setSelection(binding.editName.text.toString().length)
        binding.editDeliveryPrice.setText(
            mShop.avg_serve_time.toString()
        )

        preferencesHelper.role?.let {
            if (it == AppConstants.ROLE.SELLER.name || it == AppConstants.ROLE.DELIVERY.name) {
                binding.imageEditName.visibility = View.GONE
                binding.imageEditDeliveryPrice.visibility = View.GONE
                binding.imageEditOpeningTime.visibility = View.GONE
                binding.imageEditClosingTime.visibility = View.GONE
                binding.textLogo.visibility = View.GONE
//                binding.textCoverPhoto.visibility = View.GONE

                binding.imageEditName.isEnabled = false
                binding.imageEditDeliveryPrice.isEnabled = false
                binding.imageEditOpeningTime.isEnabled = false
                binding.imageEditClosingTime.isEnabled = false
                binding.textLogo.isEnabled = false
//                binding.textCoverPhoto.isEnabled = false

                binding.switchOrders.isClickable = false
                binding.switchDelivery.isClickable = false
                binding.buttonUpdate.visibility = View.GONE
                binding.buttonUpdate.isEnabled = false
            }

//            if(it==AppConstants.ROLE.SHOP_OWNER.name){
//                binding.textMerchantId.visibility = View.VISIBLE
//                binding.layoutMerchantId.visibility = View.VISIBLE
//            }
        }
    }

    private fun setListener() {
        binding.imageClose.setOnClickListener {
            onBackPressed()
        }
        binding.switchDelivery.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                binding.switchDelivery.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.switchSelected
                    )
                )
            else
                binding.switchDelivery.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorAccent
                    )
                )
        }
        binding.switchOrders.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                binding.switchOrders.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.switchSelected
                    )
                )
            else
                binding.switchOrders.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorAccent
                    )
                )
        }

        binding.buttonUpdate.setOnClickListener {
            if (binding.editName.isEnabled) {
                Toast.makeText(this, "Please confirm name change", Toast.LENGTH_LONG).show()
            } else if (binding.editDeliveryPrice.isEnabled) {
                Toast.makeText(this, "Please confirm delivery price change", Toast.LENGTH_LONG).show()
//            }else if (binding.layoutMerchantId.visibility == View.VISIBLE && binding.editMerchantId.isEnabled) {
//                Toast.makeText(this, "Please confirm merchant Id change", Toast.LENGTH_LONG).show()
            }else if (binding.editAccountNumber.isEnabled) {
                Toast.makeText(this, "Please confirm AccountNumber", Toast.LENGTH_LONG).show()
            }
            else if (binding.editAccountHolder.isEnabled) {
                Toast.makeText(this, "Please confirm editAccountHolder", Toast.LENGTH_LONG).show()
            }
            else if (binding.editAccountIfsc.isEnabled) {
                Toast.makeText(this, "Please confirm editAccountIfsc", Toast.LENGTH_LONG).show()
            }
            else {
                val sdf = SimpleDateFormat("HH:mm", Locale.US)
                val sdf2 = SimpleDateFormat("hh:mm", Locale.US)
                val openingTime = sdf2.parse(binding.textOpeningTime.text.toString())
                    ?.let { it1 -> sdf.format(it1) }
                val closingTime = sdf2.parse(binding.textClosingTime.text.toString())
                    ?.let { it1 -> sdf.format(it1) }
                var mid = " "

                mShop.payment.account_number=binding.editAccountNumber.text.toString()!!
                mShop.payment.account_ifsc=binding.editAccountIfsc.text.toString()!!
                mShop.payment.account_holder=binding.editAccountHolder.text.toString()!!
//                if(binding.layoutMerchantId.visibility == View.VISIBLE){
//                    mid = binding.editMerchantId.text.toString()
//                }else{
//                    shopConfig?.configurationModel?.merchantId?.let {
//                        mid = it
//                    }
//                }
//                val shopModel = ShopModel(
//                    photoUrl = if (photoUrl.isNullOrEmpty()) shopConfig?.shopModel?.photoUrl else photoUrl,
//                    closingTime = closingTime,
//                    openingTime = openingTime,
//                    name = binding.editName.text.toString(),
//                    coverUrls = imageList,
//                    mobile = shopConfig?.shopModel?.mobile,
//                    id = shopConfig?.shopModel?.id
//                )
//                updateConfigurationModel = ConfigurationModel(
//                    deliveryPrice = binding.editDeliveryPrice.text.toString().toDouble(),
//                    isDeliveryAvailable = if (binding.switchDelivery.isChecked) 1 else 0,
//                    isOrderTaken = if (binding.switchOrders.isChecked) 1 else 0,
//                    merchantId = mid,
//                    shopModel = shopModel
//                )


                val shopModel=ShopUpdateRequestTemp(
                    mShop.payment.account_holder,
                    mShop.payment.account_ifsc,
                    mShop.payment.account_number,
                    "IITK",
                    binding.editDeliveryPrice.text.toString().toInt(),
                    "GROCERY",
                    "IITK",
                    closingTime!!,
                    mShop.cover_photos,
                    "mShop.description",
                    "ddlogesh@gmail.com",
                    "24AAACC1206D1ZM",
                    mShop.icon,
                    mShop.address.lat,
                    mShop.address.lng,
                    "9176786586",
                    binding.editName.text.toString(),
                    openingTime!!,
                    "AZXPL1001E",
                    "800000",
                    "UP",
                    "IITK",
                    listOf("veg","non-veg"),
                    "25545880"
                )

                viewModel.updateShop(shopModel)
            }
        }

        binding.imageEditName.setOnClickListener {
            if (!binding.editName.isEnabled)
                binding.imageEditName.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditName.setImageResource(R.drawable.ic_edit)
            binding.editName.isEnabled = !(binding.editName.isEnabled)
        }


        binding.imageEditAccountHolder.setOnClickListener {
            if (!binding.editAccountHolder.isEnabled)
                binding.imageEditAccountHolder.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditAccountHolder.setImageResource(R.drawable.ic_edit)
            binding.editAccountHolder.isEnabled = !(binding.editAccountHolder.isEnabled)
        }

        binding.imageEditAccountIfsc.setOnClickListener {
            if (!binding.editAccountIfsc.isEnabled)
                binding.imageEditAccountIfsc.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditAccountIfsc.setImageResource(R.drawable.ic_edit)
            binding.editAccountIfsc.isEnabled = !(binding.editAccountIfsc.isEnabled)
        }

        binding.imageEditAccountNumber.setOnClickListener {
            if (!binding.editAccountNumber.isEnabled)
                binding.imageEditAccountNumber.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditAccountNumber.setImageResource(R.drawable.ic_edit)
            binding.editAccountNumber.isEnabled = !(binding.editAccountNumber.isEnabled)
        }


        binding.imageEditDeliveryPrice.setOnClickListener {
            if (!binding.editDeliveryPrice.isEnabled)
                binding.imageEditDeliveryPrice.setImageResource(R.drawable.ic_check)
            else
                binding.imageEditDeliveryPrice.setImageResource(R.drawable.ic_edit)
            binding.editDeliveryPrice.isEnabled = !(binding.editDeliveryPrice.isEnabled)
        }
//        binding.imageEditMerchantId.setOnClickListener {
//            if (!binding.editMerchantId.isEnabled)
//                binding.imageEditMerchantId.setImageResource(R.drawable.ic_check)
//            else
//                binding.imageEditMerchantId.setImageResource(R.drawable.ic_edit)
//            binding.editMerchantId.isEnabled = !(binding.editMerchantId.isEnabled)
//        }
        binding.imageEditOpeningTime.setOnClickListener {
            var openingTime =
                SimpleDateFormat("HH:mm", Locale.US).parse( "00:00")

//            if(mShop.opening_time.isNotEmpty()){
//                openingTime =
//                    SimpleDateFormat("HH:mm", Locale.US).parse(mShop.opening_time )
//            }
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    val time = CommonUtils.TimeConversion24to12(hourOfDay, minute)
                    binding.textOpeningTime.text = time
                }, openingTime.hours, openingTime.minutes, false
            )
            timePickerDialog.show()
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    android.R.color.tab_indicator_text
                )
            )
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
        }
        binding.imageEditClosingTime.setOnClickListener {
            var closingTime =
                SimpleDateFormat("HH:mm", Locale.US).parse("00:00")

//            if(mShop.closing_time.isNotEmpty()){
//                closingTime =
//                    SimpleDateFormat("HH:mm", Locale.US).parse(mShop.closing_time )
//            }
            val timePickerDialog = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    val time = CommonUtils.TimeConversion24to12(hourOfDay, minute)
                    binding.textClosingTime.text = time
                }, closingTime.hours, closingTime.minutes, false
            )
            timePickerDialog.show()
            timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    android.R.color.tab_indicator_text
                )
            )
            timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(applicationContext, R.color.colorAccent))
        }
        binding.textLogo.setOnClickListener {
            isShopLogoClicked = true
            ImagePicker.with(this)
                .galleryOnly()
                .compress(1024)
                .cropSquare()
                .start()
        }
//        binding.textCoverPhoto.setOnClickListener {
//            isShopCoverImageClicked = true;
//            ImagePicker.with(this)
//                .galleryOnly()
//                .compress(1024)
//                .crop(16f, 9f)
//                .start()
//        }
//        binding.imageLogo.setOnClickListener {
//            val intent = Intent(applicationContext, DisplayActivity::class.java)
//            val photoUrl =
//                if (photoUrl.isNullOrEmpty()) shopConfig?.shopModel?.photoUrl else photoUrl
//            intent.putExtra(AppConstants.DISPLAY_IMAGE_DETAIL, photoUrl)
//            startActivity(intent)
//        }


    }

    @SuppressLint("SuspiciousIndentation")
    private fun setObserver() {
        viewModel.iconRequestResponse.observe(this, androidx.lifecycle.Observer { resource ->
            if (resource != null) {
                progressDialog.dismiss()
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        resource.data?.let {
//                            if (isShopCoverImageClicked) {
//                                imageList.add(resource.data)
//                                shopCoverImageAdapter?.notifyDataSetChanged()
//                                isShopCoverImageClicked = !isShopCoverImageClicked
//                            } else if (isShopLogoClicked) {
//                                photoUrl = resource.data
                                Picasso.get().load(resource.data.data.icon)
                                    .placeholder(R.drawable.ic_shop)
                                    .into(binding.imageLogo)
                                isShopLogoClicked = !isShopLogoClicked
//                            }
                        }
                    }
                    Resource.Status.EMPTY -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Try again!! Error Occurred " + resource.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Try again!! Error Occurred \n" + resource.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "No Internet Connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Updating...")
                        progressDialog.show()
                    }

                    else -> {}
                }
            }
        })

        viewModel.performUpdateShopStatus.observe(
            this,
            androidx.lifecycle.Observer { resource ->
                if (resource != null) {
                    when (resource.status) {
                        Resource.Status.SUCCESS -> {

                            preferencesHelper.name= resource.data?.data?.shop?.name
//                            preferencesHelper.getShop()?.let { shopConfigurationList ->
//                                for (i in shopConfigurationList)
//                                    if (i.shopModel.id == updateConfigurationModel.shopModel?.id) {
//                                        i.shopModel = updateConfigurationModel.shopModel!!
//                                        i.configurationModel = updateConfigurationModel
//                                    }
//                                preferencesHelper.shop = Gson().toJson(shopConfigurationList)
//                            }
                            progressDialog.dismiss()
                            successSnackbar.setText("Shop Profile Updated")
                            successSnackbar.show()
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
                            resource.message?.let {
                                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                            } ?: run {
                                Toast.makeText(
                                    applicationContext,
                                    "Update Failed Try again later",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        Resource.Status.LOADING -> {
                            progressDialog.setMessage("Updating...")
                            progressDialog.show()
                        }

                        else -> {}
                    }
                }
            })


        viewModel.performShopStatus.observe(this, androidx.lifecycle.Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        resource.data?.data?.let { latestShopData ->
                            mShop=latestShopData.shop

//                            preferencesHelper.getShop()?.let { shopConfigurationList ->
//
//                                for (i in shopConfigurationList) {
//                                    if (i.shopModel.id == preferencesHelper.currentShop) {
//                                        i.shopModel = latestShopData.shopModel
//                                        i.configurationModel = latestShopData.configurationModel
//                                        i.ratingModel = latestShopData.ratingModel
//                                    }
//                                }
//                                preferencesHelper.shop = Gson().toJson(shopConfigurationList)
                                updateUI()
                           // }
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
                        resource.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        } ?: run {
                            Toast.makeText(
                                applicationContext,
                                "Failed to fetch data, Try again later",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Fetching shop data...")
                        progressDialog.show()
                    }

                    else -> {}
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImagePicker.REQUEST_CODE) {
                val fileUri = data?.data
                val file: File? = ImagePicker.getFile(data)

                val imagePart = MultipartBody.Part.createFormData("icon_file", file?.name, RequestBody.create(
                    MediaType.parse("image/png"), file!!))


                viewModel.uploadIcon(imagePart)
//                var storageReference: StorageReference? = null
//                if (isShopLogoClicked) {
//                    storageReference =
//                        mStorageRef?.child("profileImage/" + mShop.id + "/" + file?.name + Calendar.getInstance().time)
//
//                } else if (isShopCoverImageClicked) {
//                    storageReference =
//                        mStorageRef?.child("coverImage/" + mShop.id + "/" + file?.name + Calendar.getInstance().time)
//                }
//                if (storageReference != null) {
//                    if (fileUri != null) {
////                        viewModel.uploadPhotoToFireBase(storageReference, fileUri)
//                    }
//                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}