package com.zyc.busmonitor.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.zyc.busmonitor.R;

import java.util.List;

public class MainNewsListAdapter extends BaseAdapter {

    private Context context;
    private List<News> mdata;
    private LayoutInflater inflater;

    public MainNewsListAdapter(Context context, List data) {
        this.context = context;
        this.mdata = data;
        inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return mdata.size();
    }

    public News getItem(int position) {
        return mdata.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position1, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View view = null;
        final int position = position1;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_main_news_list, null);
            holder = new ViewHolder();

            holder.publishDate = convertView.findViewById(R.id.tv_publishDate);
            holder.topic = convertView.findViewById(R.id.tv_topic);
            holder.simpleText = convertView.findViewById(R.id.tv_simpleText);

            convertView.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.publishDate.setText(mdata.get(position).getDateString());
        holder.topic.setText(mdata.get(position).getTopic());
        holder.simpleText.setText(mdata.get(position).getSimpleText());


        return convertView;
    }


    class ViewHolder {

        TextView publishDate;
        TextView topic;
        TextView simpleText;
    }
}
