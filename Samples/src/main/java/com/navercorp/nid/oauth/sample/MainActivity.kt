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
import com.navercorp.nid.NidOAuth
import com.navercorp.nid.oauth.domain.enum.LoginBehavior
import com.navercorp.nid.oauth.sample.databinding.ActivityMainBinding
import com.navercorp.nid.oauth.util.NidOAuthCallback
import com.navercorp.nid.profile.domain.vo.NidProfile
import com.navercorp.nid.profile.domain.vo.NidProfileMap
import com.navercorp.nid.profile.util.NidProfileCallback

class MainActivity : AppCompatActivity() {

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
                val errorCode = NidOAuth.getLastErrorCode().code
                val errorDescription = NidOAuth.getLastErrorDescription()
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
        NidOAuth.apply {
            setLogEnabled(true)
            initialize(context, clientId, clientSecret, clientName)
            isShowMarketLink = true
            isShowBottomTab = true
        }

        // oauth 로그인 콜백 선언
        val nidOAuthCallback = object : NidOAuthCallback {
            override fun onSuccess() {
                updateView()
            }

            override fun onFailure(errorCode: String, errorDesc: String) {
                Toast.makeText(
                    context,
                    "errorCode:$errorCode, errorDesc:$errorDesc",
                    Toast.LENGTH_SHORT
                ).show()
                updateView()
            }
        }

        binding.buttonOAuthLoginImg.setOAuthLogin(nidOAuthCallback)

        // 로그인 Launcher
        binding.loginLauncher.setOnClickListener {
            NidOAuth.behavior = LoginBehavior.DEFAULT
            NidOAuth.requestLogin(context, launcher)
        }

        // 로그인 Callback
        binding.loginCallback.setOnClickListener {
            NidOAuth.behavior = LoginBehavior.DEFAULT
            NidOAuth.requestLogin(
                context = this,
                callback = nidOAuthCallback,
            )
        }

        // 로그아웃
        binding.logout.setOnClickListener {
            NidOAuth.logout(nidOAuthCallback)
        }

        // 연동 끊기
        binding.deleteToken.setOnClickListener {
            NidOAuth.disconnect(nidOAuthCallback)
        }

        // 토큰 갱신
        binding.refreshToken.setOnClickListener {
            NidOAuth.requestLogin(
                context = this,
                callback = nidOAuthCallback,
            )
        }

        // Api 호출
        binding.profileApi.setOnClickListener {
            NidOAuth.getUserProfile(object : NidProfileCallback<NidProfile> {
                override fun onSuccess(result: NidProfile) {
                    Toast.makeText(
                        context,
                        "$result",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.tvApiResult.text = result.toString()
                }

                override fun onFailure(errorCode: String, errorDesc: String) {
                    Toast.makeText(
                        context,
                        "errorCode:$errorCode, errorDesc:$errorDesc",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.tvApiResult.text = ""
                }
            })
        }

        // 프로필 Map 호출
        binding.profileMapApi.setOnClickListener {
            NidOAuth.getUserProfileMap(object : NidProfileCallback<NidProfileMap> {
                override fun onSuccess(result: NidProfileMap) {
                    Toast.makeText(
                        context,
                        "$result",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.tvApiResult.text = result.toString()
                }

                override fun onFailure(errorCode: String, errorDesc: String) {
                    Toast.makeText(
                        context,
                        "errorCode:$errorCode, errorDesc:$errorDesc",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.tvApiResult.text = ""
                }
            })
        }

        // 네이버앱 로그인 (Callback)
        binding.loginWithNaverapp.setOnClickListener {
            NidOAuth.behavior = LoginBehavior.NAVERAPP
            NidOAuth.requestLogin(
                context = this,
                callback = nidOAuthCallback,
            )
        }

        // 커스텀탭 로그인 Callback
        binding.loginWithCustomtabs.setOnClickListener {
            NidOAuth.behavior = LoginBehavior.CUSTOMTABS
            NidOAuth.requestLogin(
                context = this,
                callback = nidOAuthCallback,
            )
        }

        // 재동의 로그인 Launcher
        binding.reagreeLoginLauncher.setOnClickListener {
            NidOAuth.behavior = LoginBehavior.DEFAULT
            NidOAuth.repromptPermissions(context, launcher)
        }

        // 재동의 로그인 Callback
        binding.reagreeLoginCallback.setOnClickListener {
            NidOAuth.behavior = LoginBehavior.DEFAULT
            NidOAuth.repromptPermissions(
                context = this,
                callback = nidOAuthCallback,
            )
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
                NidOAuth.initialize(context, clientId, clientSecret, clientName)
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

            NidOAuth.initialize(context, clientId, clientSecret, clientName)

            updateUserData()

        }

        updateUserData()
    }

    private fun updateView() {
        binding.tvAccessToken.text = NidOAuth.getAccessToken()
        binding.tvRefreshToken.text = NidOAuth.getRefreshToken()
        binding.tvExpires.text = NidOAuth.getExpiresAt().toString()
        binding.tvType.text = NidOAuth.getTokenType()
        binding.tvState.text = NidOAuth.getState().toString()
    }

    private fun updateUserData() {
        binding.oauthClientid.setText(clientId)
        binding.oauthClientsecret.setText(clientSecret)
        binding.oauthClientname.setText(clientName)
    }

}