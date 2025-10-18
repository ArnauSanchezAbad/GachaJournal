package com.example.gachajournal.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gachajournal.databinding.ItemProfileHeaderBinding
import com.example.gachajournal.databinding.ItemProfileOptionBinding

sealed class ProfileRecyclerViewItem {
    data class Header(val title: String) : ProfileRecyclerViewItem()
    data class Option(val profileOption: ProfileOption) : ProfileRecyclerViewItem()
}

class ProfileAdapter(private val onActionClick: (ProfileOption) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = listOf<ProfileRecyclerViewItem>()

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_OPTION = 1
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
            is ProfileRecyclerViewItem.Header -> (holder as HeaderViewHolder).bind(item)
            is ProfileRecyclerViewItem.Option -> (holder as OptionViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class HeaderViewHolder(private val binding: ItemProfileHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: ProfileRecyclerViewItem.Header) {
            binding.textHeader.text = header.title
        }
    }

    inner class OptionViewHolder(private val binding: ItemProfileOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(optionItem: ProfileRecyclerViewItem.Option) {
            val option = optionItem.profileOption
            binding.textOptionName.text = option.cosmetic.name

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