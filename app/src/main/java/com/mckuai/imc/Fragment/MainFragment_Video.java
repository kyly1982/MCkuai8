package com.mckuai.imc.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.mckuai.imc.Activity.PostActivity;
import com.mckuai.imc.Adapter.VideoAdapter;
import com.mckuai.imc.Bean.Page;
import com.mckuai.imc.Bean.Post;
import com.mckuai.imc.Bean.VideoBean;
import com.mckuai.imc.R;
import com.mckuai.imc.Utils.MCNetEngine;

import java.util.ArrayList;

/**
 * Created by kyly on 2016/7/20.
 */
public class MainFragment_Video extends BaseFragment implements RadioGroup.OnCheckedChangeListener,
        MCNetEngine.OnLoadVideoListener,VideoAdapter.OnItemClickListener,SwipeRefreshLayout.OnRefreshListener,OnMoreListener{
    private View view;
    private RadioGroup radioGroup;
    private AppCompatRadioButton type_new,type_hot;
    private SuperRecyclerView listView;
    private RecyclerView.LayoutManager layoutManager;

    private Page page;
    private VideoAdapter adapter;
    private ArrayList<Post> posts;

    private String[] orderType = {"new","hot"};
    private int orderTypeIndex = 0;
    public  String videoType = "动画";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == view){
            view = inflater.inflate(R.layout.fragment_video,container,false);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != view && null == radioGroup) {
            initView();
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && null != view){
            showData(false);
        }
    }



    private void initView(){
        radioGroup = (RadioGroup) view.findViewById(R.id.video_type);
        type_hot = (AppCompatRadioButton) view.findViewById(R.id.type_hot);
        type_new = (AppCompatRadioButton) view.findViewById(R.id.type_new);
        listView = (SuperRecyclerView) view.findViewById(R.id.video_list);

        layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        listView.setLayoutManager(layoutManager);
        listView.setLoadingMore(true);
        listView.setRefreshListener(this);
        listView.setupMoreListener(this,1);

        radioGroup.setOnCheckedChangeListener(this);
    }



    public void setVideoType(String type){
        videoType = type;
        if (null != page){
            page.setPage(0);
        }
        loadData();
    }

    private void showData(boolean isRefresh){
        if (null == page || 0 == page.getPage()){
            loadData();
        } else {
            if (null == adapter) {
                adapter = new VideoAdapter(getActivity(), this);
                adapter.setDisplayOperations(mApplication.getCircleOptions(), mApplication.getNormalOptions());
                listView.setAdapter(adapter);
            }
            adapter.setData(posts,isRefresh);
        }
    }

    private void loadData(){
        if (null == page || 0 == page.getPage() || page.getPage() < page.getAllCount()) {
            mApplication.netEngine.loadVideoList(getActivity(),
                    videoType,
                    orderType[orderTypeIndex],
                    null == page ? 1 : page.getNextPage(),
                    this);
        } else {
            showMessage("没有更多了！", null,null);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.type_hot:
                orderTypeIndex = 1;
                break;
            case R.id.type_new:
                orderTypeIndex = 0;
                break;
        }
        onRefresh();
    }

    @Override
    public void onRefresh() {
        if (null != page){
            page.setPage(0);
        }
        loadData();
    }

    @Override
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {

    }

    @Override
    public void onLoadVideoFailure(String msg) {
        listView.hideProgress();
        listView.hideMoreProgress();
        showMessage(msg, null,null);
    }

    @Override
    public void onLoadVideoSuccess(VideoBean video) {
        this.page = video.getPageInfo();
        posts = video.getData();
        if (1 == page.getPage()){
            showData(true);//刷新
        } else {
            showData(false);//添加
        }
    }

    @Override
    public void onItemClicked(int position, Post item) {
        Intent intent = new Intent(getActivity(), PostActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(getString(R.string.tag_post),item);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onItemUserClicked(int userId) {

    }
}
