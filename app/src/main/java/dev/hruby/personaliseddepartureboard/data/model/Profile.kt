package dev.hruby.personaliseddepartureboard.data.model

import java.time.LocalTime

data class Profile(
    val id: String,
    val name: String,
    val stops: List<ProfileStop>,
    val validity: Validity
)

data class ProfileStop(
    val id: String,
    val name: String,
    val platforms: List<String>,
    val lines: List<String>
)

data class ProfileStopDraft(
    val id: String? = null,
    val name: String = "",
    val platforms: List<String> = emptyList(),
    val lines: List<String> = emptyList()
)

data class Validity(
    val from: LocalTime,
    val to: LocalTime
)

data class ProfileDraft(
    val id: String? = null,
    val name: String = "",
    val stops: List<ProfileStop> = listOf(),
    val from: LocalTime? = null,
    val to: LocalTime? = null,
    val stopDraft: ProfileStopDraft? = null
)

fun Profile.toDraft(): ProfileDraft =
    ProfileDraft(
        id = id,
        name = name,
        stops = stops,
        from = validity.from,
        to = validity.to,
        stopDraft = null
    )

fun ProfileStop.toDraft(): ProfileStopDraft =
    ProfileStopDraft(
        id = id,
        name = name,
        platforms = platforms,
        lines = lines
    )

fun ProfileStopDraft.toProfileStop(): ProfileStop {
    require(!id.isNullOrEmpty()) { "Id must not be null or empty" }
    require(name.trim().isNotBlank()) { "Name must not be blank" }
    require(platforms.isNotEmpty()) { "At least one platform must be selected"}
    require(lines.isNotEmpty()) { "At least one line must be selected" }

    return ProfileStop(
        id = id,
        name = name.trim(),
        platforms = platforms,
        lines = lines
    )
}


fun ProfileDraft.toProfile(generateId: () -> String): Profile {
    val finalFrom = requireNotNull(from) { "From time must be selected" }
    val finalTo = requireNotNull(to) { "To time must be selected" }

    require(stops.isNotEmpty()) { "At least one stop must be selected" }
    require(name.isNotBlank()) { "Name must not be blank" }
    require(finalFrom < finalTo) { "From must be before to" }

    return Profile(
        id = id ?: generateId(),
        name = name.trim(),
        stops = stops,
        validity = Validity(
            from = finalFrom,
            to = finalTo
        )
    )
}