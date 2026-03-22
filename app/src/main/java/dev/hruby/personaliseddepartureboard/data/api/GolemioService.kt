package dev.hruby.personaliseddepartureboard.data.api

import dev.hruby.personaliseddepartureboard.data.model.DepartureResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GolemioService {
    @GET("v2/pid/departureboards")
    suspend fun getDepartures(
        @Header("X-Access-Token") accessToken: String,
        @Query("ids") stopId: String,
        @Query("limit") limit: Int = 50,
        @Query("minutesBefore") minutesBefore: Int = 0,
        @Query("minutesAfter") minutesAfter: Int = 120,
        @Query("mode") mode: String = "departures",
        @Query("order") order: String = "real"
    ): DepartureResponse
}
