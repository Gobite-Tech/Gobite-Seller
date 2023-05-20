package com.example.gobiteseller.ui.home

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log.e
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gobiteseller.R
import com.example.gobiteseller.data.local.PreferencesHelper
import com.example.gobiteseller.data.local.Resource
import com.example.gobiteseller.data.model.OrderByIdModel
import com.example.gobiteseller.data.model.OrderModelNew
import com.example.gobiteseller.data.model.OrderX
import com.example.gobiteseller.databinding.FragmentNewOrdersBinding
import com.example.gobiteseller.ui.orderdetails.OrderDetailActivity
import com.example.gobiteseller.utils.AppConstants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NewOrdersFragment : Fragment() {

    lateinit var binding: FragmentNewOrdersBinding
    private val viewModel: OrderViewModel by sharedViewModel()
    private val homeViewModel: HomeViewModel by inject()
    private lateinit var progressDialog: ProgressDialog
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var errorSnackBar: Snackbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_orders, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObservers()
    }

    private fun initView() {
        updateUI()
        progressDialog = ProgressDialog(activity)
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(com.mikepenz.materialize.R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        errorSnackBar.setAction("Try Again") {
            e("oauthId", "Bearer ${preferencesHelper.oauthId}")
            viewModel.getOrderByShopId("Bearer "+PreferencesHelper(requireContext()).oauthId!!)
        }
        errorSnackBar.dismiss()
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getOrderByShopId("Bearer "+PreferencesHelper(requireContext()).oauthId!!)
        }
        viewModel.getOrderByShopId("Bearer "+PreferencesHelper(requireContext()).oauthId!!)
    }

    private fun setObservers() {
        viewModel.orderByShopIdResponse.observe(viewLifecycleOwner, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        //progressDialog.dismiss()
                        ordersList.clear()
                        if (resource.data != null){
                            resource.data.let { it1 ->
                                it1.data.orders.forEach {
                                    if (it.order_status == "placed"){
                                        ordersList.add(it)
                                    }
                                }
//                                ordersList.forEach { it.transactionModel.orderModel.orderStatus = it.orderStatusModel.last().orderStatus }
                            }
                            orderAdapter.notifyDataSetChanged()
                        }
                        if(ordersList.isEmpty())
                            showEmptyStateAnimation()
                        else{
                            binding.layoutStates.visibility = View.GONE
                            binding.animationView.visibility = View.GONE
                            binding.animationView.cancelAnimation()
                            errorSnackBar.dismiss()
                        }
                    }

                    Resource.Status.ERROR -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("order_failed_animation.json")
                        binding.animationView.playAnimation()
                        errorSnackBar.setText("Something went wrong")
                        ordersList.clear()
                        orderAdapter.notifyDataSetChanged()
                        Handler().postDelayed({ errorSnackBar.show() }, 500)
                    }

                    Resource.Status.LOADING -> {
                        ordersList.clear()
                        orderAdapter.notifyDataSetChanged()
                        if (!binding.swipeRefreshLayout.isRefreshing) {
                            binding.layoutStates.visibility = View.VISIBLE
                            binding.animationView.visibility = View.GONE
                        }
                        errorSnackBar.dismiss()
                    }

                    Resource.Status.OFFLINE_ERROR -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("no_internet_connection_animation.json")
                        binding.animationView.playAnimation()
                        errorSnackBar.setText("No Internet Connection")
                        ordersList.clear()
                        orderAdapter.notifyDataSetChanged()
                        Handler().postDelayed({ errorSnackBar.show() }, 500)
                    }

                    Resource.Status.EMPTY -> {
                        ordersList.clear()
                        orderAdapter.notifyDataSetChanged()
                        showEmptyStateAnimation()
                    }

                    else -> {}
                }
            }
        })

        homeViewModel.updateOrderResponse.observe(viewLifecycleOwner, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        viewModel.getOrderByShopId("Bearer "+PreferencesHelper(requireContext()).oauthId!!)
                    }

                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                    }

                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Updating orders...")
                        progressDialog.show()
                    }

                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(context, "Offline error", Toast.LENGTH_LONG).show()
                    }

                    Resource.Status.EMPTY -> {
                        progressDialog.dismiss()
                        Toast.makeText(context, "No orders", Toast.LENGTH_LONG).show()
                    }
                }
            }

        })

    }

    private var ordersList: ArrayList<OrderX> = ArrayList()
    private lateinit var orderAdapter: OrdersAdapter
    private fun updateUI() {
        println("Order list size " + ordersList.size)
        orderAdapter = OrdersAdapter(ordersList, object : OrdersAdapter.OnItemClickListener {
            override fun onItemClick(orderItemListModel: OrderX?, position: Int) {
                val intent = Intent(context, OrderDetailActivity::class.java)
                intent.putExtra(AppConstants.ORDER_DETAIL, Gson().toJson(orderItemListModel))
                startActivity(intent)
            }

            override fun onUpdateClick(orderItemListModel: OrderX?, position: Int) {
                val orderModel = OrderModelNew(
                    "being_prepared"
                )
                MaterialAlertDialogBuilder(context!!)
                    .setTitle(getString(R.string.confirm_order_status_update))
                    .setMessage(getString(R.string.accept_order_request))
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        homeViewModel.updateOrder(orderItemListModel?.id!!,orderModel)
                    }
                    .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
                    .show()

            }

            override fun onCancelClick(orderItemListModel: OrderX?, position: Int) {
                val orderModel = OrderModelNew(
                   "cancelled"
                )

                MaterialAlertDialogBuilder(context!!)
                    .setTitle(getString(R.string.confirm_order_status_update))
                    .setMessage(getString(R.string.cancel_order_request))
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        homeViewModel.updateOrder(orderItemListModel?.id!!,orderModel)
                    }
                    .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
                    .show()

            }
        })
        binding.recyclerOrders.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerOrders.adapter = orderAdapter
        orderAdapter.notifyDataSetChanged()
    }


    private fun showEmptyStateAnimation(){
        binding.swipeRefreshLayout.isRefreshing = false
        binding.layoutStates.visibility = View.GONE
//        binding.animationView.visibility = View.VISIBLE
//        binding.animationView.loop(true)
//        binding.animationView.setAnimation("empty_animation.json")
//        binding.animationView.playAnimation()
//        errorSnackBar.setText("No New Orders available")
        Handler().postDelayed({ errorSnackBar.show() }, 500)
    }

    override fun onPause() {
        super.onPause()
        errorSnackBar.dismiss()
    }

}