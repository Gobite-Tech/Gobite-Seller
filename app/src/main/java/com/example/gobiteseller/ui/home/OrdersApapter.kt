package com.example.gobiteseller.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gobiteseller.R
import com.example.gobiteseller.data.model.OrderByIdModel
import com.example.gobiteseller.data.model.OrderX
import com.example.gobiteseller.databinding.ItemOrderBinding
import com.example.gobiteseller.utils.AppConstants
import java.lang.Exception
import java.text.SimpleDateFormat

class OrdersAdapter(
    private val orderList: List<OrderX>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding: ItemOrderBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_order,
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

    class OrderViewHolder(var binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bind(order: OrderX, position: Int, listener: OnItemClickListener) {
            //Picasso.get().load(menuItem.photoUrl).into(binding.imageShop)
            binding.textOrderId.text = order.id
            binding.textCustomerName.text = order.customer_id.toString()
            try {
                val appDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm aaa")
                val date = order.updated_at
                val dateString = appDateFormat.format(date)
                binding.textOrderTime.text = dateString
            } catch (e: Exception) {
                e.printStackTrace()
            }
            binding.textOrderPrice.text =
                "₹ " + order.price
            var items = ""
            order.items.forEach {
                items += it.quantity.toString() + " X " + it.item_name + "\n"
            }
            binding.textOrderItems.text = items

                binding.textPickUp.text = "PICKUP"




            when (order.order_status) {
                AppConstants.STATUS.CREATED.name -> {
                    binding.textUpdateStatus.text = "ACCEPT"
                }

                AppConstants.STATUS.PLACED.name -> {
                    binding.textUpdateStatus.text = AppConstants.STATUS.BEING_PREPARED.name
                }

                AppConstants.STATUS.BEING_PREPARED.name -> {
                    binding.textCancel.visibility = View.INVISIBLE
                    binding.textCancel.isEnabled = false
                    binding.textUpdateStatus.text = "PREPARED"
                }

                AppConstants.STATUS.PREPARED.name -> {
                    binding.textCancel.visibility = View.INVISIBLE
                    binding.textCancel.isEnabled = false
                    binding.textUpdateStatus.text = "PREPARED"
                }
            }

            binding.layoutRoot.setOnClickListener { listener.onItemClick(order, position) }
            binding.textUpdateStatus.setOnClickListener { listener.onUpdateClick(order, position) }
            binding.textCancel.setOnClickListener { listener.onCancelClick(order, position) }

        }

    }

    interface OnItemClickListener {
        fun onItemClick(orderItemListModel: OrderX?, position: Int)
        fun onUpdateClick(orderItemListModel: OrderX?, position: Int)
        fun onCancelClick(orderItemListModel: OrderX?, position: Int)
    }

    /* * Update order status
     *  -> Checks if the order status change is valid
     *  -> If new state is READY or OUT_FOR_DELIVERY then secret key is generated and updated
     *  -> If new state is COMPLETED or DELIVERED then secret key sent is checked with secret key in database to validate status change
     *  -> If new state is CANCELLED_BY_SELLER or CANCELLED_BY_USER then refund is initiated
     *  -> The new state is updated in the database
     *
     * * Valid state changes
     * PENDING -> FAILURE ,PLACED
     * PLACED  -> CANCELLED_BY_SELLER,CANCELLED_BY_USER , ACCEPTED
     * CANCELLED_BY_SELLER,CANCELLED_BY_USER -> refund table entry must be added
     * ACCEPTED -> READY, OUT_FOR_DELIVERY , CANCELLED_BY_SELLER -> refund table entry must be added
     * READY -> secret key must be updated in table, COMPLETED
     * OUT_FOR_DELIVERY -> secret key must be updated in table, DELIVERED
     * */
}