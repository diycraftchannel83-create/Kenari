package com.winsmediatama.kenari.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "birds")
data class BirdEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ringId: String,
    val name: String,
    val sex: String,
    val hatchDate: Long?,
    val breed: String,
    val colorMutation: String,
    val origin: String,
    val fatherId: Long?,
    val motherId: Long?,
    val photoUri: String?,
    val notes: String,
    val createdAt: Long = System.currentTimeMillis()
)
