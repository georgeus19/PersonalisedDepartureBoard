package dev.hruby.personaliseddepartureboard.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.hruby.personaliseddepartureboard.R
import dev.hruby.personaliseddepartureboard.data.api.RetrofitInstance
import dev.hruby.personaliseddepartureboard.data.model.DepartureFeature
import dev.hruby.personaliseddepartureboard.data.model.Profile
import dev.hruby.personaliseddepartureboard.data.model.ProfileDraft
import dev.hruby.personaliseddepartureboard.data.model.ProfileStop
import dev.hruby.personaliseddepartureboard.data.model.ProfileStopDraft
import dev.hruby.personaliseddepartureboard.data.model.Stop
import dev.hruby.personaliseddepartureboard.data.model.Validity
import dev.hruby.personaliseddepartureboard.data.model.toDraft
import dev.hruby.personaliseddepartureboard.data.model.toProfile
import dev.hruby.personaliseddepartureboard.data.model.toProfileStop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalTime
import java.util.UUID

data class DepartureBoardState(
    val profiles: List<Profile> = emptyList(),
    val editedProfile: ProfileDraft? = null
)

data class Line(
    val id: String,
    val lastStop: String?
)

data class Platform(
    val code: String,
    val lines: List<Line>
)

class DepartureBoardViewModel(application: Application) : AndroidViewModel(application)  {

    val apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NDg1NCwiaWF0IjoxNzczNjAxNTI3LCJleHAiOjExNzczNjAxNTI3LCJpc3MiOiJnb2xlbWlvIiwianRpIjoiZjUxNTM2NjctMDU1Yy00NWQxLWE1YjctOTc5ZTBhNjg0YTk2In0.S2Csa_AZJe3qUCh_V_5nkZLTSScOM7yJEFMus3MinrY"


    private val _state = MutableStateFlow(DepartureBoardState())
    val state: StateFlow<DepartureBoardState> = _state

    private val _departures = MutableStateFlow<List<DepartureFeature>>(emptyList())
    val departures: StateFlow<List<DepartureFeature>> = _departures

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val stopMap = mutableMapOf<String, List<String>>()

    private val _lines = MutableStateFlow<List<Line>>(emptyList())
    val lines: StateFlow<List<Line>> = _lines

    private val _platforms = MutableStateFlow<List<Platform>>(emptyList())
    val platforms: StateFlow<List<Platform>> = _platforms

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
                            stopMap[name.lowercase()] = (stopMap[name.lowercase()] ?: emptyList()) + id
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to load stops: ${e.message}"
            }
        }
    }

    fun fetchLines(stopName: String) {
        fetchDeparturesByNameCallback(stopName, { departures ->
            // merge departures into lines
            _lines.value = departures
                .map { departure ->
                    Line(
                        id = departure.route.shortName,
                        lastStop = departure.trip.headsign
                    )
                }
                .distinctBy { it.id }
        })
    }

    fun fetchPlatforms(stopName: String) {
        fetchDeparturesByNameCallback(stopName, { departures ->
            // merge departures into lines

            _platforms.value = departures
                .groupBy { it.stop.platformCode ?: "UNK" }
                .map { (code, departuresForPlatform) ->
                    Platform(
                        code = code,
                        lines = departuresForPlatform
                            .map { departure ->
                                Line(
                                    id = departure.route.shortName,
                                    lastStop = departure.trip.headsign
                                )
                            }
                            .distinctBy { it.id }
                    )
                }
                .sortedBy { it.code }
        })
    }

    fun fetchDeparturesByNameCallback(stopName: String, callback: (List<DepartureFeature>) -> Unit) {
        val ids = stopMap[stopName.trim().lowercase()]
        if (ids != null && ids.isNotEmpty()) {
            fetchDepartures(ids, callback)
        } else {
            _error.value = "Stop not found: $stopName"
        }
    }

    fun fetchDeparturesByName(stopName: String) {
        fetchDeparturesByNameCallback (stopName) {
            _departures.value = it
        }
    }

    private fun fetchDepartures(stopIds: List<String>, callback: (List<DepartureFeature>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitInstance.api.getDepartures(
                    accessToken = apiKey,
                    stopIds = stopIds
                )

                callback(response.departures)

            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun startProfileCreate() {
        _state.value = _state.value.copy(
            editedProfile = ProfileDraft(
                stops = emptyList()
            )
        )
    }

    fun startProfileEdit(profileId: String) {
        val profile = _state.value.profiles.find { it.id == profileId } ?: return

        _state.value = _state.value.copy(
            editedProfile = profile.toDraft()
        )
    }

    fun startProfileStopEdit() {
        updateDraft {
            it.copy(stopDraft = ProfileStopDraft(id = UUID.randomUUID().toString()))
        }
    }

    fun createProfileStop() {
        val currentDraft = _state.value.editedProfile ?: return
        val stopDraft = currentDraft.stopDraft ?: return
        
        _state.value = _state.value.copy(
            editedProfile = currentDraft.copy(
                stops = currentDraft.stops + stopDraft.toProfileStop(),
                stopDraft = null
            )
        )
    }

    private fun updateDraft(transform: (ProfileDraft) -> ProfileDraft) {
        val draft = _state.value.editedProfile ?: return
        _state.value = _state.value.copy(
            editedProfile = transform(draft)
        )
    }

    private fun updateStopDraft(transform: (ProfileStopDraft) -> ProfileStopDraft) {
        val draft = _state.value.editedProfile?.stopDraft ?: return
        _state.value = _state.value.copy(
            editedProfile = _state.value.editedProfile?.copy(
                stopDraft = transform(draft)
            )
        )

    }

    fun updateDraftName(name: String) = updateDraft {
        it.copy(name = name)
    }

//    fun updateDraftStop(stop: Stop) = updateDraft {
//        it.copy(stop = stop)
//    }

    fun updateDraftStopName(name: String) = updateStopDraft {
        it.copy(name = name)
    }

    fun updateDraftStopPlatform(platforms: List<String>) = updateStopDraft {
        it.copy(platforms = platforms)
    }

    fun updateDraftStopLine(lines: List<String>) = updateStopDraft {
        it.copy(lines = lines)
    }

    fun updateDraftFrom(from: LocalTime) = updateDraft {
        it.copy(from = from)
    }

    fun updateDraftTo(to: LocalTime) = updateDraft {
        it.copy(to = to)
    }

    fun cancelProfileEdit() {
        _state.value = _state.value.copy(
            editedProfile = null
        )
    }

    fun saveProfile() {
        val draft = _state.value.editedProfile ?: return

        val profile = draft.toProfile(
            generateId = { UUID.randomUUID().toString() }
        )

        val updatedProfiles = if (draft.id == null) {
            _state.value.profiles + profile
        } else {
            _state.value.profiles.map {
                if (it.id == profile.id) profile else it
            }
        }

        _state.value = _state.value.copy(
            profiles = updatedProfiles,
            editedProfile = null
        )
    }

    fun deleteProfile(profileId: String) {
        _state.value = _state.value.copy(
            profiles = _state.value.profiles.filterNot { it.id == profileId }
        )
    }
}
