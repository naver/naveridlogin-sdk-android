package com.navercorp.nid.oauth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

private const val TAG = "EncryptedPreferences"

private const val OAUTH_LOGIN_PREF_NAME_PER_APP  = "NaverOAuthLoginPreferenceData";

object EncryptedPreferences {

    private var context: Context? = null

    private val encryptedPreferences: SharedPreferences by lazy {
        init()
    }

    private fun init(): SharedPreferences = EncryptedSharedPreferences
        .create(
            this.context!!,
            OAUTH_LOGIN_PREF_NAME_PER_APP,
            MasterKey.Builder(this.context!!)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .setUserAuthenticationRequired(false)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    @JvmStatic
    fun setContext(context: Context?) {
        this.context = context
    }

    /**
     * Int
     */
    @Synchronized
    fun set(key: String, value: Int) {
        encryptedPreferences.edit {
            putInt(key, value)
        }
    }

    @Synchronized
    fun get(key: String, defaultValue: Int = 0): Int = encryptedPreferences.getInt(key, defaultValue)

    /**
     * Long
     */
    @Synchronized
    fun set(key: String, value: Long) {
        encryptedPreferences.edit {
            putLong(key, value)
        }
    }

    @Synchronized
    fun get(key: String, defaultValue: Long = 0L): Long = encryptedPreferences.getLong(key, defaultValue)

    /**
     * String
     */
    @Synchronized
    fun set(key: String, value: String?) {
        encryptedPreferences.edit {
            putString(key, value)
        }
    }

    @Synchronized
    fun get(key: String, defaultValue: String? = null): String? = encryptedPreferences.getString(key, defaultValue)

    /**
     * Boolean
     */
    @Synchronized
    fun set(key: String, value: Boolean) {
        encryptedPreferences.edit {
            putBoolean(key, value)
        }
    }

    @Synchronized
    fun get(key: String, defaultValue: Boolean = true): Boolean = encryptedPreferences.getBoolean(key, defaultValue)

    fun del(key: String) {
        encryptedPreferences.edit {
            remove(key)
        }
    }

}