package com.udg.gachajournal.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.udg.gachajournal.data.JournalRepository
import com.udg.gachajournal.data.database.AppDatabase
import com.udg.gachajournal.databinding.FragmentProfileBinding
import com.udg.gachajournal.ui.MainViewModel
import com.udg.gachajournal.ui.MainViewModelFactory
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel
    private val mainViewModel: MainViewModel by activityViewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = JournalRepository(database.journalEntryDao(), database.userDao())
        MainViewModelFactory(repository)
    }
    private lateinit var profileAdapter: ProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val repository = JournalRepository(database.journalEntryDao(), database.userDao())
        val factory = ProfileViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        profileAdapter = ProfileAdapter { profileOption ->
            viewModel.equipCosmetic(profileOption.cosmetic)
        }

        binding.recyclerViewProfile.adapter = profileAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.recyclerViewProfile.isVisible = !state.isLoading
                profileAdapter.submitList(state.optionsByType)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.appTheme.collect { theme ->
                profileAdapter.updateTheme(theme)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}