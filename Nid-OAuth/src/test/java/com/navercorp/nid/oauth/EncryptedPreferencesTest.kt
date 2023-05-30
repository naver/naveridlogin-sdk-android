package com.navercorp.nid.oauth

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.navercorp.naverid.NaverIdTestCase
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class EncryptedPreferencesTest: NaverIdTestCase() {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    var preferences = context.getSharedPreferences("Prefs", Context.MODE_PRIVATE)

    @Before
    fun setUp() {
        EncryptedPreferences.setContext(context)
    }

    @Test
    fun Int_저장_테스트() {
        EncryptedPreferences.set("key", 100)

        assertEquals(100, EncryptedPreferences.get("key", 0))
    }

    @Test
    fun Long_저장_테스트() {
        EncryptedPreferences.set("key", 1000L)

        assertEquals(1000L, EncryptedPreferences.get("key", 0L))
    }

    @Test
    fun String_저장_테스트() {
        EncryptedPreferences.set("key", null)

        assertEquals(null, EncryptedPreferences.get("key", null))

        EncryptedPreferences.set("key", "value")

        assertEquals("value", EncryptedPreferences.get("key", null))
    }

    @Test
    fun Boolean_저장_테스트() {
        EncryptedPreferences.set("key", true)

        assertEquals(true, EncryptedPreferences.get("key", false))
    }

    @Test
    fun Int_리그레션_저장_테스트() {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putInt("key", 100)
        editor.commit()

        EncryptedPreferences.set("key", 100)

        assertEquals(preferences.getInt( "key", 0).toLong(), EncryptedPreferences.get("key", 0).toLong())
    }

    @Test
    fun Long_리그레션_저장_테스트() {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putLong("key", 1000L)
        editor.commit()

        EncryptedPreferences.set("key", 1000L)

        assertEquals(preferences.getLong("key", 0L), EncryptedPreferences.get("key", 0L))
    }

    @Test
    fun String_리그레션_저장_테스트() {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("key", null)
        editor.commit()

        EncryptedPreferences.set("key", null)

        assertEquals(preferences.getString("key", null), EncryptedPreferences.get("key", null))

        editor.putString("key", "value")
        editor.commit()

        EncryptedPreferences.set("key", "value")

        assertEquals(preferences.getString("key", null), EncryptedPreferences.get("key", null))
    }

    @Test
    fun Boolean_리그레션_저장_테스트() {
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putBoolean("key", true)
        editor.commit()

        EncryptedPreferences.set("key", true)

        assertEquals(preferences.getBoolean("key", false), EncryptedPreferences.get("key", false))
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            FakeAndroidKeyStore.setup
        }
    }

}