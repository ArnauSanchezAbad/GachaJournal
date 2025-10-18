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
import java.io.File

class JournalEntryAdapter(private val onDeleteClicked: (JournalEntry) -> Unit) : ListAdapter<JournalEntry, JournalEntryAdapter.JournalEntryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalEntryViewHolder {
        val binding = EntryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JournalEntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JournalEntryViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener { 
            // TODO: Add click listener to view full entry
        }
        holder.bind(current, onDeleteClicked)
    }

    class JournalEntryViewHolder(private val binding: EntryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: JournalEntry, onDeleteClicked: (JournalEntry) -> Unit) {
            binding.entryGameTitle.text = entry.game
            binding.entryDescription.text = entry.description
            
            entry.imageUri?.let {
                binding.entryImage.load(File(it)) {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_background)
                }
            } ?: run {
                binding.entryImage.setImageResource(R.drawable.ic_launcher_background)
            }

            binding.buttonDelete.setOnClickListener { onDeleteClicked(entry) }
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