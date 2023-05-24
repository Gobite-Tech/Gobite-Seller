package com.example.gobiteseller.ui.menuItem

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.e
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gobiteseller.R
import com.example.gobiteseller.data.local.PreferencesHelper
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.AddItemRequest
import com.example.gobiteseller.data.model.AddVariant
import com.example.gobiteseller.data.model.CategoryItemListModel
import com.example.gobiteseller.data.model.DataXXXX
import com.example.gobiteseller.data.model.DeleteRequest
import com.example.gobiteseller.databinding.ActivityMenuItemsBinding
import com.example.gobiteseller.databinding.BottomSheetAddEditMenuItemBinding
import com.example.gobiteseller.utils.AppConstants
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class MenuItemsActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMenuItemsBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var menuAdapter: MenuItemAdapter
    private var menuItemList: ArrayList<DataXXXX> = ArrayList()
    private var category: String = ""
    private val viewModel: MenuItemViewModel by viewModel()
    private var changedItemImageUrl = ""
    private lateinit var dialogBinding: BottomSheetAddEditMenuItemBinding
    private var mStorageRef: StorageReference? = null
    private var variantPrice:Double=0.0
    private var mItemID=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_items)

        getArgs()
        initView()
        setListener()
        setObserver()
        setupRecyclerView()


    }

    private fun getArgs() {
        val categoryItemList = Gson().fromJson(
            intent.getStringExtra(AppConstants.CATEGORY_ITEM_DETAIL),
            CategoryItemListModel::class.java
        )
        menuItemList = categoryItemList.itemModelList
        category = categoryItemList.category
        menuItemList.sortBy {
            it.id!!
        }
    }

    private fun setupRecyclerView() {
        menuAdapter =
            MenuItemAdapter(
                this,
                menuItemList,
                preferencesHelper.role,
                object : MenuItemAdapter.OnItemClickListener {
                    override fun onEditClick(itemModel: DataXXXX?, position: Int) {
                        if (binding.buttonSaveChanges.visibility == View.VISIBLE) {
                            Toast.makeText(
                                applicationContext,
                                "Please save current changes before proceeding",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            showItemAddEditBottomSheet(itemModel)
                        }
                    }
                    override fun onDeleteClick(itemModel: DataXXXX?, position: Int) {
                        if (binding.buttonSaveChanges.visibility == View.VISIBLE) {
                            Toast.makeText(
                                applicationContext,
                                "Please save current changes before proceeding",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            MaterialAlertDialogBuilder(this@MenuItemsActivity)
                                .setTitle("Delete Item")
                                .setMessage("Do you want to delete " + itemModel?.name + " ?")
                                .setPositiveButton("Yes") { dialog, _ ->
                                    itemModel?.id?.let {
                                        viewModel.deleteItem(DeleteRequest(it,"asehi"))
                                    }
                                    for(i in menuItemList.indices){
                                            if(menuItemList[i].id==itemModel?.id){
                                                menuItemList.removeAt(i)
                                                break;
                                            }
                                    }
                                    menuAdapter.notifyDataSetChanged()
                                }
                                .setNegativeButton("No") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                    }

                    override fun onSwitchChange(itemModel: DataXXXX?, position: Int) {
                        binding.buttonSaveChanges.visibility = View.VISIBLE
                        menuItemList.get(position).status = if(menuItemList.get(position).status=="active") "inactive" else "active"
                    }
                })

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerMenuItems.layoutManager = layoutManager
        binding.recyclerMenuItems.adapter = menuAdapter
        menuAdapter.notifyDataSetChanged()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu_items)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        binding.textCategoryName.text = category
//        mStorageRef = FirebaseStorage.getInstance().reference
        binding.switchDelivery.thumbTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switchNotSelected))

        when {
            menuItemList.isEmpty() -> {
                binding.switchDelivery.visibility = View.GONE
                binding.textAddItem.visibility = View.GONE
                binding.textAddFirstItem.visibility = View.VISIBLE
//                binding.animationView.visibility = View.VISIBLE
//                binding.animationView.loop(true)
//                binding.animationView.setAnimation("empty_animation.json")
//                binding.animationView.playAnimation()
            }
            menuItemList.none { it.status == "inactive"} -> {
                binding.switchDelivery.isChecked = true
                binding.switchDelivery.thumbTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switchSelected))
            }
            menuItemList.none { it.status == "active"} -> {
                binding.switchDelivery.isChecked = false
                binding.switchDelivery.thumbTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switchNotSelected))
            }
        }

        preferencesHelper.role?.let {
            if (it == AppConstants.ROLE.SELLER.name || it == AppConstants.ROLE.DELIVERY.name) {
                binding.textAddItem.visibility = View.GONE
                binding.textAddItem.isEnabled = false
            }
        }
    }

    private fun setListener() {
        binding.imageClose.setOnClickListener { onBackPressed() }
        binding.switchDelivery.setOnClickListener {
            val isChecked = binding.switchDelivery.isChecked
            if (isChecked) {
                binding.switchDelivery.thumbTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switchSelected))
            } else {
                binding.switchDelivery.thumbTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switchNotSelected))
            }
            binding.buttonSaveChanges.visibility = View.VISIBLE
            for (menu in menuItemList) {
                menu.status = if (isChecked) "active" else "inactive"
            }
            menuAdapter.notifyDataSetChanged()
        }
        binding.textAddItem.setOnClickListener {
//            if (binding.buttonSaveChanges.visibility == View.VISIBLE) {
//                Toast.makeText(
//                    applicationContext,
//                    "Please save current changes before proceeding",
//                    Toast.LENGTH_LONG
//                ).show()
//            } else {
                showItemAddEditBottomSheet()
            //}
        }
        binding.textAddFirstItem.setOnClickListener {
            showItemAddEditBottomSheet()
        }
        binding.buttonSaveChanges.setOnClickListener {
            binding.buttonSaveChanges.visibility = View.GONE
//            viewModel.updateItem(menuItemList)
        }
    }

    private fun setObserver() {
        viewModel.menuRequestResponse.observe(this, androidx.lifecycle.Observer { resource ->
            println("resource: $resource")
            if (resource != null) {
                when (resource.status) {

                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        resource.data?.let {
                            menuItemList.clear()
                            it.data?.filter { item -> item.category == category }
                                ?.let { it1 -> menuItemList.addAll(it1) }

                            if (menuItemList.isEmpty()) {
                                binding.switchDelivery.visibility = View.GONE
                                binding.textAddItem.visibility = View.GONE
                                binding.textAddFirstItem.visibility = View.VISIBLE
//                                binding.animationView.visibility = View.VISIBLE
//                                binding.animationView.loop(true)
//                                binding.animationView.setAnimation("empty_animation.json")
//                                binding.animationView.playAnimation()
                            } else {
//                                binding.switchDelivery.visibility = View.VISIBLE
                                binding.textAddFirstItem.visibility = View.GONE
                                binding.animationView.visibility = View.GONE
//                                binding.animationView.cancelAnimation()


                                        binding.textAddItem.visibility = View.VISIBLE


                                if (menuItemList.none { item -> item.status == "inactive" }) {
                                    binding.switchDelivery.isChecked = true
                                    binding.switchDelivery.thumbTintList = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            this,
                                            R.color.switchSelected
                                        )
                                    )
                                } else {
                                    binding.switchDelivery.isChecked = false
                                    binding.switchDelivery.thumbTintList = ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            this,
                                            R.color.switchNotSelected
                                        )
                                    )
                                }
                            }
                            menuAdapter.notifyDataSetChanged()
                        }
                    }

                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Try again!! Error Occurred " + resource.message,
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
            binding.buttonSaveChanges.visibility = View.GONE
        })

        viewModel.menuIconRequestResponse.observe(this, androidx.lifecycle.Observer { resource ->
            println("resource: $resource")
            if (resource != null) {
                progressDialog.dismiss()
                when (resource.status) {

                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        resource.data?.let {
                            changedItemImageUrl = resource.data?.data?.item?.icon!!
                            dialogBinding.imageItem.visibility = View.VISIBLE
                            dialogBinding.textChangeImage.text = "UPDATE IMAGE"
                            Picasso.get().load(resource.data?.data?.item?.icon!!)
                                .placeholder(R.drawable.ic_food)
                                .into(dialogBinding.imageItem)
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
                            "Try again!! Error Occurred " + resource.message,
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
                        progressDialog.dismiss()
                        progressDialog.setMessage("Updating...")
                        progressDialog.show()
                    }

                    else -> {}
                }
            }
        })

        viewModel.addItemRequestResponse.observe(this, androidx.lifecycle.Observer { resource ->
            if (resource != null) {
                when (resource.status) {

                    Resource.Status.SUCCESS -> {
                        viewModel.addVariant(resource.data?.data?.item?.id!!, AddVariant(
                            "variant_food",
                            variantPrice,
                            "0"
                        )
                        )
                        progressDialog.dismiss()
                        preferencesHelper.currentShop.let { viewModel.getMenu() }
                    }

                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Try again!! Error Occurred " + resource.message,
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
                binding.buttonSaveChanges.visibility = View.GONE
            }
        })

        viewModel.updateItemRequestResponse.observe(this, androidx.lifecycle.Observer { resource ->
            if (resource != null) {
                when (resource.status) {

                    Resource.Status.SUCCESS -> {

                        e("ae","ae")
                        progressDialog.dismiss()
                        resource.data?.let {
                            for(i in menuItemList.indices){
                                if(menuItemList[i].id==it.data.item.id){
                                    val item=DataXXXX(it.data.item.category,
                                        it.data.item.cover_photos ,it.data.item.created_at,it.data.item.description,it.data.item.filterable_fields,it.data.item.icon
                                        ,it.data.item.id,it.data.item.item_type,it.data.item.meta_data,it.data.item.name,it.data.item.status
                                        ,it.data.item.updated_at,it.data.item.variants
                                    )
                                    menuItemList[i]=item
                                    menuAdapter.notifyDataSetChanged()

//                                    if(binding.textAddFirstItem.isVisible){
//                                        Toast.makeText(this, "first item added", Toast.LENGTH_SHORT).show()
//                                       finish()
//                                    }
                                }
                            }
                            //if (preferencesHelper.updateItemRequest != "null") {
//                                val listType = object : TypeToken<List<DataXXXX?>?>() {}.type
//                                updateList(
//                                    ArrayList(Gson().fromJson<List<DataXXXX>>(it.data.item.toString(), listType))
//                                )
                            //} else {
                                //preferencesHelper.currentShop.let { viewModel.getMenu() }
                            //}
                        }
                    }

                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Try again!! Error Occurred " + resource.message,
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
                binding.buttonSaveChanges.visibility = View.GONE
            }
        })



        //variant observe
        viewModel.addVariantsResponse.observe(this, androidx.lifecycle.Observer { resource ->
            if (resource != null) {
                when (resource.status) {

                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        resource.data?.let {

                            for(i in menuItemList.indices){
                                if(menuItemList[i].id==it.data.item.id){
                                    val item=DataXXXX(it.data.item.category,
                                        it.data.item.cover_photos ,it.data.item.created_at,it.data.item.description,it.data.item.filterable_fields,it.data.item.icon
                                        ,it.data.item.id,it.data.item.item_type,it.data.item.meta_data,it.data.item.name,it.data.item.status
                                        ,it.data.item.updated_at,it.data.item.variants
                                    )
                                    menuItemList[i]=item
                                    menuAdapter.notifyDataSetChanged()
                                }
                            }

//                            if (preferencesHelper.updateItemRequest != "null") {
////                                val listType = object : TypeToken<List<DataXXXX?>?>() {}.type
////                                updateList(
////                                    ArrayList(Gson().fromJson<List<DataXXXX>>(preferencesHelper.updateItemRequest, listType))
////                                )
//                            } else {
//                                preferencesHelper.currentShop.let { viewModel.getMenu() }
//                            }
                        }
                    }

                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Try again!! Error Occurred " + resource.message,
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
                binding.buttonSaveChanges.visibility = View.GONE
            }
        })


        viewModel.deleteItemRequestResponse.observe(this, androidx.lifecycle.Observer { resource ->
            if (resource != null) {
                when (resource.status) {

                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()

                        if (preferencesHelper.deleteItemRequest != -1) {
//                            val tempList =
//                                menuItemList.filter { it.id != preferencesHelper.deleteItemRequest }
//                            menuItemList.clear()
//                            menuItemList.addAll(tempList)
                            menuAdapter.notifyDataSetChanged()
                        } else {
                            preferencesHelper.currentShop.let { viewModel.getMenu() }
                        }
                    }

                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Try again!! Error Occurred " + resource.message,
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
                binding.buttonSaveChanges.visibility = View.GONE
            }
        })
    }

    private fun updateList(itemModelList: ArrayList<DataXXXX>) {

        itemModelList.sortedBy { item -> item.id }
        menuItemList.sortBy {
            it.id!!
        }
        var i = 0
        var k = 0
        while (i < menuItemList.size) {
            if (k < itemModelList.size && menuItemList[i].id == itemModelList[k].id)
                menuItemList[i] = itemModelList.get(k++)
            i++
        }
        menuAdapter.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    private fun showItemAddEditBottomSheet(item: DataXXXX? = null) {

        dialogBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.bottom_sheet_add_edit_menu_item,
                null,
                false
            )

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        Picasso.get().load(R.drawable.ic_food)
            .placeholder(R.drawable.ic_food)
            .into(dialogBinding.imageItem)

        if (item != null) {
            mItemID=item.id
            dialogBinding.editItemName.setText(item.name)
            dialogBinding.editItemPrice.setText(item.variants[item.variants.size-1].price.toInt().toString())
            dialogBinding.switchAvailability.isChecked = item.status == "active"
//            dialogBinding.switchNonVeg.isChecked = item.isVeg == 0
            dialogBinding.imageItem.visibility = View.VISIBLE
            item.icon.let {
                Picasso.get().load(item.icon).placeholder(R.drawable.ic_food)
                    .into(dialogBinding.imageItem)
            }
            dialogBinding.buttonAddEdit.text = "UPDATE"
            dialogBinding.textChangeImage.text = "UPDATE IMAGE"

            if (dialogBinding.switchAvailability.isChecked)
                dialogBinding.switchAvailability.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.switchSelected
                    )
                )
            else
                dialogBinding.switchAvailability.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(applicationContext, R.color.switchNotSelected)
                )
            if (dialogBinding.switchNonVeg.isChecked)
                dialogBinding.switchNonVeg.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(applicationContext, R.color.nonVegetarianSelect)
                )
            else
                dialogBinding.switchNonVeg.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.switchNotSelected
                    )
                )
        } else {
            dialogBinding.switchAvailability.isChecked = true
            dialogBinding.switchAvailability.thumbTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.switchSelected
                )
            )
            dialogBinding.textChooseItemPhoto.visibility=View.GONE
            dialogBinding.layoutChooseItemPhoto.visibility = View.GONE
            dialogBinding.imageItem.visibility = View.GONE
            dialogBinding.textChangeImage.text = "ADD IMAGE"
        }
        dialogBinding.switchAvailability.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                item?.status = "active"
                dialogBinding.switchAvailability.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(applicationContext, R.color.switchSelected)
                )
            } else {
                item?.status = "inactive"
                dialogBinding.switchAvailability.thumbTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(applicationContext, R.color.switchNotSelected)
                )
            }
        }
//        dialogBinding.switchNonVeg.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                item?.isVeg = 0
//                dialogBinding.switchNonVeg.thumbTintList = ColorStateList.valueOf(
//                    ContextCompat.getColor(
//                        applicationContext,
//                        R.color.nonVegetarianSelect
//                    )
//                )
//            } else {
//                item?.isVeg = 1
//                dialogBinding.switchNonVeg.thumbTintList = ColorStateList.valueOf(
//                    ContextCompat.getColor(
//                        applicationContext,
//                        R.color.switchNotSelected
//                    )
//                )
//            }
//        }
        dialogBinding.buttonAddEdit.setOnClickListener { v ->

            val isAvailable = if (dialogBinding.switchAvailability.isChecked) "active" else "inactive"
            val isVeg = if (dialogBinding.switchNonVeg.isChecked) 0 else 1
            val name = dialogBinding.editItemName.text.toString()
            var price = -1.0
            if (dialogBinding.editItemPrice.text.toString().isNotEmpty() && dialogBinding.editItemPrice.text.toString()
                    .matches(Regex("\\d+"))
            ) {
                price = dialogBinding.editItemPrice.text.toString().toDouble()
            }
            val itemUrl =
                if (changedItemImageUrl.isEmpty()) item?.icon else changedItemImageUrl

            if (dialogBinding.editItemPrice.text.toString().isEmpty() || price == -1.0) {
                Toast.makeText(
                    this,
                    "Please fill the item name and price details",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (item != null) {
                var itemUpdateRequest = AddItemRequest(
                    category,
                    isAvailable,
                    item.description,
                    item.filterable_fields,
                    item.item_type,
                    name
                )
                viewModel.updateItem(item.id,itemUpdateRequest)

                e("lele","${price.toString()} % ${item.variants[item.variants.size-1].price}")
                if(price !=item.variants[item.variants.size-1].price){
                    //todo addUpdate variant
                    e("lele",item.id)

                    viewModel.addVariant(item.id, AddVariant(
                        item.variants[item.variants.size-1].reference_id,
                        price,
                        item.variants[item.variants.size-1].value+"0"
                    )
                    )
                }
                dialog.dismiss()
            } else {
//                if (changedItemImageUrl.isEmpty()) {
//                    Toast.makeText(this, "Add an image", Toast.LENGTH_SHORT).show()
//                } else {
                    val itemInsertRequest = AddItemRequest(
                        category,
                        isAvailable,
                        "desc",
                        listOf("veg"),
                        "food",
                        name
                    )
                     variantPrice=price
                    viewModel.addItem(itemInsertRequest)
                    dialog.dismiss()
               // }
            }
        }
        dialogBinding.textChangeImage.setOnClickListener { v ->
            ImagePicker.with(this)
                .galleryOnly()
                .cropSquare()
                .start()
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra(AppConstants.INTENT_UPDATED_ITEM, Gson().toJson(menuItemList))
        intent.putExtra(AppConstants.INTENT_UPDATED_ITEM_CATEGORY, category)
        setResult(35, intent)
        // NOTE: do not put on back pressed first, else result code will be 0 instead of 35
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ImagePicker.REQUEST_CODE) {
                val fileUri = data?.data
                val file: File? = ImagePicker.getFile(data)

                val imagePart = MultipartBody.Part.createFormData("icon_file", file?.name, RequestBody.create(
                    MediaType.parse("image/png"), file!!))


                viewModel.uploadIcon(mItemID,imagePart)

//                var storageReference: StorageReference? = null
//                storageReference =
//                    mStorageRef?.child("itemImage/" + preferencesHelper.id + "/" + file?.name + Calendar.getInstance().time)
//                if (storageReference != null) {
//                    if (fileUri != null) {
////                        viewModel.uploadPhotoToFireBase(storageReference, fileUri)
//
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

