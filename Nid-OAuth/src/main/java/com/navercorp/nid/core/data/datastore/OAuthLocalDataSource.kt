package com.navercorp.nid.core.data.datastore

/**
 * NidOAuthLocalDataSource 인터페이스
 */
internal interface OAuthLocalDataSource {
    suspend fun save(key: String, value: Int)

    suspend fun save(key: String, value: Long)

    suspend fun save(key: String, value: String?)

    suspend fun save(key: String, value: Boolean)

    suspend fun save(key: String, value: Float)

    suspend fun save(key: String, value: Double)

    fun load(key: String, defaultValue: Int): Int

    fun load(key: String, defaultValue: Long): Long

    fun load(key: String, defaultValue: String?): String?

    fun load(key: String, defaultValue: Boolean): Boolean

    fun load(key: String, defaultValue: Float): Float

    fun load(key: String, defaultValue: Double): Double
}
