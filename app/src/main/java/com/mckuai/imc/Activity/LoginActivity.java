package com.mckuai.imc.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.MCUser;
import com.mckuai.imc.R;
import com.mckuai.imc.Utils.MCNetEngine;
import com.mckuai.imc.Utils.QQLoginListener;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, QQLoginListener.OnQQResponseListener,MCNetEngine.OnLoginServerResponseListener {


    private final String TAG = "LoginActivity";

    private AppCompatTextView loginMsg;

    private static Tencent mTencent;
    private QQLoginListener mQQListener;
    String title = "登录";
    private boolean isFullLoginNeed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);
//        initToolbar(R.id.toolbar, 0, this);
        mTencent = Tencent.createInstance("101155101", getApplicationContext());
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleResult(false);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(title);
        initView();
    }

    private void initView() {
        mTitle.setText(title);
        findViewById(R.id.login_qqlogin).setOnClickListener(this);
        findViewById(R.id.login_anonymous).setOnClickListener(this);
        loginMsg = (AppCompatTextView) findViewById(R.id.loginmsg);
    }

    private void logoutQQ() {
        if (null != mTencent) {
            mTencent.logout(LoginActivity.this);
        }
    }


    private void loginToQQ() {
        loginMsg.setText(R.string.login_QQ);
        MobclickAgent.onEvent(this, "qqLogin");
        if (null == mTencent) {
            mTencent = Tencent.createInstance("101155101", getApplicationContext());
        } else if (mTencent.isSessionValid()) {
            mTencent.logout(getApplicationContext());
        }
        if (!mTencent.isSessionValid()) {
            mQQListener = new QQLoginListener(this, mTencent, this);
            mTencent.login(this, "all", mQQListener);
        }
    }

    private void loginToMC(MCUser user){
        MobclickAgent.onEvent(this, "login");
        loginMsg.setText(R.string.login_MCServer);
        mApplication.netEngine.loginServer(this, user, this);
    }

    public void loginIM() {
        MobclickAgent.onEvent(this, "loginChatServer");
        loginMsg.setText(R.string.login_RongIM);
        mApplication.loginIM(new MCKuai.IMLoginListener() {
            @Override
            public void onInitError() {
                MobclickAgent.onEvent(LoginActivity.this, "chatLogin_F");
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onTokenIncorrect() {
                MobclickAgent.onEvent(LoginActivity.this, "chatLogin_F");
                mApplication.user.setLoginToken(null);
                handler.sendEmptyMessage(2);
        }

            @Override
            public void onLoginFailure(String msg) {
                MobclickAgent.onEvent(LoginActivity.this, "chatLogin_F");
                handler.sendEmptyMessage(3);
            }

            @Override
            public void onLoginSuccess(String msg) {
                handler.sendEmptyMessage(4);
                MobclickAgent.onEvent(LoginActivity.this, "chatLogin_S");
            }
        });

    }
    private void handleResult(Boolean result) {
        setResult(true == result ? RESULT_OK : RESULT_CANCELED);
        this.finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_qqlogin:
                if (null != mApplication.user && mApplication.user.isUserTokenValid() && !isFullLoginNeed) {
                    loginToMC( mApplication.user);
                } else {
                    loginToQQ();
                }
                break;
            default:
                handleResult(false);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tencent.onActivityResultData(requestCode, resultCode, data, mQQListener);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLoginSuccess(MCUser user) {
        MobclickAgent.onEvent(this, "login_S");
        mApplication.user.clone(user);
        mApplication.saveProfile();
        loginIM();
    }

    @Override
    public void onLoginFailure(String msg) {
        MobclickAgent.onEvent(this, "login_F");
        if (null != mTencent){
            mTencent.logout(this);
            loginMsg.setText(getResources().getString(R.string.mc_Err,msg));
        }
    }

    @Override
    public void onQQLoginSuccess( MCUser user) {
        MobclickAgent.onEvent(this, "qqLogin_S");
        if (null == mApplication.user){
            mApplication.user = user;
        } else {
            mApplication.user.clone(user);
            mApplication.user.setLoginToken(user.getLoginToken());
        }
        loginToMC(user);
    }

    @Override
    public void onQQLoginFaile(String msg) {
        MobclickAgent.onEvent(this, "qqLogin_F");
        if (null != msg) {
            loginMsg.setText(getResources().getString(R.string.qq_err,msg));
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    loginMsg.setText(R.string.login_IM_Err_UnInit);
                    break;
                case 2:
                    loginMsg.setText(getResources().getString(R.string.login_IM_Err,getResources().getString(R.string.im_Err_TokenIncorrect)));
                    showError(R.string.tryReLogin, R.string.reLogin, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mApplication.logout();
                            isFullLoginNeed = true;
//                            onClick(findViewById(R.id.login_qqlogin));
                            loginToQQ();
                        }
                    });
                    break;
                case 3:
                    loginMsg.setText(getResources().getString(R.string.login_IM_Err,msg));
                    break;
                case 4:
                    handleResult(true);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    };
}

