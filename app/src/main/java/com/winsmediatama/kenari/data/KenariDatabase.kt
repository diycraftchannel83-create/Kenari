package com.winsmediatama.kenari.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BirdEntity::class], version = 1, exportSchema = false)
abstract class KenariDatabase : RoomDatabase() {
    abstract fun birdDao(): BirdDao
}
