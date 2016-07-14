package com.mckuai.imc.Base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.mckuai.imc.R;

/**
 * Created by kyly on 2016/7/12.
 */
public class BaseActivity extends AppCompatActivity {
    protected AppCompatTextView mTitle;
    protected Toolbar mToolbar;
    protected MCKuai mApplication = MCKuai.instence;
    private FrameLayout mContentRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setContentView(@LayoutRes int contentViewLayoutResID) {
        if (getLocalClassName().equals("Activity.MainActivity")){
            super.setContentView(R.layout.activity_with_sildingmenu);
        } else {
            super.setContentView(R.layout.activity_with_toolbar);
        }
        //添加内容视图
        mContentRootView =  (FrameLayout)findViewById(R.id.content);
        if (null != mContentRootView && (0 < contentViewLayoutResID)){
            View contentView = LayoutInflater.from(this).inflate(contentViewLayoutResID,mContentRootView,false);
            if (null != contentView){
                mContentRootView.addView(contentView);
            }
        }
        //设置toolbar
        initToolbar();
    }

    protected void initToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        String classname = getLocalClassName();
        if (!classname.equals("Activity.MainActivity")){
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mTitle = (AppCompatTextView) findViewById(R.id.toolbar_title);
    }




    public void showMessage(@StringRes int msgResId, @StringRes int actionResid, View. OnClickListener actionClicklistener){
        showMessage(0,msgResId,actionResid,actionClicklistener);
    }

    public void showWarning(@StringRes int msgResId, @StringRes int actionResid, View.OnClickListener actionClicklistener){
        showMessage(1,msgResId,actionResid,actionClicklistener);
    }

    public void showError(@StringRes int errorResId, @StringRes int actionResId, View.OnClickListener actionClicklistener){
        showMessage(2,errorResId,actionResId,actionClicklistener);
    }

    private void showMessage(int type, @StringRes int msgResId, @StringRes int actionResid, View.OnClickListener actionClicklistener){
        Snackbar snackbar = Snackbar.make(mContentRootView,msgResId,0 == type ? Snackbar.LENGTH_SHORT:Snackbar.LENGTH_LONG);
        if (0 < actionResid && null != actionClicklistener) {
            snackbar.setAction(actionResid,actionClicklistener);
            switch (type) {
                case 0:
                    snackbar.setActionTextColor(getResources().getColor(R.color.msg_normal));
                    break;
                case 1:
                    snackbar.setActionTextColor(getResources().getColor(R.color.msg_warning));
                    break;
                case 2:
                    snackbar.setActionTextColor(getResources().getColor(R.color.msg_error));
                    break;
            }
        }
        snackbar.show();
    }





}
