package com.example.gachajournal.ui.diary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.gachajournal.R
import com.example.gachajournal.data.database.JournalEntry
import com.example.gachajournal.databinding.EntryListItemBinding

class JournalEntryAdapter : ListAdapter<JournalEntry, JournalEntryAdapter.JournalEntryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalEntryViewHolder {
        val binding = EntryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JournalEntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JournalEntryViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class JournalEntryViewHolder(private val binding: EntryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: JournalEntry) {
            binding.entryGameTitle.text = entry.game
            binding.entryDescription.text = entry.description
            binding.entryImage.load(entry.imageUri) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background) // Provisional placeholder
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<JournalEntry>() {
            override fun areItemsTheSame(oldItem: JournalEntry, newItem: JournalEntry):
                Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: JournalEntry, newItem: JournalEntry):
                Boolean = oldItem == newItem
        }
    }
}