package com.navercorp.nid.preference

import android.content.Context
import androidx.security.crypto.MasterKey
import java.security.KeyStore

interface EncryptedSharedPreferenceWorkaround {
    fun apply(
        context: Context,
        fileName: String,
        throwable: Throwable
    ): Boolean
}

class IncompatibleSharedPreferencesWorkaround: EncryptedSharedPreferenceWorkaround {
    private val statusMap = mutableMapOf<String, Boolean>()

    override fun apply(context: Context, fileName: String, throwable: Throwable): Boolean {
        if (statusMap[fileName] == true) {
            return false
        }
        if (isIncompatibleError(throwable).not()) {
            return false
        }
        statusMap[fileName] = true
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().clear().apply()
        return true
    }

    private fun isIncompatibleError(throwable: Throwable): Boolean {
        val message = throwable.toString()
        return message.contains("InvalidProtocolBufferException")
                && message.contains("Protocol message contained an invalid tag (zero)")
    }
}

class KeyStoreSharedPreferencesWorkaround: EncryptedSharedPreferenceWorkaround {
    private val statusMap = mutableMapOf<String, Boolean>()

    override fun apply(context: Context, fileName: String, throwable: Throwable): Boolean {
        if (statusMap[fileName] == true) {
            return false
        }
        if (isKeyStoreError(throwable).not()) {
            return false
        }
        statusMap[fileName] = true

        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        keyStore.deleteEntry(MasterKey.DEFAULT_MASTER_KEY_ALIAS)

        context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().clear().apply()
        return true
    }

    private fun isKeyStoreError(throwable: Throwable): Boolean {
        val message = throwable.toString()
        return message.contains("KeyStoreException")
    }
}