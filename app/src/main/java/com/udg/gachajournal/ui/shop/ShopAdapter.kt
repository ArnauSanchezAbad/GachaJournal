package com.udg.gachajournal.ui.shop

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udg.gachajournal.databinding.ItemShopBinding
import com.udg.gachajournal.ui.AppTheme

class ShopAdapter(private val onPurchaseClick: (ShopItem) -> Unit) :
    ListAdapter<ShopItem, ShopAdapter.ShopViewHolder>(ShopItemDiffCallback()) {

    private var currentTheme: AppTheme? = null

    fun updateTheme(theme: AppTheme) {
        currentTheme = theme
        notifyDataSetChanged() // Redraw visible items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopViewHolder {
        val binding = ItemShopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShopViewHolder, position: Int) {
        val shopItem = getItem(position)
        holder.bind(shopItem, currentTheme)
    }

    inner class ShopViewHolder(private val binding: ItemShopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        private val defaultNameColor = binding.textItemName.currentTextColor
        private val defaultPriceColor = binding.textItemPrice.currentTextColor

        fun bind(shopItem: ShopItem, theme: AppTheme?) {
            binding.textItemName.text = shopItem.cosmetic.name
            binding.textItemPrice.text = "${shopItem.cosmetic.price} Coins"

            // Apply border
            theme?.borderColor?.let {
                try {
                    val borderColor = Color.parseColor(it)
                    val borderDrawable = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        setStroke(8, borderColor) // 8px border
                        cornerRadius = 16f
                    }
                    binding.root.background = borderDrawable
                } catch (e: Exception) {
                    binding.root.background = null
                }
            } ?: run {
                binding.root.background = null
            }

            // Apply font color
            theme?.fontColor?.let {
                try {
                    val fontColor = Color.parseColor(it)
                    binding.textItemName.setTextColor(fontColor)
                    binding.textItemPrice.setTextColor(fontColor)
                } catch (e: Exception) {
                    binding.textItemName.setTextColor(defaultNameColor)
                    binding.textItemPrice.setTextColor(defaultPriceColor)
                }
            } ?: run {
                // Explicitly set default color when fontColor is null
                binding.textItemName.setTextColor(defaultNameColor)
                binding.textItemPrice.setTextColor(defaultPriceColor)
            }

            // Set button state
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