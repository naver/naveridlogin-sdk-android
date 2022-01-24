package com.navercorp.nid.profile.api

import com.navercorp.naverid.NaverIdTestCase
import com.navercorp.nid.oauth.NidOAuthConstants
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NidProfileApiTest: NaverIdTestCase() {
    private lateinit var mockWebServer: MockWebServer

    private val httpClient = OkHttpClient()
        .newBuilder()
        .readTimeout(NidOAuthConstants.TIME_OUT, TimeUnit.MILLISECONDS)
        .connectTimeout(NidOAuthConstants.TIME_OUT, TimeUnit.MILLISECONDS)
        .build()

    lateinit var service: NidProfileService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()

        val successResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"resultcode\":\"00\",\"message\":\"success\",\"response\":{\"id\":\"210510450\",\"nickname\":\"nv_\",\"birthyear\":\"1975\"}}")

        val failedResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"resultcode\":\"024\",\"message\":\"Authentication failed (\\uc778\\uc99d \\uc2e4\\ud328\\ud558\\uc600\\uc2b5\\ub2c8\\ub2e4.)\"}")

        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.getHeader("Authorization")?.startsWith("Bearer ")) {
                    true -> {
                        successResponse
                    }
                    else -> {
                        failedResponse
                    }
                }
            }
        }

        mockWebServer.dispatcher = dispatcher
        mockWebServer.url("/")

        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NidProfileService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun requestApi_success_테스트() {
        val accessToken = "AAAAQw26u-lI6vw1pHxF2F8ltJVu62b4D3ieVolKijPzGvl7Y2Xdr8FcytRYjySsnufl3axtLrKNImBkACGse1Sadq09CfVeuglxsi3jCYrb28bY"
        runBlocking {
            val api = service.requestApi("Bearer $accessToken")

            val profileResponse = api.body()

            Assert.assertTrue(api.isSuccessful)
            Assert.assertNotNull(profileResponse)
            Assert.assertEquals("00",profileResponse?.resultCode)
            Assert.assertEquals("success",profileResponse?.message)
            Assert.assertNotNull(profileResponse?.profile?.id)

        }
    }

    @Test
    fun requestApi_Authentication_failed_테스트() {
        val accessToken = "AAAAQw26u-lI6vw1pHxF2F8ltJVu62b4D3ieVolKijPzGvl7Y2Xdr8FcytRYjySsnufl3axtLrKNImBkACGse1Sadq09CfVeuglxsi3jCYrb28bY"
        runBlocking {
            val api = service.requestApi("$accessToken")

            val profileResponse = api.body()

            Assert.assertTrue(api.isSuccessful)
            Assert.assertNotNull(profileResponse)
            Assert.assertEquals("024",profileResponse?.resultCode)
            Assert.assertNotNull(profileResponse?.message)
            Assert.assertTrue(profileResponse?.message!!.contains("Authentication failed"))
            Assert.assertNull(profileResponse.profile)

        }
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            System.setProperty("javax.net.ssl.trustStore", "NONE")
        }
    }
}