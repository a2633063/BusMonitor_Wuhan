package com.zyc.buslist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BusList extends LinearLayout {

    RecyclerView recyclerView;
    BusListAdapter adapter;
    private List<BusStation> mDataList = new ArrayList<>();

    public BusList(@NonNull Context context) {
        super(context);
    }

    public BusList(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.view_bus_list, this);

//        mDataList.clear();
//        BusStation b;
//        b = new BusStation("高新六路光谷一路");
//        mDataList.add(b);
//        b = new BusStation("高新六路流芳大道");
//        mDataList.add(b);
//        b = new BusStation("高新六路汪田村");
//        mDataList.add(b);
//        b = new BusStation("高新六路康一路");
//        mDataList.add(b);
//        b = new BusStation("高新六路佛祖岭一路");
//        mDataList.add(b);
//        b = new BusStation("高新六路光谷三路");
//        mDataList.add(b);
//        b = new BusStation("光谷三路高新四路");
//        mDataList.add(b);
//        b = new BusStation("光谷三路大吕村");
//        mDataList.add(b);
//        b = new BusStation("高新二路大吕路");
//        mDataList.add(b);
//        b = new BusStation("高新二路光谷四路");
//        mDataList.add(b);
//        b = new BusStation("高新二路驿山南路");
//        mDataList.add(b);
//        b = new BusStation("高新二路光谷六路");
//        mDataList.add(b);
//        b = new BusStation("高新二路高科园路");
//        b.setPass(1);
//        mDataList.add(b);
//        b = new BusStation("高新二路高科园二路");
//        mDataList.add(b);
//        b = new BusStation("高新二路光谷七路");
//        mDataList.add(b);
//        b = new BusStation("高新二路生物园路");
//        mDataList.add(b);
//        b = new BusStation("光谷八路蔡吴村");
//        b.setArrive(1);
//        mDataList.add(b);
//        b = new BusStation("桥北路教师小区");
//        b.setPass(2);
//        mDataList.add(b);
//        b = new BusStation("桥北路三眼桥");
//        mDataList.add(b);
//        b = new BusStation("豹澥公交停车场");
//        mDataList.add(b);


        // 获取控件
        recyclerView = findViewById(R.id.recyclerView);
        //创建一个layoutManager，这里使用LinearLayoutManager指定为线性，也就可以有ListView这样的效果
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        //完成layoutManager设置
        recyclerView.setLayoutManager(layoutManager);
        //创建IconAdapter的实例同时将iconList传入其构造函数
        adapter = new BusListAdapter(mDataList);
        adapter.setSelected(5);
        //完成adapter设置
        recyclerView.setAdapter(adapter);



        adapter.setOnItemClickListener(new BusListAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position, String data) {
                adapter.setSelected(position);
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, position, adapter.getItem(position).getName());
                }
                adapter.notifyDataSetChanged();
            }
        });


    }


    //region 单击回调事件
    //点击 RecyclerView 某条的监听
    public interface OnItemClickListener {
        /**
         * 当RecyclerView某个被点击的时候回调
         *
         * @param view     点击item的视图
         * @param position 在adapter中的位置
         * @param data     点击得到的数据
         */
        void onItemClick(View view, int position, String data);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    //endregion

    public List<BusStation> getList(){
        return mDataList;
    }
    public void clearArrive() {
        for (int i = 0; i < mDataList.size(); i++) {
            mDataList.get(i).setArrive(0);
        }
    }
    public void clearPass(){
        for (int i = 0; i < mDataList.size(); i++) {
            mDataList.get(i).setPass(0);
        }
    }
    public void clear(){
        mDataList.clear();
    }
    public BusStation getItem(int position) {
        return mDataList.get(position);
    }
    public void addBusStation(BusStation busStation){
        mDataList.add(busStation);
    }
    public void addBusStation(int index,BusStation busStation){
        mDataList.add(index,busStation);
    }

    public void setDataList(@NonNull List<BusStation> mDataList) {
        this.mDataList.clear();
        this.mDataList.addAll(mDataList);
//        this.mDataList = mDataList;
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }
}