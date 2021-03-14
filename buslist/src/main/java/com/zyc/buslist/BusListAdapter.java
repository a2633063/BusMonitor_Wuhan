package com.zyc.buslist;


import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.ViewHolder> {

    private List<BusStation> mList;
    private int mCurrentSelected = -1;

    //记录不同字数时的字体大小
    Map<Integer, Float> textSizeHashMap = new HashMap<Integer, Float>();

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
    class ViewHolder extends RecyclerView.ViewHolder {
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

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, getLayoutPosition(), mList.get(getLayoutPosition()).getName());
                    }
                }
            });
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        BusStation busStation = mList.get(position);

        if (mCurrentSelected < 0) mCurrentSelected = getItemCount() - 1;

        //region 显示站名,并自动调整字体大小
        holder.name.setText(busStation.getName());
        final TextView v = holder.name;
        if (textSizeHashMap.get(v.getText().length()) != null)
            v.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeHashMap.get(v.getText().length()));
        else v.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40);

        holder.name.post(new Runnable() {
            public void run() {
                if (v.getTop() + v.getHeight() > ((ConstraintLayout) v.getParent()).getHeight()) {
                    v.setTextSize(TypedValue.COMPLEX_UNIT_PX, v.getTextSize() - 1);
                    v.post(this);
                } else if (!textSizeHashMap.containsKey(v.getText().length())) {
                    textSizeHashMap.put(v.getText().length(), v.getTextSize());
                }
            }
        });
        //endregion

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

        holder.tvArrive.setVisibility(busStation.getArrive() == 0 ? View.INVISIBLE : View.VISIBLE);
        holder.tvPass.setVisibility(busStation.getPass() == 0 ? View.INVISIBLE : View.VISIBLE);

//        if (busStation.getArrive() > 1) {
            holder.tvArrive.setText((busStation.getArrive() > 1)?String.valueOf(busStation.getArrive()):"");
            if (busStation.getArriveDoubleDeck() < busStation.getArrive()) {
                holder.tvArrive.setBackgroundResource(position > mCurrentSelected ? R.drawable.ic_bus_unselected_24dp : R.drawable.ic_bus_station_selected_24dp);
            } else {
                holder.tvArrive.setBackgroundResource(position > mCurrentSelected ? R.drawable.ic_bus_double_unselected_24dp : R.drawable.ic_bus_double_station_selected_24dp);
            }
//        } else {
//            holder.tvArrive.setText("");
//            if (busStation.getArriveDoubleDeck() < 1 || busStation.getArriveDoubleDeck() < busStation.getArrive()) {
//
//            } else {
//                holder.tvArrive.setBackgroundResource(position > mCurrentSelected ? R.drawable.ic_bus_unselected_24dp : R.drawable.ic_bus_station_selected_24dp);
//            }
//        }

        if (busStation.getPass() > 1) {
            holder.tvPass.setText(String.valueOf(busStation.getPass()));
            if (busStation.getPassDoubleDeck() < busStation.getPass()) {
                holder.tvPass.setBackgroundResource(position >= mCurrentSelected ? R.drawable.ic_bus_num_unselected_24dp : R.drawable.ic_bus_num_selected_24dp);
            } else {
                holder.tvPass.setBackgroundResource(position >= mCurrentSelected ? R.drawable.ic_bus_double_num_unselected_24dp : R.drawable.ic_bus_double_num_selected_24dp);
            }
        } else {
            holder.tvPass.setText("");
            if (busStation.getPassDoubleDeck() > 0) {
                holder.tvPass.setBackgroundResource(position >= mCurrentSelected ? R.drawable.ic_bus_double_unselected_24dp : R.drawable.ic_bus_double_selected_24dp);
            } else {
                holder.tvPass.setBackgroundResource(position >= mCurrentSelected ? R.drawable.ic_bus_unselected_24dp : R.drawable.ic_bus_selected_24dp);
            }
        }
        //endregion

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void clear() {
        mList.clear();
    }

    public BusStation getItem(int position) {
        return mList.get(position);
    }

    public void setSelected(int s) {
        mCurrentSelected = s;
    }

    public int getSelected() {
        return mCurrentSelected;
    }
}