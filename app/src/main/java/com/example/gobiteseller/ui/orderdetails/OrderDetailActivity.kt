package com.example.gobiteseller.ui.orderdetails

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.e
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gobiteseller.R
import com.example.gobiteseller.data.model.ItemXX
import com.example.gobiteseller.data.model.OrderX
import com.example.gobiteseller.databinding.ActivityOrderDetailBinding
import com.example.gobiteseller.utils.AppConstants
import com.google.gson.Gson

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var order: OrderX
    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var orderAdapter: OrderItemAdapter

    private var orderList: ArrayList<ItemXX> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        getArgs()

    }

    private fun getArgs(){
        if (intent.hasExtra(AppConstants.ORDER_DETAIL)) {
            order = Gson().fromJson(
                intent.getStringExtra(AppConstants.ORDER_DETAIL),
                OrderX::class.java
            )
            initView()

            e("OrderDetail",order.toString())
        }
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)

        binding.imageClose.setOnClickListener {
            onBackPressed()
        }
        binding.textUserName.text=order.customer_name
        binding.textLastUpdateTime.text=order.order_placed_time
        binding.textOrderId.text=order.id
        binding.textOrderStatus.text=order.order_status

        binding.textItemTotalPrice.text = "₹" + order.price?.toInt().toString()

        binding.imageCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${order.customer_mobile}")
            startActivity(intent)
        }

        setupShopRecyclerView()

    }

    private fun setupShopRecyclerView() {
        orderList.addAll(order.items)
        orderAdapter = OrderItemAdapter(
            orderList,
            object : OrderItemAdapter.OnItemClickListener {
                override fun onItemClick(item: ItemXX?, position: Int) {
                }
            })
        binding.recyclerOrderItems.layoutManager =
            LinearLayoutManager(this@OrderDetailActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerOrderItems.adapter = orderAdapter
    }
}