package com.navercorp.nid.util

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.navercorp.naverid.NaverIdTestCase
import com.navercorp.nid.util.legacy.ApplicationUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class UserAgentFactoryTest: NaverIdTestCase() {

    lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun create_테스트() {
        val userAgent = UserAgentFactory.create(context)

        val versionInfo = "Android/${Build.VERSION.RELEASE}".refine()
        assertTrue(userAgent.contains(versionInfo))

        val modelInfo = "Model/${Build.MODEL}".refine()
        assertTrue(userAgent.contains(modelInfo))

        assertTrue(userAgent.contains("uid"))

        val sdkInfo = "OAuthLoginMod/${OAuthLogin.getVersion()}".refine()
        assertTrue(userAgent.contains(sdkInfo))
    }

    @Test
    fun UserAgentFactory_create_리그레션_테스트() {
        assertEquals(
            ApplicationUtil.getUserAgent(context),
            UserAgentFactory.create(context)
        )
    }
}