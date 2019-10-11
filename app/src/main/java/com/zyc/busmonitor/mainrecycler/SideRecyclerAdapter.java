package com.zyc.busmonitor.mainrecycler;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zyc.busmonitor.R;
import com.zyc.busmonitoritem.BusLine;

import java.util.List;


public class SideRecyclerAdapter extends RecyclerView.Adapter<SideRecyclerAdapter.ViewHolder> {

    private List<BusLine> mData;
    MainRecyclerAdapter adapter;

    public List<BusLine> getDataList() {
        return mData;
    }

    public SideRecyclerAdapter(List<BusLine> mData,MainRecyclerAdapter adapter) {
        this.mData = mData;
        this.adapter = adapter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_side_recycler, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ViewHolder mHolder = holder;
        mHolder.name.setText(mData.get(position).getLineName());
//        mHolder.itemView.setBackgroundColor(0);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        public ViewHolder(View itemView) {
            super(itemView);
            name =  itemView.findViewById(R.id.tv_bus);
        }
    }

    public void onItemDissmiss(int position) {
        //移除数据
        mData.remove(position);
        notifyItemRemoved(position);
        adapter.notifyItemRemoved(position);
    }

}