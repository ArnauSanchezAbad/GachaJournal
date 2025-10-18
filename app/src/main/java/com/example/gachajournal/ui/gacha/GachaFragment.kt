package com.example.gachajournal.ui.gacha

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.gachajournal.data.JournalRepository
import com.example.gachajournal.data.database.AppDatabase

class GachaFragment : Fragment() {

    private val viewModel: GachaViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        GachaViewModelFactory(JournalRepository(database.journalEntryDao(), database.userDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                GachaScreen(viewModel = viewModel)
            }
        }
    }
}