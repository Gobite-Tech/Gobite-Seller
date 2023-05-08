package com.example.gobiteseller.ui.menu

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gobiteseller.R
import com.example.gobiteseller.data.local.PreferencesHelper
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.CategoryItemListModel
import com.example.gobiteseller.data.model.DataXXXX
import com.example.gobiteseller.data.model.MenuModel
import com.example.gobiteseller.databinding.ActivityMenuBinding
import com.example.gobiteseller.databinding.BottomSheetAddCategoryBinding
import com.example.gobiteseller.ui.menuItem.MenuItemsActivity
import com.example.gobiteseller.utils.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: MenuViewModel by viewModel()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var categoryAdapter: CategoryAdapter
    private var categoryItemList: ArrayList<CategoryItemListModel> = ArrayList()
    private lateinit var errorSnackBar: Snackbar
    private var categoryHashMap: HashMap<String, ArrayList<DataXXXX>> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        initView()
        setListener()
        setObservers()
        setUpRecyclerView()


    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_menu)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        errorSnackBar.setAction("Try Again") {
            viewModel.getMenu()
        }
    }

    private fun setUpRecyclerView() {
        categoryAdapter =
            CategoryAdapter(categoryItemList, object : CategoryAdapter.OnItemClickListener {
                override fun onItemClick(
                    categoryItemListModel: CategoryItemListModel?,
                    position: Int
                ) {
                    val intent = Intent(applicationContext, MenuItemsActivity::class.java)
                    intent.putExtra(
                        AppConstants.CATEGORY_ITEM_DETAIL,
                        Gson().toJson(categoryItemListModel)
                    )
                    startActivityForResult(intent, AppConstants.REQUEST_UPDATED_MENU_ITEMS)
                }
            })
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerCategory.layoutManager = layoutManager
        binding.recyclerCategory.adapter = categoryAdapter
        viewModel.getMenu()
    }


    private fun setListener() {
        binding.imageClose.setOnClickListener {
            onBackPressed()
        }
        binding.textAddCategory.setOnClickListener {
            showCategoryAdditionBottomSheet()
        }
    }

    private fun showCategoryAdditionBottomSheet() {
        val dialogBinding: BottomSheetAddCategoryBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.bottom_sheet_add_category,
                null,
                false
            )
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()

        dialogBinding.editCategory.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    insertCategoryRequest(dialogBinding, dialog)
                    true
                }
                else -> false
            }
        }
        dialogBinding.buttonConfirm.setOnClickListener {
            insertCategoryRequest(dialogBinding, dialog)
        }
    }

    private fun insertCategoryRequest(
        dialogBinding: BottomSheetAddCategoryBinding,
        dialog: BottomSheetDialog
    ) {
        if (dialogBinding.editCategory.text.toString().isEmpty() ||
            !dialogBinding.editCategory.text.toString().matches(Regex("^[a-zA-Z]*\$"))
        ) {
            Toast.makeText(this, "Category name is empty or incorrect format ", Toast.LENGTH_LONG).show()
        } else {
            val category = CategoryItemListModel(
                dialogBinding.editCategory.text.toString().toUpperCase(),
                ArrayList()
            )
            categoryHashMap[category.category] = ArrayList()
            categoryItemList.add(category)
            categoryAdapter.notifyDataSetChanged()
            dialog.dismiss()
            val intent = Intent(applicationContext, MenuItemsActivity::class.java)
            intent.putExtra(AppConstants.CATEGORY_ITEM_DETAIL, Gson().toJson(category))
            startActivityForResult(intent, AppConstants.REQUEST_UPDATED_MENU_ITEMS)
        }
    }

    private fun setObservers() {
        viewModel.performMenuStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        categoryItemList.clear()
                        resource.data?.data?.let {
                            val itemList = it.sortedByDescending { item -> item.category }
                            categoryHashMap = HashMap()
                            for (item in itemList) {
                                if (categoryHashMap.containsKey(item.category)) {
                                    categoryHashMap[item.category]?.add(item)
                                } else {
                                    categoryHashMap[item.category] = ArrayList(listOf(item))
                                }
                            }
                            for (category in categoryHashMap.keys) {
                                categoryHashMap[category]?.let { it1 ->
                                    categoryItemList.add(CategoryItemListModel(category, it1))
                                }
                            }
                            categoryAdapter.notifyDataSetChanged()
                            binding.layoutStates.visibility = View.GONE
                            binding.animationView.visibility = View.GONE
                            binding.animationView.cancelAnimation()
                            errorSnackBar.dismiss()
                        }
                    }

                    Resource.Status.ERROR -> {
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("order_failed_animation.json")
                        binding.animationView.playAnimation()
                        errorSnackBar.setText("Something went wrong")
                        Handler().postDelayed({ errorSnackBar.show() }, 500)
                    }

                    Resource.Status.LOADING -> {
                        categoryItemList.clear()
                        categoryAdapter.notifyDataSetChanged()
                        binding.layoutStates.visibility = View.VISIBLE
                        binding.animationView.visibility = View.GONE
                        errorSnackBar.dismiss()
                    }

                    Resource.Status.OFFLINE_ERROR -> {
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("no_internet_connection_animation.json")
                        binding.animationView.playAnimation()
                        errorSnackBar.setText("No Internet Connection")
                        Handler().postDelayed({ errorSnackBar.show() }, 500)
                    }

                    Resource.Status.EMPTY -> {
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("empty_animation.json")
                        binding.animationView.playAnimation()
                        errorSnackBar.setText("No Orders available")
                        Handler().postDelayed({ errorSnackBar.show() }, 500)
                    }
                }
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.REQUEST_UPDATED_MENU_ITEMS) {
            if (resultCode == 35) {
                val listType = object : TypeToken<List<DataXXXX?>?>() {}.type
                val itemList = Gson().fromJson<List<DataXXXX>>(
                    data?.getStringExtra(AppConstants.INTENT_UPDATED_ITEM),
                    listType
                )
                val category = data?.getStringExtra(AppConstants.INTENT_UPDATED_ITEM_CATEGORY)
                if (itemList.isNotEmpty()) {
                    categoryHashMap[itemList[0].category]?.let {
                        categoryHashMap[itemList[0].category] = ArrayList(itemList)
                        categoryItemList.clear()
                        for (key in categoryHashMap.keys) {
                            categoryHashMap[key]?.let { it1 ->
                                if (it1.size > 0)
                                    categoryItemList.add(CategoryItemListModel(key, it1))
                            }
                        }
                        categoryAdapter.notifyDataSetChanged()
                    }
                } else {
                    categoryItemList.clear()
                    category?.let {
                        categoryHashMap[it] = ArrayList()
                    }
                    for (key in categoryHashMap.keys) {
                        categoryHashMap[key]?.let { it1 ->
                            if (it1.size > 0)
                                categoryItemList.add(CategoryItemListModel(key, it1))
                        }
                    }
                    categoryAdapter.notifyDataSetChanged()
                }
                println("testing: $itemList")
            }
        }
    }
}