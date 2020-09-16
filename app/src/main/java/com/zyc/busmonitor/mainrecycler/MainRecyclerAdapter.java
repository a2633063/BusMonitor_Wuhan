package com.zyc.busmonitor.mainrecycler;


import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zyc.busmonitor.R;
import com.zyc.busmonitoritem.BusLine;
import com.zyc.busmonitoritem.BusMonitorItem;

import java.util.List;


public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {

    private List<BusLine> mData;

    public List<BusLine> getDataList() {
        return mData;
    }

    public MainRecyclerAdapter(List<BusLine> mData) {
        this.mData = mData;
    }
    
    public BusLine get(int position){
        return mData.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_main_recycler, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ViewHolder mHolder = holder;
        mHolder.BusItem.setBus(mData.get(position));

//        mHolder.itemView.setBackgroundColor(0);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        BusMonitorItem BusItem;
        public ViewHolder(View itemView) {
            super(itemView);
            BusItem =  itemView.findViewById(R.id.BusMonitorItem);
        }
    }

    public void onItemDissmiss(int position) {
        //移除数据
        mData.remove(position);
        notifyItemRemoved(position);
    }

}