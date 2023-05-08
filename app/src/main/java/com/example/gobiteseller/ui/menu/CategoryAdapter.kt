package com.example.gobiteseller.ui.menu

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gobiteseller.R
import com.example.gobiteseller.data.model.CategoryItemListModel
import com.example.gobiteseller.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val categoryList: List<CategoryItemListModel>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding: ItemCategoryBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_category, parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoryList[position], holder.adapterPosition, listener)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class CategoryViewHolder(var binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(categoryItemListModel: CategoryItemListModel, position: Int, listener: OnItemClickListener) {
            binding.textCategoryName.text =categoryItemListModel.category
            binding.textNumberOfItems.text = categoryItemListModel.itemModelList.size.toString()+" item(s)"
            binding.layoutRoot.setOnClickListener{ listener.onItemClick(categoryItemListModel,position) }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(categoryItemListModel: CategoryItemListModel?, position: Int)
    }

}