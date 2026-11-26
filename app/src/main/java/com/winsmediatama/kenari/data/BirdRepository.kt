package com.winsmediatama.kenari.data

import kotlinx.coroutines.flow.Flow

class BirdRepository(private val dao: BirdDao) {
    val birds: Flow<List<BirdEntity>> = dao.observeAll()

    suspend fun save(bird: BirdEntity): Result<Long> {
        val ring = bird.ringId.trim()
        if (ring.isBlank()) return Result.failure(IllegalArgumentException("Nomor ring wajib diisi"))
        if (dao.countRingDuplicate(ring, bird.id) > 0) {
            return Result.failure(IllegalArgumentException("Nomor ring sudah terdaftar"))
        }
        val normalized = bird.copy(ringId = ring)
        return if (bird.id == 0L) Result.success(dao.insert(normalized))
        else {
            dao.update(normalized)
            Result.success(bird.id)
        }
    }

    suspend fun delete(bird: BirdEntity) = dao.delete(bird)
}
