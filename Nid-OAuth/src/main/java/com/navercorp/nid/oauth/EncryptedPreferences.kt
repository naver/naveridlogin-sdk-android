package com.navercorp.nid.oauth

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.log.NidLog
import com.navercorp.nid.util.AndroidVer

private const val TAG = "EncryptedPreferences"

private const val OAUTH_LOGIN_PREF_NAME_PER_APP  = "NaverOAuthLoginEncryptedPreferenceData"

object EncryptedPreferences {

    private var context: Context? = null
    private fun getCtx() = context ?: NaverIdLoginSDK.applicationContext

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(getCtx())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .setUserAuthenticationRequired(false)
            .build()
    }

    private val encryptedPreferences: SharedPreferences by lazy {
        init()
    }

    private fun init(): SharedPreferences = EncryptedSharedPreferences
        .create(
            getCtx(),
            OAUTH_LOGIN_PREF_NAME_PER_APP,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    @JvmStatic
    fun setContext(context: Context) {
        this.context = context

        kotlin.runCatching {
            migration()
        }.onFailure {
            deleteCurrentEncryptedPreference()
            migration()
        }
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

    private const val OLD_OAUTH_LOGIN_PREF_NAME  = "NaverOAuthLoginPreferenceData"

    private fun migration() {

        // 마이그레이션 필요 없음
        if (!NidOAuthPreferencesManager.clientId.isNullOrEmpty()) {
            return
        }

        var oldPreference = getCtx().getSharedPreferences(OLD_OAUTH_LOGIN_PREF_NAME, Context.MODE_PRIVATE)

        // 마이그레이션
        kotlin.runCatching {
            moveToData(oldPreference)
        }.onFailure { e ->
            if (e is SecurityException) {
                // 네아로 v5.0.0 미만 데이터 삭제
                oldPreference.edit {
                    NidOAuthPreferencesManager.keyList.forEach { key ->
                        remove(key)
                    }
                }

                oldPreference = EncryptedSharedPreferences.create(
                    getCtx(),
                    OLD_OAUTH_LOGIN_PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
                moveToData(oldPreference)

                oldPreference.edit {
                    clear()
                }
            }
        }.also {
            if (Build.VERSION.SDK_INT >= AndroidVer.API_24_NOUGAT) {
                getCtx().deleteSharedPreferences(OLD_OAUTH_LOGIN_PREF_NAME)
            } else {
                oldPreference.edit {
                    clear()
                }
            }
        }
    }

    private fun deleteCurrentEncryptedPreference(){
        val preferences = getCtx().getSharedPreferences(OAUTH_LOGIN_PREF_NAME_PER_APP, Context.MODE_PRIVATE)

        if (Build.VERSION.SDK_INT >= AndroidVer.API_24_NOUGAT) {
            getCtx().deleteSharedPreferences(OLD_OAUTH_LOGIN_PREF_NAME)
        } else {
            preferences.edit {
                clear()
            }
        }
    }

    @Throws(SecurityException::class)
    private fun moveToData(preferences: SharedPreferences) {
        for (entry in preferences.all.entries) {
            val key = entry.key
            val value: Any? = entry.value
            set(key, value)
        }
    }

    @Throws(SecurityException::class)
    private fun set(key: String, value: Any?) {
        when (value) {
            is Int -> set(key, value)
            is Long -> set(key, value)
            is String? -> set(key, value)
            is Boolean -> set(key, value)
            else -> {
                NidLog.d(TAG, "Preferences Set() fail | key:$key")
            }
        }
    }

}