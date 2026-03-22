package dev.hruby.personaliseddepartureboard.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.hruby.personaliseddepartureboard.R
import dev.hruby.personaliseddepartureboard.data.api.RetrofitInstance
import dev.hruby.personaliseddepartureboard.data.model.DepartureFeature
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class DepartureViewModel(application: Application) : AndroidViewModel(application) {
    private val _departures = MutableStateFlow<List<DepartureFeature>>(emptyList())
    val departures: StateFlow<List<DepartureFeature>> = _departures

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val stopMap = mutableMapOf<String, String>()

    init {
        loadStops()
    }

    private fun loadStops() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = getApplication<Application>().resources.openRawResource(R.raw.stops)
                val reader = BufferedReader(InputStreamReader(inputStream))
                reader.readLine() // Skip header
                reader.forEachLine { line ->
                    val parts = line.split(",")
                    if (parts.size >= 2) {
                        val id = parts[0].trim()
                        val name = parts[1].trim().removeSurrounding("\"")
                        // Store the first ID we find for a name, or handle duplicates as needed
                        if (!stopMap.containsKey(name.lowercase())) {
                            stopMap[name.lowercase()] = id
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to load stops: ${e.message}"
            }
        }
    }

    fun fetchDeparturesByName(stopName: String, apiKey: String) {
        val id = stopMap[stopName.trim().lowercase()]
        if (id != null) {
            fetchDepartures(id, apiKey)
        } else {
            _error.value = "Stop not found: $stopName"
        }
    }

    private fun fetchDepartures(stopId: String, apiKey: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitInstance.api.getDepartures(
                    accessToken = apiKey,
                    stopId = stopId
                )
                _departures.value = response.departures
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
