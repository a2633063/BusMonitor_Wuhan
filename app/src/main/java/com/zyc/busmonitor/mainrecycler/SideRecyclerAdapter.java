package com.zyc.busmonitor.mainrecycler;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zyc.busmonitor.R;
import com.zyc.busmonitoritem.BusLine;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class SideRecyclerAdapter extends RecyclerView.Adapter<SideRecyclerAdapter.ViewHolder> {

    private List<BusLine> mData;
    MainRecyclerAdapter adapter;

    public List<BusLine> getDataList() {
        return mData;
    }

    public SideRecyclerAdapter(List<BusLine> mData, MainRecyclerAdapter adapter) {
        this.mData = mData;
        this.adapter = adapter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_side_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        ViewHolder mHolder = holder;
        mHolder.name.setText(mData.get(position).getLineName());

        mHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onClick(holder.getAdapterPosition());
            }
        });

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
            name = itemView.findViewById(R.id.tv_bus);
        }
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}