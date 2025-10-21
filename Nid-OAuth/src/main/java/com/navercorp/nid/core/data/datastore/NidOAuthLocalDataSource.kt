package com.navercorp.nid.core.data.datastore

import android.content.Context
import android.os.Build
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.core.log.NidLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import java.nio.ByteBuffer
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlin.Int.Companion.SIZE_BYTES

object NidOAuthLocalDataSource {
    private const val TAG = "NidDataStore"

    private const val DATA_STORE_NAME = "NidDataStore"
    private const val KEY_ALIAS = "DataStoreSecretKey"
    private const val IV_CIPHER_SEPARATOR = "]"
    private const val GCM_TAG_LENGTH = 128
    private const val CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"
    private const val AES_ALGORITHM = "AES"

    private val keyAlgorithm =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) KeyProperties.KEY_ALGORITHM_AES else AES_ALGORITHM

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATA_STORE_NAME)
    private val nidDataStore: DataStore<Preferences> by lazy {
        NidOAuth.getApplicationContext().dataStore
    }

    private val secretKey: SecretKey by lazy {
        NidKeyStoreManager.getOrCreateSecretKey(
            keyAlias = KEY_ALIAS,
            keyAlgorithm = keyAlgorithm
        )
    }

    /**
     * dataStore preferences StateFlow
     */

    private var scope: CoroutineScope? = null
    private var preferences: StateFlow<Preferences>? = null

    // preferences stateFlow sharing 시작
    fun startSharing() {
        // scope가 cancel되지 않은 상태라면 재생성하지 않음
        if (scope?.isActive == true) return
        // collect하기 위한 scope 재생성
        val newScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope = newScope
        preferences = createPreferencesStateFlow(newScope)
    }

    // preferences stateFlow sharing 중지
    fun stopSharing() {
        scope?.cancel()
    }

    // 새로운 scope, sateFlow 생성
    private fun createPreferencesStateFlow(scope: CoroutineScope): StateFlow<Preferences> =
        nidDataStore.data
            .catch {
                NidLog.e(TAG, "nidDataStore.data error")
                emit(emptyPreferences())
            }.stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = emptyPreferences()
            )

    /**
     * type prefixes for type-safe keys
     */
    private const val INT_PREFIX = "int_"
    private const val LONG_PREFIX = "long_"
    private const val BOOLEAN_PREFIX = "bool_"
    private const val STRING_PREFIX = "str_"
    private const val FLOAT_PREFIX = "float_"
    private const val DOUBLE_PREFIX = "double_"

    /**
     * generate type-safe keys
     */
    private fun getIntKey(key: String) = "${INT_PREFIX}$key"
    private fun getLongKey(key: String) = "${LONG_PREFIX}$key"
    private fun getBooleanKey(key: String) = "${BOOLEAN_PREFIX}$key"
    private fun getStringKey(key: String) = "${STRING_PREFIX}$key"
    private fun getFloatKey(key: String) = "${FLOAT_PREFIX}$key"
    private fun getDoubleKey(key: String) = "${DOUBLE_PREFIX}$key"

    /**
     * conversion to byteArray
     */
    private fun Int.toByteArray(): ByteArray =
        ByteBuffer.allocate(SIZE_BYTES).apply {
            putInt(this@toByteArray)
        }.array()

    private fun Long.toByteArray(): ByteArray =
        ByteBuffer.allocate(Long.SIZE_BYTES).apply {
            putLong(this@toByteArray)
        }.array()

    private fun Boolean.toByteArray(): ByteArray =
        byteArrayOf(if (this) 1 else 0)

    private fun String.toByteArray(): ByteArray =
        this.toByteArray(Charsets.UTF_8)

    private fun Float.toByteArray(): ByteArray =
        ByteBuffer.allocate(Float.SIZE_BYTES).apply {
            putFloat(this@toByteArray)
        }.array()

    private fun Double.toByteArray(): ByteArray =
        ByteBuffer.allocate(Double.SIZE_BYTES).apply {
            putDouble(this@toByteArray)
        }.array()

    /**
     * save data
     */
    suspend fun save(key: String, value: Int) {
        val typeKey = getIntKey(key)
        val byteArray = value.toByteArray()
        saveData(typeKey, byteArray)
    }

    suspend fun save(key: String, value: Long) {
        val typeKey = getLongKey(key)
        val byteArray = value.toByteArray()
        saveData(typeKey, byteArray)
    }

    suspend fun save(key: String, value: String?) {
        val typeKey = getStringKey(key)
        val byteArray = value?.toByteArray() ?: return
        saveData(typeKey, byteArray)
    }

    suspend fun save(key: String, value: Boolean) {
        val typeKey = getBooleanKey(key)
        val byteArray = value.toByteArray()
        saveData(typeKey, byteArray)
    }

    suspend fun save(key: String, value: Float) {
        val typeKey = getFloatKey(key)
        val byteArray = value.toByteArray()
        saveData(typeKey, byteArray)
    }

    suspend fun save(key: String, value: Double) {
        val typeKey = getDoubleKey(key)
        val byteArray = value.toByteArray()
        saveData(typeKey, byteArray)
    }

    suspend fun saveData(key: String, byteArray: ByteArray) {
        val preferencesKey = stringPreferencesKey(key)
        val encryptedValue = encrypt(byteArray) ?: return
        nidDataStore.edit { preferences ->
            preferences[preferencesKey] = encryptedValue
        }
    }

    /**
     * load data
     */
    fun load(key: String, defaultValue: Int): Int {
        val typeKey = getIntKey(key)
        return loadData(typeKey)?.let {
            ByteBuffer.wrap(it).int
        } ?: defaultValue
    }

    fun load(key: String, defaultValue: Long): Long {
        val typeKey = getLongKey(key)
        return loadData(typeKey)?.let {
            ByteBuffer.wrap(it).long
        } ?: defaultValue
    }

    fun load(key: String, defaultValue: String?): String? {
        val typeKey = getStringKey(key)
        return loadData(typeKey)?.let {
            String(it, Charsets.UTF_8)
        } ?: defaultValue
    }

    fun load(key: String, defaultValue: Boolean): Boolean {
        val typeKey = getBooleanKey(key)
        return loadData(typeKey)?.let {
            it[0] == 1.toByte()
        } ?: defaultValue
    }

    fun load(key: String, defaultValue: Float): Float {
        val typeKey = getFloatKey(key)
        return loadData(typeKey)?.let {
            ByteBuffer.wrap(it).float
        } ?: defaultValue
    }

    fun load(key: String, defaultValue: Double): Double {
        val typeKey = getDoubleKey(key)
        return loadData(typeKey)?.let {
            ByteBuffer.wrap(it).double
        } ?: defaultValue
    }

    fun loadData(key: String): ByteArray? {
        val preferencesKey = stringPreferencesKey(key)
        val encryptedValue = preferences?.value[preferencesKey] ?: return null

        return decrypt(encryptedValue)
    }

    /**
     * encryption / decryption methods
     */
    private fun encrypt(plainText: ByteArray): String? =
        runCatching {
            val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val iv = cipher.iv
            val cipherText = cipher.doFinal(plainText)

            val encodedIv = Base64.encodeToString(iv, Base64.NO_WRAP)
            val encodedCipherText = Base64.encodeToString(cipherText, Base64.NO_WRAP)

            "$encodedIv$IV_CIPHER_SEPARATOR$encodedCipherText"
        }.onFailure { exception ->
            NidLog.e("NidDataStore", "Encryption failed: ${exception.message}")
        }.getOrNull()

    private fun decrypt(encryptedData: String): ByteArray? =
        runCatching {
            val parts = encryptedData.split(IV_CIPHER_SEPARATOR)
            require(parts.size == 2) { "Invalid encrypted data format" }

            val decodedIv = Base64.decode(parts[0], Base64.NO_WRAP)
            val decodedCipherText = Base64.decode(parts[1], Base64.NO_WRAP)

            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, decodedIv)
            val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

            cipher.doFinal(decodedCipherText)
        }.onFailure { exception ->
            NidLog.e("NidDataStore", "Decryption failed: ${exception.message}")
        }.getOrNull()
}