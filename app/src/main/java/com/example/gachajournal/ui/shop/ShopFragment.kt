package com.example.gachajournal.ui.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.gachajournal.data.JournalRepository
import com.example.gachajournal.data.database.AppDatabase
import com.example.gachajournal.databinding.FragmentShopBinding
import com.example.gachajournal.ui.MainViewModel
import com.example.gachajournal.ui.MainViewModelFactory
import kotlinx.coroutines.launch

class ShopFragment : Fragment() {

    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ShopViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var shopAdapter: ShopAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val repository = JournalRepository(database.journalEntryDao(), database.userDao())
        
        // Init ShopViewModel (Fragment-scoped)
        val factory = ShopViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ShopViewModel::class.java]

        // Init MainViewModel (Activity-scoped)
        val mainFactory = MainViewModelFactory(repository)
        mainViewModel = ViewModelProvider(requireActivity(), mainFactory)[MainViewModel::class.java]

        shopAdapter = ShopAdapter { shopItem ->
            viewModel.purchaseItem(shopItem.cosmetic)
        }

        binding.recyclerViewShop.adapter = shopAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.isVisible = state.isLoading
                binding.recyclerViewShop.isVisible = !state.isLoading
                shopAdapter.submitList(state.items)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.appTheme.collect { theme ->
                shopAdapter.updateTheme(theme)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.purchaseEvents.collect { result ->
                when (result) {
                    PurchaseResult.SUCCESS -> Toast.makeText(requireContext(), "Purchase successful!", Toast.LENGTH_SHORT).show()
                    PurchaseResult.INSUFFICIENT_FUNDS -> Toast.makeText(requireContext(), "Not enough points!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}