package com.example.gachajournal.ui.newentry

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.gachajournal.databinding.FragmentNewEntryBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NewEntryFragment : Fragment() {

    private var _binding: FragmentNewEntryBinding? = null
    private val binding get() = _binding!!

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            binding.imagePreview.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewEntryBinding.inflate(inflater, container, false)

        binding.inputDate.setOnClickListener {
            showDatePicker()
        }

        binding.imagePreview.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        return binding.root
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView(calendar)
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