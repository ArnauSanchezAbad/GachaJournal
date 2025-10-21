package com.udg.gachajournal.ui.profile

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udg.gachajournal.databinding.ItemProfileHeaderBinding
import com.udg.gachajournal.databinding.ItemProfileOptionBinding
import com.udg.gachajournal.ui.AppTheme

sealed class ProfileRecyclerViewItem {
    data class Header(val title: String) : ProfileRecyclerViewItem()
    data class Option(val profileOption: ProfileOption) : ProfileRecyclerViewItem()
}

class ProfileAdapter(private val onActionClick: (ProfileOption) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = listOf<ProfileRecyclerViewItem>()
    private var currentTheme: AppTheme? = null

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_OPTION = 1
    }

    fun updateTheme(theme: AppTheme) {
        currentTheme = theme
        notifyDataSetChanged()
    }

    fun submitList(optionsByType: Map<String, List<ProfileOption>>) {
        val newItems = mutableListOf<ProfileRecyclerViewItem>()
        optionsByType.forEach { (type, options) ->
            newItems.add(ProfileRecyclerViewItem.Header(type.replace("_", " ").capitalize()))
            options.forEach { option ->
                newItems.add(ProfileRecyclerViewItem.Option(option))
            }
        }
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ProfileRecyclerViewItem.Header -> VIEW_TYPE_HEADER
            is ProfileRecyclerViewItem.Option -> VIEW_TYPE_OPTION
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(ItemProfileHeaderBinding.inflate(inflater, parent, false))
            VIEW_TYPE_OPTION -> OptionViewHolder(ItemProfileOptionBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ProfileRecyclerViewItem.Header -> (holder as HeaderViewHolder).bind(item, currentTheme)
            is ProfileRecyclerViewItem.Option -> (holder as OptionViewHolder).bind(item, currentTheme)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class HeaderViewHolder(private val binding: ItemProfileHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val defaultColor = binding.textHeader.currentTextColor
        
        fun bind(header: ProfileRecyclerViewItem.Header, theme: AppTheme?) {
            binding.textHeader.text = header.title

            // Apply font color
            theme?.fontColor?.let {
                try {
                    binding.textHeader.setTextColor(Color.parseColor(it))
                } catch (e: Exception) {
                    binding.textHeader.setTextColor(defaultColor)
                }
            } ?: run {
                binding.textHeader.setTextColor(defaultColor)
            }
        }
    }

    inner class OptionViewHolder(private val binding: ItemProfileOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val defaultColor = binding.textOptionName.currentTextColor

        fun bind(optionItem: ProfileRecyclerViewItem.Option, theme: AppTheme?) {
            val option = optionItem.profileOption
            binding.textOptionName.text = option.cosmetic.name

            // Apply border
            theme?.borderColor?.let {
                try {
                    val borderColor = Color.parseColor(it)
                    val borderDrawable = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        setStroke(8, borderColor)
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
                    binding.textOptionName.setTextColor(fontColor)
                } catch (e: Exception) {
                    binding.textOptionName.setTextColor(defaultColor)
                }
            } ?: run {
                binding.textOptionName.setTextColor(defaultColor)
            }

            // Set button state
            binding.buttonAction.isEnabled = option.isOwned
            binding.buttonAction.text = when {
                option.isEquipped -> "Equipped"
                option.isOwned -> "Equip"
                else -> "Locked"
            }

            if (!option.isEquipped && option.isOwned) {
                binding.buttonAction.setOnClickListener { onActionClick(option) }
            } else {
                binding.buttonAction.setOnClickListener(null)
            }
        }
    }
}