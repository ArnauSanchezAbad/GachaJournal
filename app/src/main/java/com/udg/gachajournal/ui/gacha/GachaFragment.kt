package com.udg.gachajournal.ui.gacha

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.udg.gachajournal.data.JournalRepository
import com.udg.gachajournal.data.database.AppDatabase
import com.udg.gachajournal.ui.MainViewModel
import com.udg.gachajournal.ui.MainViewModelFactory

class GachaFragment : Fragment() {

    private val viewModel: GachaViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        GachaViewModelFactory(JournalRepository(database.journalEntryDao(), database.userDao()))
    }

    private val mainViewModel: MainViewModel by activityViewModels {
        val database = AppDatabase.getDatabase(requireContext())
        MainViewModelFactory(JournalRepository(database.journalEntryDao(), database.userDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val appTheme by mainViewModel.appTheme.collectAsState()
                GachaScreen(viewModel = viewModel, theme = appTheme)
            }
        }
    }
}