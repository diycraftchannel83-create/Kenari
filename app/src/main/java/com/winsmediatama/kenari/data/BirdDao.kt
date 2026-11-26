package com.winsmediatama.kenari.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BirdDao {
    @Query("SELECT * FROM birds ORDER BY name COLLATE NOCASE, ringId")
    fun observeAll(): Flow<List<BirdEntity>>

    @Query("SELECT * FROM birds WHERE id = :birdId LIMIT 1")
    suspend fun getById(birdId: Long): BirdEntity?

    @Query("SELECT COUNT(*) FROM birds WHERE ringId = :ringId AND id != :excludeId")
    suspend fun countRingDuplicate(ringId: String, excludeId: Long = 0): Int

    @Insert
    suspend fun insert(bird: BirdEntity): Long

    @Update
    suspend fun update(bird: BirdEntity)

    @Delete
    suspend fun delete(bird: BirdEntity)
}
