package com.navercorp.nid.util

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.navercorp.naverid.NaverIdTestCase
import com.navercorp.nid.util.legacy.NetworkState
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NidNetworkUtilTest: NaverIdTestCase() {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun getType_테스트() {
        var type = NidNetworkUtil.getType(context)

        if (isConnected(context, ConnectivityManager.TYPE_MOBILE)) {
            assertEquals("cell", type)
        } else if (isConnected(context, ConnectivityManager.TYPE_WIFI)) {
            assertEquals("wifi", type)
        } else {
            assertEquals("other", type)
        }

    }

    @Test
    fun isAvailable_테스트() {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var info = manager.activeNetworkInfo
        val isAvailable =  (info != null && info.isConnected)

        if (isAvailable) {
            assertTrue(NidNetworkUtil.isAvailable(context))
        } else {
            assertFalse(NidNetworkUtil.isAvailable(context))
        }
    }

    @Test
    fun isNotAvailable_테스트() {

        val isAvailable = NidNetworkUtil.isAvailable(context)

        if (isAvailable) {
            assertFalse(NidNetworkUtil.isNotAvailable(context))
        } else {
            assertTrue(NidNetworkUtil.isNotAvailable(context))
        }
    }

    @Test
    fun getType_리그레션_테스트() {
        assertEquals( NetworkState.getNetworkState(context), NidNetworkUtil.getType(context))
    }

    @Test
    fun isAvailable_리그레션_테스트() {
        assertEquals( NetworkState.isDataConnected(context), NidNetworkUtil.isAvailable(context))
    }

    @Test
    fun isNotAvailable_리그레션_테스트() {
        assertNotEquals( NetworkState.isDataConnected(context), NidNetworkUtil.isNotAvailable(context))
    }

    private fun isConnected(context: Context, connectType: Int): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (manager != null) {
            if (Build.VERSION.SDK_INT < AndroidVer.API_23_MARSHMALLOW) {

                val networkInfo = manager.getNetworkInfo(connectType)
                if (networkInfo != null && networkInfo.isConnected) {
                    return true
                }
            } else {
                val networks = manager.allNetworks
                networks.forEach {
                    val info = manager.getNetworkInfo(it)
                    if (info != null && info.type == connectType && info.isConnected) {
                        return true
                    }
                }
            }
        }
        return false
    }

}