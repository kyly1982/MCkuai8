package com.mckuai.imc.Activity;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.mckuai.imc.Base.BaseActivity;
import com.mckuai.imc.Bean.MCUser;
import com.mckuai.imc.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileEditerActivity extends BaseActivity
        implements View.OnClickListener,
        TextView.OnEditorActionListener,
        View.OnFocusChangeListener
{

    private AppCompatImageView userCover;
    private TextInputLayout nickWrapper, addressWrapper;
    private TextInputEditText nickEdit;
    private AppCompatAutoCompleteTextView addressEdit;
    private View progress;

    private ImageLoader loader;
    private DisplayImageOptions circleOption;
    private MCUser user;

    private LocationClient locationClient;

    private boolean isUserChange = true;//用于防止设置昵称和城市时触发事件响应
    private int status;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editer);

        mToolbar.setNavigationOnClickListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitle.setText("设置");
        loader = ImageLoader.getInstance();
        circleOption = mApplication.getCircleOptions();
        user = mApplication.user;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == userCover){
            initView();
        }
        showData();
        location();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                upload();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        userCover = (AppCompatImageView) findViewById(R.id.usercover);
        nickWrapper = (TextInputLayout) findViewById(R.id.usernick_wrapper);
        addressWrapper = (TextInputLayout) findViewById(R.id.useraddress_wrapper);
        nickEdit = (TextInputEditText) findViewById(R.id.usernick);
        addressEdit = (AppCompatAutoCompleteTextView) findViewById(R.id.useraddress);
        progress = findViewById(R.id.uploadview);

        userCover.setFocusable(true);
        userCover.setFocusableInTouchMode(true);
        addressEdit.setFocusable(true);
        addressEdit.setFocusableInTouchMode(true);

        nickEdit.setOnEditorActionListener(this);
        addressEdit.setOnEditorActionListener(this);
        nickEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isUserChange && 0 == s.length()){
                    nickEdit.setError("昵称不能为空");
                }
            }
        });
        addressEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isUserChange && 0 == s.length()){
                    addressEdit.setError("城市不能为空");
                }
            }
        });
        nickEdit.setOnFocusChangeListener(this);
        addressEdit.setOnFocusChangeListener(this);



            String[] citys = getResources().getStringArray(R.array.citylist);
            ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,citys);
            addressEdit.setAdapter(cityAdapter);
    }

    private void showData(){
        String url = (String) userCover.getTag();
        if (null == url || url.isEmpty() || !url.equals(user.getHeadImg())){
            loader.displayImage(user.getHeadImg(),userCover,circleOption);
            userCover.setTag(user.getHeadImg());
            isUserChange = false;
            nickEdit.setText(user.getNike());
            addressEdit.setText(user.getAddr());
            isUserChange = true;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus){
            if (v.getId() == addressEdit.getId()){
                if (addressEdit.getText().toString().equals(user.getAddr())){
                    addressEdit.setTextColor(getResources().getColor(R.color.textColorPrimary));
                } else {
                    addressEdit.setTextColor(getResources().getColor(R.color.textGreen));
                }
            } else if (v.getId() == nickEdit.getId()){
                if (nickEdit.getText().toString().equals(user.getNike())){
                    nickEdit.setTextColor(getResources().getColor(R.color.textColorPrimary));
                } else {
                    nickEdit.setTextColor(getResources().getColor(R.color.textGreen));
                }
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            addressEdit.requestFocus();
        } else if (actionId == EditorInfo.IME_ACTION_NONE) {
            hideKeyboard();
        }
        return false;
    }



    @Override
    public void onClick(View v) {
        if (0 < status){
            showMessage("当前内容还未保存，是否退出？", "退出", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

    }

    private void location(){
        if (null == locationClient){
            locationClient = new LocationClient(getApplicationContext());

            initLocationOption();
            locationClient.registerLocationListener(new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    int result = bdLocation.getLocType();
                    if (result == BDLocation.TypeGpsLocation ||
                            result == BDLocation.TypeNetWorkLocation ||
                            result == BDLocation.TypeOffLineLocation)
                    {
                        if (!addressEdit.getText().toString().equals(bdLocation.getCity())) {
                            isUserChange = false;
                            addressEdit.setText(bdLocation.getCity());
                            addressEdit.setTextColor(getResources().getColor(R.color.textGreen));
                            isUserChange = true;
                        }
                    }
                }
            });
            locationClient.start();
        }
        locationClient.requestLocation();
    }

    private void initLocationOption(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//高精度定位
        option.setCoorType("bd0911");
        option.setScanSpan(0);//仅定位一次
        option.setIsNeedAddress(true);//需要地址信息
        //option.setIsNeedLocationDescribe(true);//设置需要位置语义化结果
        option.setIgnoreKillProcess(true);//设置在stop的时候杀死这个进程
        locationClient.setLocOption(option);
    }

    private void showProgress() {
        if (null != progress) {
            progress.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        if (null != progress) {
            progress.setVisibility(View.GONE);
        }
    }

    private void hideKeyboard() {
            showMessage("隐藏软键盘",null,null);
    }

    private void upload() {

    }

    private void uploadNick() {

    }

    private void uploadCover() {

    }

    private void uploadLocation() {

    }
}
