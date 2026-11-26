package com.winsmediatama.kenari.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

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

    suspend fun seedDemoIfEmpty() {
        if (dao.observeAll().firstOrNull()?.isNotEmpty() == true) return
        val now = System.currentTimeMillis()
        val fatherId = dao.insert(BirdEntity(ringId = "KTS-001", name = "Golden Voice", sex = "Jantan", hatchDate = now - 720L*86400000, breed = "Yorkshire / lokal", colorMutation = "Yellow Intensive", origin = "Demo • Kenari Tasikmalaya", fatherId = null, motherId = null, photoUri = null, notes = "Data contoh jantan indukan. Silakan edit atau hapus.", createdAt = now))
        val motherId = dao.insert(BirdEntity(ringId = "KTS-002", name = "Luna Yellow", sex = "Betina", hatchDate = now - 680L*86400000, breed = "Yorkshire / lokal", colorMutation = "Yellow Mosaic", origin = "Demo • Kenari Tasikmalaya", fatherId = null, motherId = null, photoUri = null, notes = "Data contoh betina indukan. Silakan edit atau hapus.", createdAt = now))
        dao.insert(BirdEntity(ringId = "KTS-003", name = "Sunny Junior", sex = "Belum diketahui", hatchDate = now - 95L*86400000, breed = "Yorkshire / lokal", colorMutation = "Yellow", origin = "Anakan demo", fatherId = fatherId, motherId = motherId, photoUri = null, notes = "Anakan contoh untuk demonstrasi geneologi. Edit data, jenis kelamin, dan foto.", createdAt = now))
    }

    suspend fun delete(bird: BirdEntity) = dao.delete(bird)
}
