package com.example.eco_setu

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {
    data class HomeUiState(
        val earnedAmount: String = "₹0.00",
        val treesSaved: String = "~0",
        val wasteCollected: String = "~0 Kgs",
        val co2Averted: String = "~0 Kgs"
    )

    // Backing state flow (mutable inside ViewModel only)
    private val _uiState = MutableStateFlow(HomeUiState())
    // Publicly exposed read‑only StateFlow
    val uiState: StateFlow<HomeUiState> = _uiState

    /**
     * Simulate handling a newly scanned QR code.
     * For MVP we:
     *  - Add 5 kg to wasteCollected
     *  - Add ₹10.00 to earnedAmount
     */
    fun onQrCodeScanned() {
        _uiState.update { current ->
            val earnedNumeric = current.earnedAmount.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
            val wasteNumeric = current.wasteCollected.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0

            val newEarned = earnedNumeric + 10.0
            val newWaste = wasteNumeric + 5.0

            current.copy(
                earnedAmount = "₹" + String.format("%.2f", newEarned),
                wasteCollected = buildString {
                    append("~")
                    // Show integer if whole number, else one decimal place
                    if (newWaste % 1.0 == 0.0) append(newWaste.toInt()) else append(String.format("%.1f", newWaste))
                    append(" Kgs")
                }
            )
        }
    }
}
