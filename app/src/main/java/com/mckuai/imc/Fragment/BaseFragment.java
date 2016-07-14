package com.mckuai.imc.Fragment;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.mckuai.imc.Base.BaseActivity;
import com.umeng.analytics.MobclickAgent;


public class BaseFragment extends Fragment {
    protected int mTitleResId;
    protected OnFragmentEventListener mOnFragmentEventListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (this instanceof CreateCartoonFragment) {
//            mTitleResId = R.string.fragment_cartoon;
//        } else if (this instanceof MainFragment_Cartoon) {
//            mTitleResId = R.string.fragment_cartoon;
//        } else if (this instanceof MainFragment_Chat) {
//            mTitleResId = R.string.fragment_chat;
//        } else if (this instanceof MainFragment_Community) {
//            mTitleResId = R.string.fragment_community;
//        } else if (this instanceof RecommendFragment) {
//            mTitleResId = R.string.fragment_mine;
//        } else if (this instanceof ProfileEditerFragment) {
//            mTitleResId = R.string.fragment_profile;
//        } else if (this instanceof ThemeFragment){
//            mTitleResId = R.string.fragment_theme;
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (0 != mTitleResId) {
            MobclickAgent.onPageStart(getResources().getString(mTitleResId));
        } else {
            MobclickAgent.onPageStart(this.getClass().getName());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (0 != mTitleResId) {
            MobclickAgent.onPageEnd(getResources().getString(mTitleResId));
        } else {
            MobclickAgent.onPageEnd(this.getClass().getName());
        }
    }

    public interface OnFragmentEventListener {
        void onFragmentShow(int titleResId);
        void onFragmentAttach(int titleResId);
        void onFragmentAction(Object object);
    }

    public void setFragmentEventListener(OnFragmentEventListener l) {
        this.mOnFragmentEventListener = l;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && null != mOnFragmentEventListener) {
            mOnFragmentEventListener.onFragmentShow(mTitleResId);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (null != mOnFragmentEventListener) {
            mOnFragmentEventListener.onFragmentAttach(mTitleResId);
        }
    }

    public String getTitleResId() {
        return mTitleResId + "";
    }

    public String getTitle() {

        if (0 != mTitleResId) {
            return getString(mTitleResId);
        } else {
            return "未知";
        }
    }

    protected void showMessage(String msg, String action, View.OnClickListener listener) {
        if (null != getActivity()) {
            ((BaseActivity) getActivity()).showMessage(msg, action, listener);
        }
    }


    public boolean onBackPressed(){
        return false;
    }
}
