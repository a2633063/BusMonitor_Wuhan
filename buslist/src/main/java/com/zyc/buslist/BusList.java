package com.zyc.buslist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BusList extends LinearLayout {
    public final static String Tag = "BusList";

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


        // 获取控件
        recyclerView = findViewById(R.id.recyclerView);
        //创建一个layoutManager，这里使用LinearLayoutManager指定为线性，也就可以有ListView这样的效果
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        //完成layoutManager设置
        recyclerView.setLayoutManager(layoutManager);
        //创建IconAdapter的实例同时将iconList传入其构造函数
        adapter = new BusListAdapter(mDataList);
        adapter.setSelected(12);
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

    public List<BusStation> getList() {
        return mDataList;
    }

    public int getCount() {
        return mDataList.size();
    }

    public void setSelected(int p) {
        adapter.setSelected(p);

    }

    public int getSelected() {
        return adapter.getSelected();
    }

    public void clearArrive() {
        for (int i = 0; i < mDataList.size(); i++) {
            mDataList.get(i).setArrive(0);
        }
    }

    public void clearPass() {
        for (int i = 0; i < mDataList.size(); i++) {
            mDataList.get(i).setPass(0);
        }
    }

    public void clear() {
        mDataList.clear();
    }

    public BusStation getItem(int position) {
        return mDataList.get(position);
    }

    public void addBusStation(BusStation busStation) {
        mDataList.add(busStation);
    }

    public void addBusStation(int index, BusStation busStation) {
        mDataList.add(index, busStation);
    }

    public void setDataList(@NonNull List<BusStation> mDataList) {
        this.mDataList.clear();
        this.mDataList.addAll(mDataList);
//        this.mDataList = mDataList;
    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public void setOpposite() {
        setSelected(getCount()-getSelected()-1);
//        int movingDistance=   recyclerView.computeHorizontalScrollOffset();//当前移动距离
//        int maxDistance=   recyclerView.computeHorizontalScrollRange();//总距离
//        int recycleViewWidth=   recyclerView.computeHorizontalScrollExtent();//控件宽度
//        Log.d(Tag,"当前移动距离:"+movingDistance);
//        Log.d(Tag,"总距离:"+maxDistance);
//        Log.d(Tag,"控件宽度:"+recycleViewWidth);//

//        recyclerView.scrollToPosition(0);
        recyclerView.scrollToPosition(getSelected());
//        View selectedItem = recyclerView.getChildAt(getSelected());
//        int firstItemBottom =selectedItem.getLeft();
//
////        RecyclerView.LayoutManager layoutManager =  rec-dLeft(recyclerView.getChildAt(getSelected()));
//        Log.d(Tag,"左侧距离:"+firstItemBottom);


//        notifyDataSetChanged();
    }
}