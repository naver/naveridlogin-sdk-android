package com.navercorp.nid.core.data.datastore

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.security.KeyStore
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

internal object NidKeyStoreManager {
    private const val DEFAULT_PROVIDER = "AndroidKeyStore"
    private const val DEFAULT_KEY_SIZE = 256

    fun getOrCreateSecretKey(
        keyAlias: String,
        keyAlgorithm: String,
    ): SecretKey {
        val keyStore = KeyStore.getInstance(DEFAULT_PROVIDER)
        keyStore.load(null)

        return if (keyStore.containsAlias(keyAlias)) {
            keyStore.getKey(keyAlias, null) as SecretKey
        } else {
            createNewKeyStoreKey(keyAlias, keyAlgorithm)
        }
    }

    private fun createNewKeyStoreKey(keyAlias: String, keyAlgorithm: String): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(keyAlgorithm, DEFAULT_PROVIDER)
        // API 23을 기준으로 키 생성 로직 분기
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val keyGenParameterSpec = getKeyGenParameterSpec(keyAlias)
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        } else {
            keyGenerator.init(DEFAULT_KEY_SIZE, SecureRandom())
            keyGenerator.generateKey()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getKeyGenParameterSpec(keyAlias: String): AlgorithmParameterSpec? =
        KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setKeySize(DEFAULT_KEY_SIZE)
            setRandomizedEncryptionRequired(true)
            setUserAuthenticationRequired(false)
        }.build()
}