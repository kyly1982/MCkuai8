package com.mckuai.imc.Utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.mckuai.imc.Bean.MCUser;
import com.mckuai.imc.Bean.Token;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

/**
 * Created by kyly on 2015/11/30.
 */
public class QQLoginListener implements IUiListener {
    private Context mContext;
    private OnQQResponseListener mListener;
    private Tencent mTencent;
    private MCUser mUser;

    public interface OnQQResponseListener {
        void onQQLoginSuccess(MCUser user);

        void onQQLoginFaile(String msg);
    }

    public QQLoginListener(@Nullable Context context, @Nullable Tencent tencent, @Nullable OnQQResponseListener listener) {
        this.mContext = context;
        this.mTencent = tencent;
        this.mListener = listener;
        mUser = new MCUser();
    }

    @Override
    public void onComplete(Object response) {
        JSONObject object = (JSONObject) response;
        if (null != object && 10 < object.length()) {
            if (initOpenidAndToken(object)) {
                updateUserInfo();
            }
        }
    }


    @Override
    public void onError(UiError uiError) {
        if (null != mListener) {
            mListener.onQQLoginFaile(uiError.errorMessage);
        }
    }

    @Override
    public void onCancel() {
        if (null != mListener) {
            mListener.onQQLoginFaile(null);
        }
    }

    private boolean initOpenidAndToken(JSONObject response) {
        String qqtoken = null;
        long expires = (long)0;
        String openId = null;
        try {
            qqtoken = response.getString(Constants.PARAM_ACCESS_TOKEN);
            expires = response.getLong(Constants.PARAM_EXPIRES_IN);
            openId = response.getString(Constants.PARAM_OPEN_ID);

        } catch (Exception e) {
            return false;
        }
        if (null != qqtoken && 0 != expires && null != openId){
            Token token  = new Token(0);
            token.setBirthday(System.currentTimeMillis() - 600000);
            token.setExpires(expires);
            token.setToken(qqtoken);
            mUser.setName(openId);
            mUser.setLoginToken(token);
            if (!TextUtils.isEmpty(qqtoken) && !TextUtils.isEmpty(expires+"") && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(qqtoken, expires+"");
                mTencent.setOpenId(openId);
            }
            return true;
        }
        return false;
    }

    private void updateUserInfo() {
        if (null != mTencent && mTencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {

                @Override
                public void onError(UiError e) {
                    if (null != mListener) {
                        mListener.onQQLoginFaile(e.errorMessage);
                    }
                }

                @Override
                public void onComplete(final Object response) {
                    JSONObject json = (JSONObject) response;
                    if (json.has("nickname")) {
                        try {
                            Log.e("updateUserInfo", "");
                            mUser.setNike(json.getString("nickname"));
                            mUser.setHeadImg(json.getString("figureurl_2"));// 取空间头像做为头像
                            mUser.setGender(json.getString("gender"));
                            mUser.setAddr(json.getString("city"));
                        } catch (Exception e) {
                            // TODO: handle exception
                            if (null != mListener){
                                mListener.onQQLoginFaile(e.getLocalizedMessage());
                            }
                        }
                        if (null != mListener){
                            mListener.onQQLoginSuccess(mUser);
                        }
                    }
                }

                @Override
                public void onCancel() {
                    if (null != mListener) {
                        mListener.onQQLoginFaile(null);
                    }
                }
            };
            UserInfo mInfo = new UserInfo(mContext, mTencent.getQQToken());
            mInfo.getUserInfo(listener);

        } else {
            if (null != mListener) {
                mListener.onQQLoginFaile("腾讯登录模块异常！");
            }
        }
    }

}
