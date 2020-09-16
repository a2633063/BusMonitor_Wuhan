package com.zyc.busmonitor.addbus;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zyc.busmonitor.R;
import com.zyc.busmonitoritem.BusLine;

import java.util.List;


public class AddBusAdapter extends BaseAdapter {

    private List<BusLine> mData;
    private LayoutInflater inflater;

    public List<BusLine> getDataList() {
        return mData;
    }

    public AddBusAdapter(Context context, List<BusLine> mData) {
        this.mData = mData;
        inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return mData.size();
    }

    public BusLine getItem(int position) {
        return mData.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position1, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View view = null;
        final int position = position1;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_add_bus_list, null);
            holder = new ViewHolder();

            holder.tv = convertView.findViewById(R.id.tv_bus);

            holder.tv_line = convertView.findViewById(R.id.tv_line);

            convertView.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        BusLine busLine= mData.get(position);
        holder.tv.setText(busLine.getLineName());

        holder.tv_line.setText("("+busLine.getStartStopName()+" 至 "+busLine.getEndStopName()+")");
        return convertView;
    }


    class ViewHolder {
        TextView tv;
        TextView tv_line;
    }

}