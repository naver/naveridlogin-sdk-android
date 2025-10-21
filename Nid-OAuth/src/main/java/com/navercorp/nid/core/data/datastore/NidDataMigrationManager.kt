package com.navercorp.nid.core.data.datastore

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.core.log.NidLog
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

object NidDataMigrationManager {
    private const val TAG = "NidDataMigrationManager"
    private const val DEFAULT_TRY_COUNT = 3
    private val appContext: Context
        get() = NidOAuth.getApplicationContext()
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(appContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .setUserAuthenticationRequired(false)
            .build()
    }
    private val encryptedPrefs: SharedPreferences by lazy {
        createEncryptedPreferences()
    }

    /**
     * 마이그레이션 진행 중인지 여부 반환
     *
     * @return 마이그레이션 진행 중 여부
     */
    private val _isMigrating = AtomicBoolean(false)
    val isMigrating: Boolean
        get() = _isMigrating.get()

    /**
     * SharedPreferences 파일 이름 상수
     */
    private const val OLD_OAUTH_LOGIN_PREF_NAME = "NaverOAuthLoginPreferenceData"
    private const val ENCRYPTED_OAUTH_LOGIN_PREF_NAME = "NaverOAuthLoginEncryptedPreferenceData"

    /**
     * dataStore 마이그레이션 필요 여부 판단 core data key
     */
    private enum class CoreClientInfo(val key: String) {
        CLIENT_ID("CLIENT_ID"),
        CLIENT_SECRET("CLIENT_SECRET"),
        CLIENT_NAME("CLIENT_NAME")
    }

    /**
     * 모든 저장소에서 NidDataStore로 마이그레이션 진행
     * 마이그레이션 실패 시 OAuth가 재진행되면 되기 때문에 별도의 처리 x
     *
     * 마이그레이션 우선순위:
     * 1. EncryptedPreferences가 있으면 -> DataStore로 직접 마이그레이션
     * 2. SharedPreferences가 있으면 -> DataStore로 직접 마이그레이션
     * 3. 둘 다 없으면 -> 마이그레이션 스킵
     */
    suspend fun migrateDataFromLegacyStores() {
        // dataStore에 이미 core data가 존재한다면 마이그레이션 완료된 상태
        if (!isMigrationNeeded()) {
            NidLog.i(TAG, "Already migrated to DataStore")
            return
        }

        try {
            // 저장소 존재 여부 확인
            val hasSharedPreferences = hasDataInSharedPreferences()
            val hasEncryptedPreferences = hasDataInEncryptedPreferences()

            // 마이그레이션할 데이터가 없으면 종료
            if (!hasSharedPreferences && !hasEncryptedPreferences) {
                NidLog.i(TAG, "No legacy data found to migrate")
                return
            }

            // 마이그레이션 진행 세팅
            _isMigrating.set(true)

            // 마이그레이션 진행
            if (hasEncryptedPreferences) {
                // EncryptedPreferences -> DataStore 마이그레이션 및 정리
                val encryptedPrefs = encryptedPrefs
                migrateFromSharedPreferencesToDataStore(encryptedPrefs, ENCRYPTED_OAUTH_LOGIN_PREF_NAME)
                clearPreferencesData(encryptedPrefs)
            } else {
                // SharedPreferences -> DataStore 마이그레이션 및 정리
                val sharedPrefs = appContext.getSharedPreferences(OLD_OAUTH_LOGIN_PREF_NAME, Context.MODE_PRIVATE)
                migrateFromSharedPreferencesToDataStore(sharedPrefs, OLD_OAUTH_LOGIN_PREF_NAME)
                clearPreferencesData(sharedPrefs)
            }

            NidLog.i(TAG, "Migration completed successfully")
            _isMigrating.set(false)
        } catch (e: Exception) {
            NidLog.e(TAG, "Migration failed: ${e.message}")
            _isMigrating.set(false)
        } finally {
            // 마이그레이션 성공 여부와 상관없이 기존 SharedPreferences 파일 모두 삭제
            deleteOldSharedPreferences()
        }
    }

    /**
     * SharedPreferences에 마이그레이션 필요 데이터 확인
     *
     * @return 마이그레이션 필요 데이터 존재 여부
     */
    private fun hasDataInSharedPreferences(): Boolean {
        return try {
            getSharedPreferencesFile(OLD_OAUTH_LOGIN_PREF_NAME) ?: return false
            val sharedPrefs = appContext.getSharedPreferences(OLD_OAUTH_LOGIN_PREF_NAME, Context.MODE_PRIVATE)
            val allData = sharedPrefs.all
            allData.isNotEmpty() && allData.any { it.value != null }
        } catch (e: Exception) {
            NidLog.w(TAG, "Failed to check SharedPreferences data: ${e.message}")
            false
        }
    }

    /**
     * EncryptedPreferences에 마이그레이션 필요 데이터 확인
     *
     * @return 마이그레이션 필요 데이터 존재 여부
     */
    private fun hasDataInEncryptedPreferences(): Boolean {
        return try {
            getSharedPreferencesFile(ENCRYPTED_OAUTH_LOGIN_PREF_NAME) ?: return false
            val encryptedPrefs = encryptedPrefs
            val allData = encryptedPrefs.all
            allData.isNotEmpty() && allData.any { it.value != null }
        } catch (e: Exception) {
            NidLog.w(TAG, "Failed to check EncryptedPreferences data: ${e.message}")
            false
        }
    }

    /**
     * EncryptedPreferences 인스턴스 획득
     *
     * @return EncryptedSharedPreferences 인스턴스
     */
    private fun createEncryptedPreferences(): SharedPreferences = try {
        EncryptedSharedPreferences.create(
            appContext,
            ENCRYPTED_OAUTH_LOGIN_PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        NidLog.e(TAG, "Failed to create EncryptedSharedPreferences: ${e.message}")
        throw e
    }

    /**
     * SharedPreferences에서 DataStore로 마이그레이션
     *
     * @param prefs 마이그레이션을 진행할 SharedPreferences 인스턴스
     */
    private suspend fun migrateFromSharedPreferencesToDataStore(prefs: SharedPreferences, prefName: String) {
        try {
            val allData = prefs.all

            // 마이그레이션 할 데이터 없음
            if (allData.isEmpty()) return

            // 데이터 마이그레이션 진행
            for ((key, value) in allData) {
                if (value == null) continue

                val isMigrationSuccess = migrationItemWithRetry(key, value)
                if (!isMigrationSuccess) {
                    throw Exception("Failed to migrate key: $key after $DEFAULT_TRY_COUNT attempts")
                }
            }
        } catch (e: Exception) {
            NidLog.w(TAG, "Failed to migrate from $prefName sharedPreferences: ${e.message}")
            throw e
        }
    }

    /**
     * 단일 아이템 마이그레이션 시도 (최대 3번)
     *
     * @param key 마이그레이션할 데이터의 키
     * @param value 마이그레이션할 데이터의 값
     * @return 마이그레이션 성공 여부
     */
    private suspend fun migrationItemWithRetry(key: String, value: Any): Boolean {
        repeat(DEFAULT_TRY_COUNT) {
            try {
                saveData(key, value)
                NidLog.i(TAG, "Successfully migrated key: $key, value: $value")
                return true
            } catch (e: Exception) {
                NidLog.w(TAG, "Migration failed for key: $key, error: ${e.message}, attempts left: ${DEFAULT_TRY_COUNT - it - 1}")
            }
        }
        return false
    }

    /**
     * 기존 SharedPreferences 파일 삭제
     */
    private fun deleteOldSharedPreferences() {
        try {
            deleteLegacyStore(OLD_OAUTH_LOGIN_PREF_NAME)
            deleteLegacyStore(ENCRYPTED_OAUTH_LOGIN_PREF_NAME)
            NidLog.i(TAG, "successfully delete old SharedPreferences files")
        } catch (e: Exception) {
            NidLog.w(TAG, "Failed to delete old sharedPreferences: ${e.message}")
        }
    }

    /**
     * Preferences 데이터 삭제
     *
     * @param prefs 데이터 삭제할 SharedPreferences 인스턴스
     */
    private fun clearPreferencesData(prefs: SharedPreferences) {
        try {
            prefs.edit { clear() }
        } catch (e: Exception) {
            NidLog.w(TAG, "Failed to clear sharedPreferences data: ${e.message}")
            throw e
        }
    }

    /**
     * SharedPreferences 파일 삭제
     *
     * @param prefName 삭제할 SharedPreferences 이름
     */
    private fun deleteLegacyStore(prefName: String) {
        try {
            // 파일이 존재하지 않은 경우 리턴
            val prefsFile = getSharedPreferencesFile(prefName) ?: return

            // 파일 삭제 버저닝 처리
            val isDelete = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                appContext.deleteSharedPreferences(prefName)
            } else {
                prefsFile.delete()
            }

            if (isDelete) {
                NidLog.i(TAG, "$prefName sharedPreferences file deleted successfully")
            } else {
                NidLog.w(TAG, "Failed to delete $prefName sharedPreferences file")
            }
        } catch (e: Exception) {
            NidLog.w(TAG, "Failed to delete $prefName sharedPreferences file: ${e.message}")
            throw e
        }
    }

    /**
     * NidDataStore에 데이터 저장
     *
     * @param key 저장할 데이터의 키
     * @param value 저장할 데이터의 값 (String, Int, Long, Boolean, Float, Double 타입 지원)
     */
    private suspend fun saveData(key: String, value: Any?) {
        when (value) {
            is String -> NidOAuthLocalDataSource.save(key, value)
            is Int -> NidOAuthLocalDataSource.save(key, value)
            is Long -> NidOAuthLocalDataSource.save(key, value)
            is Boolean -> NidOAuthLocalDataSource.save(key, value)
            is Float -> NidOAuthLocalDataSource.save(key, value)
            is Double -> NidOAuthLocalDataSource.save(key, value)
            else -> {
                NidLog.w(TAG, "Unsupported data type for key: $key")
                throw Exception("Failed to save data for key: $key")
            }
        }
    }

    /**
     * Android Api 24 미만에서만 사용
     * SharedPreferences 파일 가져오기
     *
     * @param prefName SharedPreferences 이름
     * @return 찾은 preferences 파일, 없으면 null
     */
    private fun getSharedPreferencesFile(prefName: String): File? {
        val sharedPrefsDir = File(appContext.applicationInfo.dataDir, "shared_prefs")
        val prefFile = File(sharedPrefsDir, "$prefName.xml")
        return if (prefFile.exists()) prefFile else null
    }

    /**
     * 마이그레이션 필요 여부 확인
     * CoreClientInfo에 정의된 키들이 모두 NidDataStore에 존재하는지 확인
     *
     * @return 마이그레이션 필요 여부
     */
    private suspend fun isMigrationNeeded(): Boolean = try {
        CoreClientInfo.entries.any {
            NidOAuthLocalDataSource.load(it.key, null).isNullOrEmpty()
        }
    } catch (_: Exception) {
        false
    }
}