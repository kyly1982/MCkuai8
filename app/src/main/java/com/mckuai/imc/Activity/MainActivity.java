package com.mckuai.imc.Activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.mckuai.imc.R;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    protected Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
    }


    /**
     * 设置内容视图，将指定的内容视图添加到视图中
     * 此方法会同时设置好toolbar和侧边栏
     * @param contentViewLayoutResID 要添加的视图
     */
    @Override
    public void setContentView(@LayoutRes int contentViewLayoutResID) {
        super.setContentView(R.layout.activity_base_withsildingmenu);
        //添加内容视图
        FrameLayout mContentRoot =  (FrameLayout)findViewById(R.id.content);
        if (null != mContentRoot && (0 < contentViewLayoutResID)){
            View contentView = LayoutInflater.from(this).inflate(contentViewLayoutResID,mContentRoot,false);
            if (null != contentView){
                mContentRoot.addView(contentView);
            }
        }

        //设置toolbar
        setToolbar(R.id.toolbar);

        //设置侧边栏
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 侧边栏点击事件
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()){
            case R.id.nav_search:
                break;
            case R.id.nav_package:
                break;
            case R.id.nav_setting:
                break;
            case R.id.nav_logout:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_prise:
                break;
            case R.id.nav_upgread:
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
