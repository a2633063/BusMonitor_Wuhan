package com.zyc.buslist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.ViewHolder> {

    private List<BusStation> mList;
    private int mCurrentSelected = 0;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvArrive;
        TextView tvPass;
        TextView name;
        TextView dot;
        View lineRight;
        View lineleft;

        public ViewHolder(View view) {
            super(view);
            tvArrive = view.findViewById(R.id.tv_arrive);
            tvPass = view.findViewById(R.id.tv_pass);
            name = view.findViewById(R.id.name);
            dot = view.findViewById(R.id.tv_dot);
            lineRight = view.findViewById(R.id.line_right);
            lineleft = view.findViewById(R.id.line_left);
        }
    }

    public BusListAdapter(List<BusStation> iconList) {
        mList = iconList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bus_station, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BusStation busStation = mList.get(position);
        holder.name.setText(busStation.getName());

        //region 首位不显示线条
        if (position == 0) {
            holder.lineleft.setVisibility(View.INVISIBLE);
        } else if (position == mList.size() - 1) {
            holder.lineRight.setVisibility(View.INVISIBLE);
        } else {
            holder.lineleft.setVisibility(View.VISIBLE);
            holder.lineRight.setVisibility(View.VISIBLE);
        }
        //endregion

        //region 根据位置设置颜色
        if (position == mCurrentSelected) {
            holder.name.setTextColor(0xfffe871f);
            holder.dot.setText("0");
            holder.dot.setBackgroundResource(R.drawable.ic_dot_selected_24dp);
            holder.lineRight.setBackgroundColor(0xfff2f2f2);
            holder.lineleft.setBackgroundColor(0xffffe7a1);
        } else if (position < mCurrentSelected) {
            holder.name.setTextColor(0xff000000);
            holder.dot.setText(String.valueOf(mCurrentSelected - position));
            holder.dot.setBackgroundResource(R.drawable.ic_dot_selected_24dp);
            holder.lineRight.setBackgroundColor(0xffffe7a1);
            holder.lineleft.setBackgroundColor(0xffffe7a1);
        } else if (position > mCurrentSelected) {
            holder.name.setTextColor(0xff999999);
            holder.dot.setBackgroundResource(R.drawable.ic_dot_unselected_24dp);
            holder.dot.setText(String.valueOf(position - mCurrentSelected));
            holder.lineRight.setBackgroundColor(0xfff2f2f2);
            holder.lineleft.setBackgroundColor(0xfff2f2f2);
        }
        //endregion

        //region 设置车图标显示
        if (busStation.getArrive() == 0) {
            holder.tvArrive.setVisibility(View.INVISIBLE);
        } else
            holder.tvArrive.setVisibility(View.VISIBLE);

        if (busStation.getPass() == 0) {
            holder.tvPass.setVisibility(View.INVISIBLE);
        } else {
            holder.tvPass.setVisibility(View.VISIBLE);
            if(busStation.getPass()>1)holder.tvPass.setText(String.valueOf(busStation.getPass()));
            else holder.tvPass.setText("");
        }
        //endregion
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void setSelected(int s) {
        mCurrentSelected = s;
    }

    public int getSelected() {
        return mCurrentSelected;
    }
}