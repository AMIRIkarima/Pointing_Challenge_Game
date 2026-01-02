package com.achraf.jeudepoints.data.remote

import com.achraf.jeudepoints.data.model.GameSession
import com.achraf.jeudepoints.data.model.LeaderboardEntry
import com.achraf.jeudepoints.data.model.Player
import com.achraf.jeudepoints.data.model.PlayerLevel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ========== Player Management ==========

    @GET("players")
    suspend fun getPlayers(): Response<List<Player>>

    @POST("players")
    suspend fun createPlayer(@Body request: Map<String, String>): Response<Player>

    @GET("players/{id}")
    suspend fun getPlayer(
        @Path("id") playerId: Long
    ): Response<Player>

    @PUT("players/{id}")
    suspend fun updatePlayer(
        @Path("id") playerId: Long,
        @Body player: Map<String, Any?>
    ): Response<Player>

    @GET("players/{id}/level")
    suspend fun getPlayerLevel(
        @Path("id") playerId: Long
    ): Response<PlayerLevel>

    // ========== Game Sessions ==========

    @POST("games/start")
    suspend fun startGame(
        @Query("playerId") playerId: Long,
        @Query("difficulty") difficulty: String
    ): Response<GameSession>

    @GET("games/active/{playerId}")
    suspend fun getActiveGame(
        @Path("playerId") playerId: Long
    ): Response<GameSession>

    @POST("games/{gameId}/direct-finish")
    suspend fun finishGame(
        @Path("gameId") gameId: Long,
        @Body payload: Map<String, Double>
    ): Response<GameSession>

    // ========== Analytics ==========

    @GET("api/analytics/fitts-data/{playerId}")
    suspend fun getFittsData(
        @Path("playerId") playerId: Long
    ): Response<List<Map<String, Double>>>

    @GET("api/analytics/constants/{playerId}")
    suspend fun getFittsConstants(
        @Path("playerId") playerId: Long
    ): Response<Map<String, Double>>
}
