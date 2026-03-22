package dev.hruby.personaliseddepartureboard.data.model

import com.google.gson.annotations.SerializedName

data class DepartureResponse(
    @SerializedName("stops") val stops: List<Stop>,
    @SerializedName("departures") val departures: List<DepartureFeature>
)

data class Stop(
    @SerializedName("stop_id") val stopId: String,
    @SerializedName("stop_name") val stopName: String,
    @SerializedName("platform_code") val platformCode: String?
)

data class DepartureFeature(
    @SerializedName("route") val route: Route,
    @SerializedName("departure_timestamp") val departureTimestamp: DepartureTimestamp,
    @SerializedName("trip") val trip: Trip,
    @SerializedName("stop") val stop: StopReference,
    @SerializedName("last_stop") val lastStop: LastStop?
)

data class LastStop(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class StopReference(
    @SerializedName("id") val id: String,
    @SerializedName("platform_code") val platformCode: String?
)

data class Route(
    @SerializedName("short_name") val shortName: String,
    @SerializedName("type") val type: Int
)

data class DepartureTimestamp(
    @SerializedName("scheduled") val scheduled: String,
    @SerializedName("predicted") val predicted: String?,
    @SerializedName("minutes") val minutes: String?
)

data class Trip(
    @SerializedName("headsign") val headsign: String,
    @SerializedName("is_wheelchair_accessible") val isWheelchairAccessible: Boolean?,
    @SerializedName("is_air_conditioned") val isAirConditioned: Boolean?
)
