package com.navercorp.nid.oauth.api

import com.navercorp.naverid.NaverIdTestCase
import com.navercorp.nid.oauth.NidOAuthConstants
import com.navercorp.nid.oauth.data.NidOAuthResponse
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

class NidOAuthApiTest: NaverIdTestCase() {

    private lateinit var mockWebServer: MockWebServer

    private val httpClient = OkHttpClient()
        .newBuilder()
        .readTimeout(NidOAuthConstants.TIME_OUT, TimeUnit.MILLISECONDS)
        .connectTimeout(NidOAuthConstants.TIME_OUT, TimeUnit.MILLISECONDS)
        .build()

    lateinit var service: NidOAuthLoginService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()

        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val requestUrl = request.requestUrl!!
                return when {
                    requestUrl.queryParameter("client_id").isNullOrEmpty() -> {
                        MockResponse()
                            .setResponseCode(200)
                            .setBody("{\"error\":\"invalid_request\",\"error_description\":\"client_id is missing.\"}")
                    }
                    requestUrl.queryParameter("client_secret").isNullOrEmpty() -> {
                        MockResponse()
                            .setResponseCode(200)
                            .setBody("{\"error\":\"invalid_request\",\"error_description\":\"client_secret is missing.\"}")
                    }
                    else -> {
                        when (requestUrl.queryParameter("grant_type")) {
                            "authorization_code" -> {
                                MockResponse()
                                    .setResponseCode(200)
                                    .setBody("{\"access_token\":\"AAAARzsU7xX2zISMBWCmkFVRRwhKtOTl9Sw16gGgeo7rX7OTrVNVvqDCQVkznD8a-Ja4OyuQuns0Q0cuajeZl0dqESjtI0ibOLg212z70aZUmwMG\",\"refresh_token\":\"QElDlSkFGipTkQTSUI11p3isgVVYTV5rvxEqIU5kT7SzthVbnuqoFVLzCtdIT53NpQJ32Ijbto392LF9q87rVwAYxoeBZkSdIipisii2jipL7mzxURYOPr7K7j427clS2PCTism\",\"token_type\":\"bearer\",\"expires_in\":\"3600\"}")
                            }
                            "refresh_token" -> {
                                MockResponse()
                                    .setResponseCode(200)
                                    .setBody("{\"access_token\":\"AAAAR4YPjO8go14V7i89y8pyRATLmPW8DogZmqFqd93/gwlTbND9icIXhmzFwBCcTSiB/YoKleMn/KLFKDCh2Km/0kUaiGdL/Iywmj6OvsFYGL+h\",\"refresh_token\":\"QElDlSkFGipTkQTSUI11p3isgVVYTV5rvxEqIU5kT7SzthVbnuqoFVLzCtdIT53NpQJ32Ijbto392LF9q87rVwAYxoeBZkSdIipisii2jipL7mzxURYOPr7K7j427clS2PCTism\",\"token_type\":\"bearer\",\"expires_in\":\"3600\"}")
                            }
                            "delete" -> {
                                MockResponse()
                                    .setResponseCode(200)
                                    .setBody("{\"result\":\"success\",\"access_token\":\"AAAAR4YPjO8go14V7i89y8pyRATLmPW8DogZmqFqd93/gwlTbND9icIXhmzFwBCcTSiB/YoKleMn/KLFKDCh2Km/0kUaiGdL/Iywmj6OvsFYGL+h\"}")
                            }
                            else -> {
                                MockResponse()
                                    .setResponseCode(200)
                                    .setBody("")
                            }
                        }
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
            .create(NidOAuthLoginService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun requestAccessToken_sucess_테스트() {
        runBlocking {
            val api = service.requestAccessToken(clientId = "clientId", clientSecret = "clientSecret", state = "state", code = "code", version = "android-4.4.1", locale = "ko_KR")

            val response = NidOAuthResponse("AAAARzsU7xX2zISMBWCmkFVRRwhKtOTl9Sw16gGgeo7rX7OTrVNVvqDCQVkznD8a-Ja4OyuQuns0Q0cuajeZl0dqESjtI0ibOLg212z70aZUmwMG",
                                            "QElDlSkFGipTkQTSUI11p3isgVVYTV5rvxEqIU5kT7SzthVbnuqoFVLzCtdIT53NpQJ32Ijbto392LF9q87rVwAYxoeBZkSdIipisii2jipL7mzxURYOPr7K7j427clS2PCTism",
                                            "bearer", "3600", null, null, null
            )

            val oAuthResponse = api.body()!!

            Assert.assertTrue(response == oAuthResponse)
        }
    }

    @Test
    fun requestAccessToken_fail_테스트() {
        runBlocking {
            // clientId is empty
            var api = service.requestAccessToken(clientId = "", clientSecret = "clientSecret", state = "state", code = "code", version = "android-4.4.1", locale = "ko_KR")

            var response = NidOAuthResponse(null, null, null, null, null,
                "invalid_request", "client_id is missing."
            )

            var oAuthResponse = api.body()!!

            Assert.assertTrue(response == oAuthResponse)

            // clientSecret is empty
            api = service.requestAccessToken(clientId = "clientId", clientSecret = "", state = "state", code = "code", version = "android-4.4.1", locale = "ko_KR")

            response = NidOAuthResponse(null, null, null, null, null,
                "invalid_request", "client_secret is missing."
            )

            oAuthResponse = api.body()!!

            Assert.assertTrue(response == oAuthResponse)
        }
    }

    @Test
    fun requestRefreshToken_sucess_테스트() {
        runBlocking {
            val api = service.requestRefreshToken(clientId = "clientId", clientSecret = "clientSecret", refreshToken = "refreshToken", version = "android-4.4.1", locale = "ko_KR")

            val response = NidOAuthResponse("AAAAR4YPjO8go14V7i89y8pyRATLmPW8DogZmqFqd93/gwlTbND9icIXhmzFwBCcTSiB/YoKleMn/KLFKDCh2Km/0kUaiGdL/Iywmj6OvsFYGL+h",
                                            "QElDlSkFGipTkQTSUI11p3isgVVYTV5rvxEqIU5kT7SzthVbnuqoFVLzCtdIT53NpQJ32Ijbto392LF9q87rVwAYxoeBZkSdIipisii2jipL7mzxURYOPr7K7j427clS2PCTism",
                                            "bearer", "3600", null, null, null
            )

            val oAuthResponse = api.body()!!

            Assert.assertTrue(response == oAuthResponse)
        }
    }

    @Test
    fun deleteToken_sucess_테스트() {
        runBlocking {
            val api = service.requestDeleteToken(clientId = "clientId", clientSecret = "clientSecret", accessToken = "accessToken", version = "android-4.4.1", locale = "ko_KR")

            val oAuthResponse = api.body()!!

            val response = NidOAuthResponse("AAAAR4YPjO8go14V7i89y8pyRATLmPW8DogZmqFqd93/gwlTbND9icIXhmzFwBCcTSiB/YoKleMn/KLFKDCh2Km/0kUaiGdL/Iywmj6OvsFYGL+h",
                                            null, null, null, "success", null, null
            )

            Assert.assertEquals(3600L, oAuthResponse.expiresIn)
            Assert.assertTrue(response == oAuthResponse)
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