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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final static String Tag = "MainActivity";
    ArrayList<Integer> listInt = new ArrayList<>();
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

        List<String> mData=new ArrayList<>();
        mData.add("907");
        mData.add("2");

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
