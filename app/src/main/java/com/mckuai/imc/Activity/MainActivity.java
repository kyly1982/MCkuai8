package com.mckuai.imc.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Bean.User;
import com.mckuai.imc.Fragment.MainFragment_Chat;
import com.mckuai.imc.Fragment.MainFragment_Community;
import com.mckuai.imc.Fragment.MainFragment_Recommend;
import com.mckuai.imc.Fragment.MainFragment_Video;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener,
        RadioGroup.OnCheckedChangeListener{

    private DrawerLayout slidingView;
    private AppCompatImageButton userCover;
    private AppCompatTextView userName;
    private AppCompatTextView userLevel;
    private RadioGroup radioGroup;
    private AppCompatRadioButton nav_recommend,nav_video,nav_chat,nav_community;

    private ImageLoader loader;
    private DisplayImageOptions circleDisplayOption;


    private FragmentManager fragmentManager;
    private ArrayList<Fragment> fragments;
    private int currentFragmentIndex = 0;

    private Menu menu;
    private MenuItem type_new,type_hot;

    private final int LOGIN_USERCENTER = 0;
    private final int LOGIN_SETTING = 1;
    private final int LOGIN_PACKAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        initSlidingMenuView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == radioGroup){
            initView();
            initImageLoader();
            initFragment();
        }

        if (null != fragmentManager && null != fragments) {
            fragmentManager.beginTransaction().show(fragments.get(currentFragmentIndex)).commit();
        }
    }

    /**
     * 设置侧边栏
     */
    public void initSlidingMenuView() {

        //设置侧边栏
        slidingView = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, slidingView, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        slidingView.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view = navigationView.inflateHeaderView(R.layout.nav_header_base);
        userCover = (AppCompatImageButton) view.findViewById(R.id.slh_userCover);
        userName = (AppCompatTextView) view.findViewById(R.id.slh_userName);
        userLevel = (AppCompatTextView) view.findViewById(R.id.slh_userLevel);
        userCover.setOnClickListener(this);

    }


    private void initView(){
        radioGroup = (RadioGroup) findViewById(R.id.nav);
        nav_recommend = (AppCompatRadioButton) findViewById(R.id.nav_recommend);
        nav_video = (AppCompatRadioButton) findViewById(R.id.nav_video);
        nav_chat = (AppCompatRadioButton) findViewById(R.id.nav_chat);
        nav_community = (AppCompatRadioButton) findViewById(R.id.nav_community);

        radioGroup.setOnCheckedChangeListener(this);
    }

    private void initImageLoader(){
        if (null == loader){
            loader = ImageLoader.getInstance();
            circleDisplayOption = mApplication.getCircleOptions();
        }
    }

    private void initFragment(){
        fragments = new ArrayList<>(4);
        fragments.add(new MainFragment_Recommend());
        fragments.add(new MainFragment_Video());
        fragments.add(new MainFragment_Chat());
        fragments.add(new MainFragment_Community());

        RelativeLayout contentView = (RelativeLayout) findViewById(R.id.fragment_content);

        fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (Fragment fragment:fragments){
            transaction.add(contentView.getId(),fragment);
            transaction.hide(fragment);
        }
        transaction.commit();
        currentFragmentIndex = 0;
    }




    @Override
    public void onBackPressed() {
        if (slidingView.isDrawerOpen(GravityCompat.START)) {
            slidingView.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_videocategory, menu);
        this.menu = menu;
        type_hot = menu.findItem(R.id.action_hot);
        type_new = menu.findItem(R.id.action_new);
        return true;
    }

    /**
     * toolbar中的菜单
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        if (nav_video.isChecked()){
            switch (item.getItemId()){
                case R.id.action_new:
                    type_new.setVisible(false);
                    type_hot.setVisible(true);
                    break;
                case R.id.action_hot:
                    type_hot.setVisible(false);
                    type_new.setVisible(true);
                    break;
            }
            ((MainFragment_Video)fragments.get(1)).setVideoType(item.getItemId());
        }
       /* switch (item.getItemId()){
            case R.id.action_hot:
                break;
            case R.id.action_new:
                break;
            case R.id.action_cartoon:
                break;
            case R.id.action_knowledge:
                break;
            case R.id.action_map:
                break;
            case R.id.action_mod:
                break;
            case R.id.action_show:
                break;
            case R.id.action_online:
                break;
        }*/
        return true;
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
        closeSlidingMenu();
        switch (item.getItemId()){
            case R.id.nav_search:
                showSearch();
                break;
            case R.id.nav_package:
                showPackage();
                break;
            case R.id.nav_setting:
                showSetting();
                break;
            case R.id.nav_logout:
                logout();
                break;
            case R.id.nav_share:
                shareApp();
                break;
            case R.id.nav_prise:
                priseApp();
                break;
            case R.id.nav_upgread:
                checkUpgread(false);
                break;
        }
        return true;
    }

    /*
    底部导航栏事件
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (null != fragments && !fragments.isEmpty()) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (0 <= currentFragmentIndex) {
                transaction.hide(fragments.get(currentFragmentIndex));
            }
            switch (checkedId) {
                case R.id.nav_recommend:
                    MobclickAgent.onEvent(this, "clickCartoon");
                    mTitle.setText("推荐");
                    currentFragmentIndex = 0;
                    break;
                case R.id.nav_video:
                    MobclickAgent.onEvent(this, "clickChat");
                    mTitle.setText("视频");
                    currentFragmentIndex = 1;
                    break;
                case R.id.nav_chat:
                    MobclickAgent.onEvent(this, "clickForum");
                    mTitle.setText("聊天");
                    currentFragmentIndex = 2;
                    break;
                case R.id.nav_community:
                    MobclickAgent.onEvent(this, "clickMine");
                    mTitle.setText("社区");
                    currentFragmentIndex = 3;
                    break;
            }
            transaction.show(fragments.get(currentFragmentIndex)).commit();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.slh_userCover:
                closeSlidingMenu();
                showUserCenter();
                break;
        }
    }

    private void refreshUser(){
        if (mApplication.isLogin()){
            loader.displayImage(mApplication.user.getHeadImg(), userCover, circleDisplayOption, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (null != loadedImage) {
                        mToolbar.setNavigationIcon(new BitmapDrawable(loadedImage));
                    }
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            userName.setText(mApplication.user.getNike());
            userLevel.setText("LV "+mApplication.user.getLevel());
        } else {
            userCover.setImageResource(R.mipmap.ic_usercover_default);
            userName.setText("未登录");
            userLevel.setText("点击头像登录");
            mToolbar.setNavigationIcon(R.mipmap.ic_usercover_default);
        }
    }

    private void showSearch(){
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("TYPE",0);
        startActivity(intent);
    }

    private void showPackage(){
        if (mApplication.isLogin()){
            Intent intent = new Intent(this, SearchActivity.class);
            Bundle bundle = new Bundle();

            bundle.putSerializable("PACKAGE",new User(mApplication.user));
            intent.putExtras(bundle);
            intent.putExtra("TYPE",1);
            startActivity(intent);
        } else {
            showMessage("登录后才能使用背包，是否登录？", "登录", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callLogin(LOGIN_PACKAGE);
                }
            });
        }
    }

    private void showSetting(){
        if (mApplication.isLogin()){
            Intent intent = new Intent(this,ProfileEditerActivity.class);
            startActivity(intent);
        } else {
            showMessage("登录后才能设置，是否登录？", "登录", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callLogin(LOGIN_SETTING);
                }
            });
        }
    }

    private void showUserCenter(){
        if (mApplication.isLogin()){
            Intent intent = new Intent(this,UserCenterActivity.class);
            startActivity(intent);
        } else {
            callLogin(LOGIN_USERCENTER);
        }
    }

    private void shareApp(){
        share("变大神，用麦块，你也可以",
                "自从用了麦块我的世界盒子，游戏玩的特别6，妈妈再也不用担心我变不成大神了,666",
                "http://www.mckuai.com/down.html",
                new UMImage(this,R.mipmap.ic_share_default));
    }

    private void priseApp(){
        try
        {
            Uri uri = Uri.parse("market://details?id=com.tars.mckuai");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e)
        {
            // TODO: handle exception
            showMessage("你还没安装有应用市场，安装后才能评分！",null,null);
        }
    }

    private void checkUpgread(boolean isSlience){

    }

    private void logout(){
        mApplication.logout();
        refreshUser();
    }

    private void closeSlidingMenu(){
        if (null != slidingView){
            slidingView.closeDrawer(GravityCompat.START);
        }
    }

}
