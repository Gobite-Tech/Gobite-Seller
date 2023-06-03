package com.example.gobiteseller.ui.orderHistory

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gobiteseller.R
import com.example.gobiteseller.data.local.PreferencesHelper
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.OrderX
import com.example.gobiteseller.databinding.ActivityOrderHistoryBinding
import com.example.gobiteseller.ui.orderdetails.OrderDetailActivity
import com.example.gobiteseller.utils.AppConstants
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import org.koin.android.ext.android.inject

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderHistoryBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: OrderHistoryViewModel by inject()
    private lateinit var orderAdapter: OrderHistoryAdapter
    private lateinit var progressDialog: ProgressDialog
    private var orderList: ArrayList<OrderX> = ArrayList()
    private lateinit var errorSnackBar: Snackbar
    var pageCnt = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        initView()
        setListener()
        setObservers()
        getOrders()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_history)
        binding.imageClose.setOnClickListener{
            onBackPressed()
        }
        progressDialog = ProgressDialog(this)
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(com.mikepenz.materialdrawer.R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, com.mikepenz.materialize.R.color.accent))
        val text = "<font color=#000000>Manage and track</font> <font color=#FF4141>orders</font>"
        binding.titleOrders.text = Html.fromHtml(text)
        //binding.layoutSearch.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        setupShopRecyclerView()
    }

    private fun setListener() {
//        binding.editSearch.setOnClickListener{
////            startActivity(Intent(applicationContext,SearchOrderActivity::class.java))
//        }
    }

    private fun setupShopRecyclerView() {
        orderAdapter = OrderHistoryAdapter(orderList, object : OrderHistoryAdapter.OnItemClickListener {
            override fun onItemClick(item: OrderX?, position: Int) {
                val intent = Intent(applicationContext, OrderDetailActivity::class.java)
                intent.putExtra(AppConstants.ORDER_DETAIL, Gson().toJson(item))
                startActivity(intent)
            }

            override fun onPhoneClick(orderItemListModel: OrderX?, position: Int) {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:${orderItemListModel?.customer_mobile}")
                startActivity(intent)
            }
        })
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerShops.layoutManager = layoutManager
        binding.recyclerShops.adapter = AlphaInAnimationAdapter(orderAdapter)

    }


    private fun getOrders() {
        orderList.clear()
        orderAdapter.notifyDataSetChanged()
        viewModel.getOrderByShopId("Bearer "+preferencesHelper.oauthId!!)
    }

    private fun setObservers() {

        viewModel.orderByShopIdResponse.observe(this, Observer {resource->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    orderList.clear()
                    if (resource.data!=null) {
                        resource.data.let { it1 ->
                            //ordersList.addAll(it1)
                            it1.data.orders.forEach {
                                    orderList.add(it)
                            }
                        }
                    }
                    orderAdapter.notifyDataSetChanged()
                    if (orderList.isEmpty()) {
//                        showEmptyStateAnimation()
                    } else {
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.GONE
//                        binding.animationView.cancelAnimation()
                        errorSnackBar.dismiss()
                    }
                }
                Resource.Status.ERROR -> {
                    binding.layoutStates.visibility = View.GONE
//                    binding.animationView.visibility = View.VISIBLE
//                    binding.animationView.loop(true)
//                    binding.animationView.setAnimation("order_failed_animation.json")
//                    binding.animationView.playAnimation()
                    orderList.clear()
                    orderAdapter.notifyDataSetChanged()
                    errorSnackBar.setText("Error: " + resource.message)

                    //Toast.makeText(context,"Something went wrong. Error:\n"+it.message, Toast.LENGTH_LONG).show()

                }

                Resource.Status.LOADING -> {
                    orderList.clear()
                    orderAdapter.notifyDataSetChanged()
//                    if (!binding.swipeRefreshLayout.isRefreshing) {
//                        binding.layoutStates.visibility = View.VISIBLE
//                        binding.animationView.visibility = View.GONE
//                    }
                    errorSnackBar.dismiss()
                }

                Resource.Status.OFFLINE_ERROR -> {
//                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.layoutStates.visibility = View.GONE
                    binding.animationView.visibility = View.VISIBLE
//                    binding.animationView.loop(true)
//                    binding.animationView.setAnimation("no_internet_connection_animation.json")
//                    binding.animationView.playAnimation()
//                    errorSnackBar.setText("No Internet Connection")
                    orderList.clear()
                    orderAdapter.notifyDataSetChanged()
                    Handler().postDelayed({ errorSnackBar.show() }, 500)
                }

                Resource.Status.EMPTY -> {
                    orderList.clear()
                    orderAdapter.notifyDataSetChanged()
//                    showEmptyStateAnimation()
                }

                else -> {}
            }
        })



    }


}