package com.example.gachajournal.ui.newentry

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.net.Uri
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
import com.example.gachajournal.data.Reward
import com.example.gachajournal.data.RewardGacha
import com.example.gachajournal.data.database.AppDatabase
import com.example.gachajournal.data.database.JournalEntry
import com.example.gachajournal.databinding.FragmentNewEntryBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class NewEntryFragment : Fragment() {

    private var _binding: FragmentNewEntryBinding? = null
    private val binding get() = _binding!!

    private var selectedGame: String? = null
    private var selectedDate: Calendar = Calendar.getInstance()
    private var imagePath: String? = null

    private val viewModel: NewEntryViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        NewEntryViewModelFactory(JournalRepository(database.journalEntryDao(), database.userDao()))
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri ->
            imagePath = saveImageToInternalStorage(uri)
            binding.imagePreview.setImageURI(Uri.fromFile(File(imagePath)))
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().filesDir, "${UUID.randomUUID()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
        updateDateInView(selectedDate)

        viewModel.reward.observe(viewLifecycleOwner) {
            it?.let { showRewardDialog(it) }
        }

        viewModel.user.observe(viewLifecycleOwner) {
            it?.let { user ->
                val remaining4Star = RewardGacha.PITY_4_STAR - user.entriesSinceLast4Star
                val remaining5Star = RewardGacha.PITY_5_STAR - user.entriesSinceLast5Star
                binding.textPity4Star.text = "⭐⭐⭐⭐ (10%) Assegurat en $remaining4Star entrades"
                binding.textPity5Star.text = "⭐⭐⭐⭐⭐ (1%) Assegurat en $remaining5Star entrades"
            }
        }

        return binding.root
    }

    private fun showRewardDialog(reward: Reward) {
        val stars = "⭐".repeat(reward.rarity)
        AlertDialog.Builder(requireContext())
            .setTitle("Recompensa Obtinguda!")
            .setMessage("$stars\n+ ${reward.points} GachaPoints")
            .setPositiveButton("Genial!") { _, _ ->
                viewModel.resetReward()
                findNavController().popBackStack()
            }
            .setCancelable(false)
            .show()
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
            imageUri = imagePath,
            description = description
        )

        viewModel.saveEntry(newEntry)
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