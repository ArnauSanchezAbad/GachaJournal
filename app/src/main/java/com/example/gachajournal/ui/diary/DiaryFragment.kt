package com.example.gachajournal.ui.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gachajournal.R
import com.example.gachajournal.data.JournalRepository
import com.example.gachajournal.data.database.AppDatabase
import com.example.gachajournal.data.database.JournalEntry
import com.example.gachajournal.databinding.FragmentDiaryBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.ZoneId
import java.util.Date

class DiaryFragment : Fragment() {

    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiaryViewModel by viewModels {
        DiaryViewModelFactory(JournalRepository(AppDatabase.getDatabase(requireContext()).journalEntryDao()))
    }

    private val journalAdapter = JournalEntryAdapter()
    private var allEntries = emptyList<JournalEntry>()
    private var selectedDate: Date = Date() // Today by default

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewEntries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = journalAdapter
        }

        binding.buttonOpenCalendar.setOnClickListener { showCalendar() }

        setupFilter()

        viewModel.allEntries.observe(viewLifecycleOwner) {
            allEntries = it
            updateEntriesForSelectedDate()
        }

        updateEntriesForSelectedDate() // Initial load for today
    }

    private fun setupFilter() {
        binding.toggleButtonGroupFilter.addOnButtonCheckedListener { _, _, isChecked ->
            if (isChecked) {
                updateEntriesForSelectedDate()
            }
        }
        binding.toggleButtonGroupFilter.check(R.id.button_all)
    }

    private fun showCalendar() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecciona una data")
            .setSelection(selectedDate.time)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDate = Date(selection)
            updateEntriesForSelectedDate()
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun getFilteredEntries(): List<JournalEntry> {
        val selectedGameId = binding.toggleButtonGroupFilter.checkedButtonId
        if (selectedGameId == R.id.button_all) {
            return allEntries
        }
        val selectedGame = when (selectedGameId) {
            R.id.button_genshin_filter -> "Genshin"
            R.id.button_star_rail_filter -> "Star Rail"
            R.id.button_zzz_filter -> "ZZZ"
            else -> null
        }
        return allEntries.filter { it.game == selectedGame }
    }

    private fun updateEntriesForSelectedDate() {
        val entriesForDay = getFilteredEntries().filter {
            val entryDate = Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
            val selectedLocalDate = Instant.ofEpochMilli(selectedDate.time).atZone(ZoneId.systemDefault()).toLocalDate()
            entryDate == selectedLocalDate
        }

        if (entriesForDay.isEmpty()) {
            binding.recyclerViewEntries.visibility = View.GONE
            binding.textNoEntries.visibility = View.VISIBLE
        } else {
            binding.recyclerViewEntries.visibility = View.VISIBLE
            binding.textNoEntries.visibility = View.GONE
            journalAdapter.submitList(entriesForDay)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}