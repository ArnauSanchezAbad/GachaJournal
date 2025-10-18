package com.example.gachajournal

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gachajournal.data.JournalRepository
import com.example.gachajournal.data.database.AppDatabase
import com.example.gachajournal.databinding.ActivityMainBinding
import com.example.gachajournal.ui.MainViewModel
import com.example.gachajournal.ui.MainViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        val database = AppDatabase.getDatabase(this)
        val repository = JournalRepository(database.journalEntryDao(), database.userDao())
        MainViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        binding.fab.setOnClickListener {
            navController.navigate(R.id.navigation_new_entry)
        }

        lifecycleScope.launch {
            viewModel.appTheme.collect { theme ->
                // Apply background color
                val color = try {
                    theme.backgroundColor?.let { Color.parseColor(it) } ?: Color.TRANSPARENT
                } catch (e: IllegalArgumentException) {
                    Color.TRANSPARENT
                }
                binding.container.setBackgroundColor(color)
            }
        }

        // Show/Hide bottom nav based on destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_diary,
                R.id.navigation_gacha,
                R.id.navigation_shop,
                R.id.navigation_profile -> {
                    binding.bottomAppBar.visibility = View.VISIBLE
                    binding.fab.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomAppBar.visibility = View.GONE
                    binding.fab.visibility = View.GONE
                }
            }
        }
    }
}