package com.navercorp.nid.util

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.navercorp.naverid.NaverIdTestCase
import com.navercorp.nid.util.legacy.DeviceUtil
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NidDeviceUtilTest: NaverIdTestCase() {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun getSystemLocale_테스트() {
        assertNotNull(NidDeviceUtil.getSystemLocale(context))
    }

    @Test
    fun getLocale_테스트() {
        assertNotNull(NidDeviceUtil.getLocale(context))
    }

    @Test
    fun isKorean_테스트() {
        val language = NidDeviceUtil.getSystemLocale(context).language
        if(language.startsWith("ko")) {
            assertTrue(true);
        } else {
            assertFalse(false);
        }
    }

    @Test
    fun getSystemLocale_리그레션_테스트() {
        assertEquals( DeviceUtil.getSystemLocale(context), NidDeviceUtil.getSystemLocale(context))
    }

    @Test
    fun getLocale_리그레션_테스트() {
        assertEquals( DeviceUtil.getLocale(context), NidDeviceUtil.getLocale(context))
    }

    @Test
    fun isKorean_리그레션_테스트() {
        assertEquals( DeviceUtil.isKorean(context), NidDeviceUtil.isKorean(context))
    }

}