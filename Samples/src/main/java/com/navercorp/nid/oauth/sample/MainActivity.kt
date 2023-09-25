package com.navercorp.nid.oauth.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.oauth.NidOAuthBehavior
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.sample.databinding.ActivityMainBinding
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileMap
import com.navercorp.nid.profile.data.NidProfileResponse

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding
    private lateinit var context: Context

    private var clientId = "jyvqXeaVOVmV"
    private var clientSecret = "527300A0_COq1_XV33cf"
    private var clientName = "네이버 아이디로 로그인"

    private val launcher = registerForActivityResult<Intent, ActivityResult>(ActivityResultContracts.StartActivityForResult()) { result ->
        when(result.resultCode) {
            RESULT_OK -> {
                // 성공
                updateView()
            }
            RESULT_CANCELED -> {
                // 실패 or 에러
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(context, "errorCode:$errorCode, errorDesc:$errorDescription", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        context = this
        // Initialize NAVER id login SDK
        NaverIdLoginSDK.apply {
            showDevelopersLog(true)
            initialize(context, clientId, clientSecret, clientName)
            isShowMarketLink = true
            isShowBottomTab = true
        }

        binding.buttonOAuthLoginImg.setOAuthLogin(object : OAuthLoginCallback {
            override fun onSuccess() {
                updateView()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(
                    context,
                    "errorCode:$errorCode, errorDesc:$errorDescription",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }

        })

        // 로그인 Launcher
        binding.loginLauncher.setOnClickListener {
            NaverIdLoginSDK.behavior = NidOAuthBehavior.DEFAULT
            NaverIdLoginSDK.authenticate(context, launcher)
        }

        // 로그인 Callback
        binding.loginCallback.setOnClickListener {
            NaverIdLoginSDK.behavior = NidOAuthBehavior.DEFAULT
            NaverIdLoginSDK.authenticate(context, object : OAuthLoginCallback {
                override fun onSuccess() {
                    updateView()
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Toast.makeText(
                        context,
                        "errorCode:$errorCode, errorDesc:$errorDescription",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            })
        }

        // 로그아웃
        binding.logout.setOnClickListener {
            NaverIdLoginSDK.logout()
            updateView()
        }

        // 연동 끊기
        binding.deleteToken.setOnClickListener {
            NidOAuthLogin().callDeleteTokenApi(object : OAuthLoginCallback {
                override fun onSuccess() {
                    updateView()
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Toast.makeText(
                        context,
                        "errorCode:$errorCode, errorDesc:$errorDescription",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateView()
                }

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            })
        }

        // 토큰 갱신
        binding.refreshToken.setOnClickListener {
            NidOAuthLogin().callRefreshAccessTokenApi(object : OAuthLoginCallback {
                override fun onSuccess() {
                    updateView()
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Toast.makeText(
                        context,
                        "errorCode:$errorCode, errorDesc:$errorDescription",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateView()
                }

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }

            })
        }

        // Api 호출
        binding.profileApi.setOnClickListener {
            NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                override fun onSuccess(response: NidProfileResponse) {
                    Toast.makeText(
                        context,
                        "$response",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.tvApiResult.text = response.toString()
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Toast.makeText(
                        context,
                        "errorCode:$errorCode, errorDesc:$errorDescription",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.tvApiResult.text = ""
                }

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            })
        }

        // 프로필 Map 호출
        binding.profileMapApi.setOnClickListener {
            NidOAuthLogin().getProfileMap(object : NidProfileCallback<NidProfileMap> {
                override fun onSuccess(result: NidProfileMap) {
                    Toast.makeText(
                        context,
                        "$result",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.tvApiResult.text = result.toString()
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Toast.makeText(
                        context,
                        "errorCode:$errorCode, errorDesc:$errorDescription",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.tvApiResult.text = ""
                }

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            })
        }

        // 네이버앱 로그인 (Callback)
        binding.loginWithNaverapp.setOnClickListener {
            NaverIdLoginSDK.behavior = NidOAuthBehavior.NAVERAPP
//            NaverIdLoginSDK.naverappIntentFlag = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK  // naverappIntent 생성 시 flag 추가
            NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback {
                override fun onSuccess() {
                    updateView()
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Toast.makeText(
                        context,
                        "errorCode:$errorCode, errorDesc:$errorDescription",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }

            })
        }

        // 커스텀탭 로그인 Callback
        binding.loginWithCustomtabs.setOnClickListener {
            NaverIdLoginSDK.behavior = NidOAuthBehavior.CUSTOMTABS
//            OAuthLogin.getInstance().setCustomTabReAuth(false) // 무조건 재인증시 true
            NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback {
                override fun onSuccess() {
                    updateView()
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Toast.makeText(
                        context,
                        "errorCode:$errorCode, errorDesc:$errorDescription",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }

            })
        }

        // 웹뷰 로그인
        binding.loginWithWebView.setOnClickListener {
            NaverIdLoginSDK.behavior = NidOAuthBehavior.WEBVIEW
            NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback {
                override fun onSuccess() {
                    updateView()
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Toast.makeText(
                        context,
                        "errorCode:$errorCode, errorDesc:$errorDescription",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }

            })
        }

        // 재동의 로그인 Launcher
        binding.reagreeLoginLauncher.setOnClickListener {
            NaverIdLoginSDK.behavior = NidOAuthBehavior.DEFAULT
            NaverIdLoginSDK.reagreeAuthenticate(context, launcher)
        }

        // 재동의 로그인 Callback
        binding.reagreeLoginCallback.setOnClickListener {
            NaverIdLoginSDK.behavior = NidOAuthBehavior.DEFAULT
            NaverIdLoginSDK.reagreeAuthenticate(context, object : OAuthLoginCallback {
                override fun onSuccess() {
                    updateView()
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Toast.makeText(
                        context,
                        "errorCode:$errorCode, errorDesc:$errorDescription",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }

            })
        }

        // ClientSpinner

        val oauthClientSpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.client_list,
            android.R.layout.simple_spinner_item
        )
        oauthClientSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.consumerListSpinner.prompt = "샘플에서 이용할 client 를 선택하세요"
        binding.consumerListSpinner.adapter = oauthClientSpinnerAdapter
        binding.consumerListSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parents: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                Toast.makeText(this@MainActivity,
                    oauthClientSpinnerAdapter.getItem(pos).toString() + "가 선택됨",
                    Toast.LENGTH_SHORT
                ).show()
                if (oauthClientSpinnerAdapter.getItem(pos) == "네이버아이디로로그인") {
                    clientId = "jyvqXeaVOVmV"
                    clientSecret = "527300A0_COq1_XV33cf"
                    clientName = "네이버 아이디로 로그인"
                } else if (oauthClientSpinnerAdapter.getItem(pos) == "소셜게임(12G)") {
                    clientId = "5875kZ1sZ_aL"
                    clientSecret = "509C949A_yi7jOzKU4Pg"
                    clientName = "소셜게임"
                } else if (oauthClientSpinnerAdapter.getItem(pos) == "ERROR_NO_NAME") {
                    clientId= "5875kZ1sZ_aL"
                    clientSecret = "509C949A_yi7jOzKU4Pg"
                    clientName = ""
                } else if (oauthClientSpinnerAdapter.getItem(pos) == "ERROR_CLIENT_ID") {
                    clientId = "5875kZ1sZ_a"
                    clientSecret = "509C949A_yi7jOzKU4Pg"
                    clientName = "ERROR_CLIENT_ID"
                } else if (oauthClientSpinnerAdapter.getItem(pos) == "ERROR_CLIENT_SECRET") {
                    clientId = "jyvqXeaVOVmV"
                    clientSecret = "509C949Ayi7jOzKU4Pg"
                    clientName = "ERROR_CLIENT_SECRET"
                } else {
                    return
                }
                updateUserData()
                NaverIdLoginSDK.initialize(context, clientId, clientSecret, clientName)
            }

            override fun onNothingSelected(parents: AdapterView<*>?) {
                // do nothing
            }

        }

        // Client 정보 변경
        binding.buttonOAuthInit.setOnClickListener {
            clientId = binding.oauthClientid.text.toString()
            clientSecret = binding.oauthClientsecret.text.toString()
            clientName = binding.oauthClientname.text.toString()

            NaverIdLoginSDK.initialize(context, clientId, clientSecret, clientName)

            updateUserData()

        }

        updateUserData()
    }

    private fun updateView() {
        binding.tvAccessToken.text = NaverIdLoginSDK.getAccessToken()
        binding.tvRefreshToken.text = NaverIdLoginSDK.getRefreshToken()
        binding.tvExpires.text = NaverIdLoginSDK.getExpiresAt().toString()
        binding.tvType.text = NaverIdLoginSDK.getTokenType()
        binding.tvState.text = NaverIdLoginSDK.getState().toString()
    }

    private fun updateUserData() {
        binding.oauthClientid.setText(clientId)
        binding.oauthClientsecret.setText(clientSecret)
        binding.oauthClientname.setText(clientName)
    }

}