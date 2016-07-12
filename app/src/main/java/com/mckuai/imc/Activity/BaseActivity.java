package com.mckuai.imc.Activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;

/**
 * Created by kyly on 2016/7/12.
 */
public class BaseActivity extends AppCompatActivity {
    protected AppCompatTextView mTitle;
    protected Toolbar mToolbar;

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

    public void setToolbar(@IdRes int toolbarId){
        if (0 < toolbarId){
            mToolbar = (Toolbar) findViewById(toolbarId);
            mToolbar.setTitle("");
            setSupportActionBar(mToolbar);
            String classname = getLocalClassName();
            if (!classname.equals("Activity.MainActivity")){
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            }
        }
    }




}
