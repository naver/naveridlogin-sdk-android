package com.navercorp.nid.oauth

import com.navercorp.naverid.NaverIdTestCase
import com.navercorp.nid.oauth.legacy.OAuthErrorCode
import org.junit.Assert
import org.junit.Test

class NidOAuthErrorCodeTest: NaverIdTestCase() {

    @Test
    fun fromString_테스트() {

        Assert.assertEquals(NidOAuthErrorCode.NONE, NidOAuthErrorCode.fromString(null))

        for (errorCode in NidOAuthErrorCode.values()) {
            when (errorCode) {
                NidOAuthErrorCode.CLIENT_ERROR_NO_CLIENTID, NidOAuthErrorCode.CLIENT_ERROR_NO_CLIENTSECRET,
                NidOAuthErrorCode.CLIENT_ERROR_NO_CLIENTNAME, NidOAuthErrorCode.CLIENT_ERROR_NO_CALLBACKURL -> {
                    Assert.assertEquals(NidOAuthErrorCode.SERVER_ERROR_INVALID_REQUEST, NidOAuthErrorCode.fromString(errorCode.code))
                }
                NidOAuthErrorCode.CLIENT_ERROR_CONNECTION_ERROR, NidOAuthErrorCode. CLIENT_ERROR_CERTIFICATION_ERROR -> {
                    Assert.assertEquals(NidOAuthErrorCode.SERVER_ERROR_SERVER_ERROR, NidOAuthErrorCode.fromString(errorCode.code))
                }
                else -> {
                    Assert.assertEquals(errorCode, NidOAuthErrorCode.fromString(errorCode.code))
                }
            }

            Assert.assertEquals(errorCode, NidOAuthErrorCode.fromString(errorCode.name))

        }

        Assert.assertEquals(NidOAuthErrorCode.ERROR_NO_CATAGORIZED, NidOAuthErrorCode.fromString("value"))

    }

    @Test
    fun fromString_리그레션_테스트() {
        for (errorCode in NidOAuthErrorCode.values()) {
            Assert.assertEquals(
                OAuthErrorCode.fromString(errorCode.code).name,
                NidOAuthErrorCode.fromString(errorCode.code).name
            )
            Assert.assertEquals(
                OAuthErrorCode.fromString(errorCode.name).name,
                NidOAuthErrorCode.fromString(errorCode.name).name
            )
        }
        Assert.assertEquals(
            OAuthErrorCode.fromString("value").name,
            NidOAuthErrorCode.fromString("value").name
        )
    }




}