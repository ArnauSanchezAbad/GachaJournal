package com.example.gachajournal.ui.shop.model

data class GachaItem(
    val id: Int,
    val name: String,
    val description: String,
    val isUnlocked: Boolean = false
)