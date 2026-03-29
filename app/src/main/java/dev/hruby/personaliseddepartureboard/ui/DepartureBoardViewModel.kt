package dev.hruby.personaliseddepartureboard.ui

import androidx.lifecycle.ViewModel
import dev.hruby.personaliseddepartureboard.data.model.Profile
import dev.hruby.personaliseddepartureboard.data.model.ProfileDraft
import dev.hruby.personaliseddepartureboard.data.model.ProfileStop
import dev.hruby.personaliseddepartureboard.data.model.ProfileStopDraft
import dev.hruby.personaliseddepartureboard.data.model.Stop
import dev.hruby.personaliseddepartureboard.data.model.Validity
import dev.hruby.personaliseddepartureboard.data.model.toDraft
import dev.hruby.personaliseddepartureboard.data.model.toProfile
import dev.hruby.personaliseddepartureboard.data.model.toProfileStop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalTime
import java.util.UUID

data class DepartureBoardState(
    val profiles: List<Profile> = emptyList(),
    val editedProfile: ProfileDraft? = null
)

class DepartureBoardViewModel : ViewModel() {

    private val _state = MutableStateFlow(DepartureBoardState())
    val state: StateFlow<DepartureBoardState> = _state

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
