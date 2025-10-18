package com.example.gachajournal.ui.shop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gachajournal.databinding.ItemShopBinding

class ShopAdapter(private val onPurchaseClick: (ShopItem) -> Unit) :
    ListAdapter<ShopItem, ShopAdapter.ShopViewHolder>(ShopItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val binding = ItemShopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        val shopItem = getItem(position)
        holder.bind(shopItem)
    }

    inner class ShopViewHolder(private val binding: ItemShopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(shopItem: ShopItem) {
            binding.textItemName.text = shopItem.cosmetic.name
            binding.textItemPrice.text = "${shopItem.cosmetic.price} Coins"

            if (shopItem.isOwned) {
                binding.buttonPurchase.visibility = View.GONE
                binding.imageCheck.visibility = View.VISIBLE
            } else {
                binding.buttonPurchase.visibility = View.VISIBLE
                binding.imageCheck.visibility = View.GONE
                binding.buttonPurchase.setOnClickListener { onPurchaseClick(shopItem) }
            }
        }
    }
}

class ShopItemDiffCallback : DiffUtil.ItemCallback<ShopItem>() {
    override fun areItemsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
        return oldItem.cosmetic.id == newItem.cosmetic.id
    }

    override fun areContentsTheSame(oldItem: ShopItem, newItem: ShopItem): Boolean {
        return oldItem == newItem
    }
}