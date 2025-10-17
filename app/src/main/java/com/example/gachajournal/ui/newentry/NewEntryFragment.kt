package com.example.gachajournal.ui.newentry

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.gachajournal.R
import com.example.gachajournal.data.JournalRepository
import com.example.gachajournal.data.database.AppDatabase
import com.example.gachajournal.data.database.JournalEntry
import com.example.gachajournal.databinding.FragmentNewEntryBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NewEntryFragment : Fragment() {

    private var _binding: FragmentNewEntryBinding? = null
    private val binding get() = _binding!!

    private var selectedGame: String? = null
    private var selectedDate: Calendar = Calendar.getInstance()
    private var imageUri: String? = null

    private val viewModel: NewEntryViewModel by viewModels {
        NewEntryViewModelFactory(JournalRepository(AppDatabase.getDatabase(requireContext()).journalEntryDao()))
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            binding.imagePreview.setImageURI(it)
            imageUri = it.toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewEntryBinding.inflate(inflater, container, false)

        setupClickListeners()
        setupGameSelection()
        updateDateInView(selectedDate) // Show current date initially

        return binding.root
    }

    private fun setupClickListeners() {
        binding.inputDate.setOnClickListener { showDatePicker() }
        binding.imagePreview.setOnClickListener { galleryLauncher.launch("image/*") }
        binding.buttonBack.setOnClickListener { findNavController().popBackStack() }
        binding.buttonSave.setOnClickListener { saveEntry() }
    }

    private fun saveEntry() {
        val description = binding.inputFeelings.text.toString()

        if (selectedGame == null) {
            Toast.makeText(requireContext(), "Si us plau, selecciona un joc", Toast.LENGTH_SHORT).show()
            return
        }

        if (description.isBlank()) {
            Toast.makeText(requireContext(), "Si us plau, escriu una descripció", Toast.LENGTH_SHORT).show()
            return
        }

        val newEntry = JournalEntry(
            date = selectedDate.timeInMillis,
            game = selectedGame!!,
            imageUri = imageUri,
            description = description
        )

        viewModel.insert(newEntry)
        Toast.makeText(requireContext(), "Entrada desada!", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun setupGameSelection() {
        binding.toggleButtonGroupGame.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedGame = when (checkedId) {
                    R.id.button_genshin -> "Genshin"
                    R.id.button_star_rail -> "Star Rail"
                    R.id.button_zzz -> "ZZZ"
                    else -> null
                }
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, month)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView(selectedDate)
        }

        DatePickerDialog(
            requireContext(),
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateInView(calendar: Calendar) {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.inputDate.setText(sdf.format(calendar.time))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}