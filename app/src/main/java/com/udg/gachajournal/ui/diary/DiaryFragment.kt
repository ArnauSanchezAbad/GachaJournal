package com.udg.gachajournal.ui.diary

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.udg.gachajournal.R
import com.udg.gachajournal.data.JournalRepository
import com.udg.gachajournal.data.database.AppDatabase
import com.udg.gachajournal.data.database.JournalEntry
import com.udg.gachajournal.databinding.FragmentDiaryBinding
import java.util.Calendar

class DiaryFragment : Fragment() {

    private var _binding: FragmentDiaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DiaryViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        DiaryViewModelFactory(JournalRepository(database.journalEntryDao(), database.userDao()))
    }

    private val journalAdapter = JournalEntryAdapter { entry -> 
        showDeleteConfirmationDialog(entry)
    }
    private var allEntries = emptyList<JournalEntry>()

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

        setupCalendar()
        setupFilter()

        viewModel.allEntries.observe(viewLifecycleOwner) {
            allEntries = it
            updateCalendarEvents()
            updateEntriesForSelectedDate(binding.calendarView.selectedDates.firstOrNull())
        }
    }
    
    private fun showDeleteConfirmationDialog(entry: JournalEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle("Esborrar entrada")
            .setMessage("Estàs segur que vols esborrar aquesta entrada?")
            .setPositiveButton("Esborrar") { _, _ ->
                viewModel.delete(entry)
            }
            .setNegativeButton("Cancel·lar", null)
            .show()
    }

    private fun setupFilter() {
        binding.toggleButtonGroupFilter.addOnButtonCheckedListener { _, _, isChecked ->
            if (isChecked) {
                updateCalendarEvents()
                updateEntriesForSelectedDate(binding.calendarView.selectedDates.firstOrNull())
            }
        }
        binding.toggleButtonGroupFilter.check(R.id.button_all)
    }

    private fun setupCalendar() {
        binding.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                updateEntriesForSelectedDate(eventDay.calendar)
            }
        })
    }

    private fun updateCalendarEvents() {
        val datesWithEntries = getFilteredEntries().map {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.date
            EventDay(calendar, R.drawable.ic_dot)
        }
        binding.calendarView.setEvents(datesWithEntries)
    }

    private fun getFilteredEntries(): List<JournalEntry> {
        val selectedGameId = binding.toggleButtonGroupFilter.checkedButtonId
        if (selectedGameId == R.id.button_all || selectedGameId == -1) {
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

    private fun updateEntriesForSelectedDate(selectedCalendar: Calendar?) {
        val calendar = selectedCalendar ?: return

        val entriesForDay = getFilteredEntries().filter {
            val entryCalendar = Calendar.getInstance().apply { timeInMillis = it.date }
            entryCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
            entryCalendar.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
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