package com.navercorp.nid.oauth.legacy;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.navercorp.nid.log.NidLog;

public class OAuthLoginPreferenceManager {

    private static final String TAG = "OAuthLoginPreferenceManager";

    private final static String OAUTH_LOGIN_PREF_NAME_PER_APP  = "NaverOAuthLoginPreferenceData";

    private static Context mContext = null;
    private static SharedPreferences mPref = null;


    public OAuthLoginPreferenceManager(Context _context) {
        mContext = _context;

        if (mContext != null) {
            if (mPref == null) {
                mPref = mContext.getSharedPreferences(OAUTH_LOGIN_PREF_NAME_PER_APP, Context.MODE_PRIVATE);
            }

        } else {
            NidLog.e(TAG, "context is null!");
        }
    }

    public void setAccessToken(String aToken) {
        PREF_KEY.ACCESS_TOKEN.set(aToken);
    }

    public String getAccessToken() {
        String token = (String) PREF_KEY.ACCESS_TOKEN.get();

        if (TextUtils.isEmpty(token)) {
            return null;
        }

        // expires time 검증 후 return 해줌
        if (System.currentTimeMillis() / 1000 - getExpiresAt() < 0)
            return token;

        return null;
    }

    public void setRefreshToken(String rToken) {
        PREF_KEY.REFRESH_TOKEN.set(rToken);
    }

    public String getRefreshToken() {
        return (String) PREF_KEY.REFRESH_TOKEN.get();
    }

    public void setExpiresAt(long expiresTimeStamp) {
        PREF_KEY.EXPIRES_AT.set(expiresTimeStamp);
    }

    public long getExpiresAt() {
        Long expires = (Long) PREF_KEY.EXPIRES_AT.get();
        if (expires == null)
            return 0;

        return expires;
    }

    public void setClientId(String clientId) {
        PREF_KEY.CLIENT_ID.set(clientId);
    }

    public String getClientId() {
        return (String) PREF_KEY.CLIENT_ID.get();
    }


    public void setClientSecret(String clientSecret) {
        PREF_KEY.CLIENT_SECRET.set(clientSecret);
    }

    public String getClientSecret() {
        return (String) PREF_KEY.CLIENT_SECRET.get();
    }

    public void setClientName(String clientName) {
        PREF_KEY.CLIENT_NAME.set(clientName);
    }

    public String getClientName() {
        return (String) PREF_KEY.CLIENT_NAME.get();
    }


    public void setCallbackUrl(String callbackUrl) {
        PREF_KEY.CALLBACK_URL.set(callbackUrl);
    }

    public String getCallbackUrl() {
        return (String) PREF_KEY.CALLBACK_URL.get();
    }


    public void setTokenType(String tokenType) {
        PREF_KEY.TOKEN_TYPE.set(tokenType);
    }

    public String getTokenType() {
        return (String) PREF_KEY.TOKEN_TYPE.get();
    }


    public void setLastErrorCode(OAuthErrorCode errorCode) {
        PREF_KEY.LAST_ERROR_CODE.set(errorCode.getCode());
    }

    public OAuthErrorCode getLastErrorCode() {
        String code = (String) PREF_KEY.LAST_ERROR_CODE.get();
        return OAuthErrorCode.fromString(code);
    }

    public void setLastErrorDesc(String errorDesc) {
        PREF_KEY.LAST_ERROR_DESC.set(errorDesc);
    }

    public String getLastErrorDesc() {
        return (String) PREF_KEY.LAST_ERROR_DESC.get();
    }


    @SuppressWarnings("rawtypes")
    protected enum PREF_KEY {
        ACCESS_TOKEN			("ACCESS_TOKEN"       , String.class),
        REFRESH_TOKEN			("REFRESH_TOKEN"      , String.class),
        EXPIRES_AT				("EXPIRES_AT"         , long.class),
        TOKEN_TYPE				("TOKEN_TYPE"         , String.class),
        CLIENT_ID				("CLIENT_ID"       	  , String.class),
        CLIENT_SECRET			("CLIENT_SECRET"      , String.class),
        CLIENT_NAME				("CLIENT_NAME"        , String.class),
        CALLBACK_URL			("CALLBACK_URL"       , String.class),
        LAST_ERROR_CODE			("LAST_ERROR_CODE"    , String.class),
        LAST_ERROR_DESC			("LAST_ERROR_DESC"    , String.class);


        private String key;
        private String type;

        private PREF_KEY(String key, Class type) {
            this.key = key;
            this.type = type.getCanonicalName();
        }


        public String getValue() {
            return key;
        }

        public boolean set(Object data) {
            SharedPreferences pref = mPref ;

            boolean res = false;
            int cnt = 0;
            // preference 기록이 실패하는 경우 3회까지 재실행함
            while (res == false && cnt < 3) {
                if (cnt > 0) {
                    NidLog.e(TAG, "preference set() fail (cnt:" + cnt + ")");
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        NidLog.e(TAG, e);
                    }
                }
                res = setSub(pref, data);
                cnt ++;
            }

            return res;
        }

        private boolean setSub(SharedPreferences pref, Object data) {

            if (pref == null)		return false;

            SharedPreferences.Editor editor = pref.edit();
            if (editor == null)		return false;


            try {
                if (type.equals(int.class.getCanonicalName())) {
                    editor.putInt(key, (Integer) data);

                } else if (type.equals(long.class.getCanonicalName())) {
                    editor.putLong(key, (Long) data);

                } else if (type.equals(String.class.getCanonicalName())) {
                    editor.putString(key, (String) data);

                } else if (type.equals(boolean.class.getCanonicalName())) {
                    editor.putBoolean(key, (Boolean) data);

                }

                return editor.commit();
            } catch (Exception e) {
                NidLog.e(TAG, "Prefernce Set() fail, key:" + key + ", type:" + type + "e:" + e.getMessage());
            }
            return false;
        }

        public boolean del() {

            return delSub(mPref);
        }

        private boolean delSub(SharedPreferences pref) {
            if (pref == null)		return false;
            SharedPreferences.Editor editor = pref.edit();
            if (editor == null)		return false;
            try {
                editor.remove(type);
                return editor.commit();
            } catch (Exception e) {
                NidLog.e(TAG, "Prefernce del() fail, key:" + key + ", type:" + type + "e:" + e.getMessage());
            }
            return false;
        }

        public Object get() {
            try {
                return getSub(mPref);
            } catch (Exception e) {
                NidLog.e(TAG, "get() fail, e:" + e.getMessage());
            }
            return null;
        }

        private Object getSub(SharedPreferences pref) {

            Object data = null;

            try {
                if (type.equals(int.class.getCanonicalName())) {
                    data = pref.getInt(key, 0);

                } else if (type.equals(long.class.getCanonicalName())) {
                    data = pref.getLong(key, 0);

                } else if (type.equals(String.class.getCanonicalName())) {
                    data = pref.getString(key, "");

                } else if (type.equals(boolean.class.getCanonicalName())) {
                    data = pref.getBoolean(key, true);

                }
            } catch (Exception e) {
                NidLog.e(TAG, "get(), key:" + key + ", pref:" + ( (pref == null) ? "null" : "ok"));
            }

            return data;
        }
    }





}
