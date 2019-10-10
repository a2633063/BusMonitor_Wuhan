package com.zyc.busmonitor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.TextView;

import com.zyc.busmonitor.mainrecycler.MainRecyclerAdapter;
import com.zyc.busmonitor.mainrecycler.MainRecyclerItemTouchHelper;
import com.zyc.busmonitor.mainrecycler.SpacesRecyclerViewItemDecoration;
import com.zyc.busmonitoritem.BusLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final static String Tag = "MainActivity";
    TextView log;

    MainRecyclerAdapter adapter;

    //region Handler
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        log = findViewById(R.id.log);

        List<BusLine> mData=new ArrayList<>();
        mData.add(new BusLine("907","907",1));
        mData.add(new BusLine("2","907",0));

        //region RecyclerView初始化
        RecyclerView rv=  findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
//      GridLayoutManager layoutManager=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        adapter=new MainRecyclerAdapter(mData);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);

        //设置长按拖动排序
        ItemTouchHelper helper=new ItemTouchHelper(new MainRecyclerItemTouchHelper(adapter));
        helper.attachToRecyclerView(rv);

        // 设置RecyclerView Item边距
        rv.addItemDecoration(new SpacesRecyclerViewItemDecoration(10,10,10,20));

        //endregion

    }

}
