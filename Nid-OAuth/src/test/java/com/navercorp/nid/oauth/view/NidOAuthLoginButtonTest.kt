package com.navercorp.nid.oauth.view

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.navercorp.naverid.NaverIdTestCase
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest

class NidOAuthLoginButtonTest: NaverIdTestCase() {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun init_테스트() {
        val nidOAuthLoginButton = NidOAuthLoginButton(context)

        nidOAuthLoginButton.init()

        Assert.assertNotNull(nidOAuthLoginButton.drawable)
        Assert.assertTrue(nidOAuthLoginButton.hasOnClickListeners())
    }

    @Test
    fun setOAuthLoginCallback_테스트() {
        val nidOAuthLoginButton = NidOAuthLoginButton(context)

        val callback = Mockito.mock(OAuthLoginCallback::class.java)
        nidOAuthLoginButton.setOAuthLoginCallback(callback)

        Assert.assertEquals(callback, NidOAuthLoginButton.oauthLoginCallback)
    }

    @Test
    fun onClick_테스트() {
        val nidOAuthLoginButton = NidOAuthLoginButton(context)

        val callback = Mockito.mock(OAuthLoginCallback::class.java)
        nidOAuthLoginButton.setOAuthLoginCallback(callback)

        mockkObject(NaverIdLoginSDK)
        every { NaverIdLoginSDK.authenticate(context, callback) } returns Unit

        nidOAuthLoginButton.performClick()

        verify { NaverIdLoginSDK.authenticate(context, callback) }

    }

}