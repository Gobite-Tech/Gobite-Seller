package com.example.gobiteseller.ui.orderHistory

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gobiteseller.R
import com.example.gobiteseller.data.model.OrderX
import com.example.gobiteseller.databinding.ItemPastOrderBinding
import java.lang.Exception
import java.text.SimpleDateFormat

class OrderHistoryAdapter(private val orderList: List<OrderX>, private val listener: OnItemClickListener) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {
        val binding: ItemPastOrderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_past_order,
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orderList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class OrderViewHolder(var binding: ItemPastOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(order: OrderX, position: Int, listener: OnItemClickListener) {
            binding.textCustomerName.text = order.customer_id.toString()
            try {
                val appDateFormat = SimpleDateFormat("yyyy MMMM dd, hh:mm:ss")
                val dateString = appDateFormat.format(order.created_at)
                binding.textOrderTime.text = dateString
            } catch (e: Exception) {
                e.printStackTrace()
            }
            binding.textOrderId.text = order.id
            binding.textOrderPrice.text = "₹ " + order.price?.toInt().toString()
            var items = ""
            order.items.forEach {
                items += it.quantity.toString() + " X " + it.item_name + "\n"
            }
            binding.textOrderItems.text = items
//            binding.textViewMore.visibility = if (order.orderItemsList.size > 2)  View.VISIBLE else View.GONE
            binding.textOrderStatus.text = order.order_status
            binding.layoutRoot.setOnClickListener { listener.onItemClick(order, position) }
//            binding.buttonViewOrder.setOnClickListener{ listener.onItemClick(order, position) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: OrderX?, position: Int)
    }
}