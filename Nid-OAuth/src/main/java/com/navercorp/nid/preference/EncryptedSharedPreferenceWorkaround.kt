package com.navercorp.nid.preference

import android.content.Context

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