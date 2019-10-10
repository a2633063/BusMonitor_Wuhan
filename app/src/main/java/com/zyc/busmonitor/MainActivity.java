package com.zyc.busmonitor;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zyc.MyFunction;

import com.zyc.busmonitor.mainrecycler.MainRecyclerAdapter;
import com.zyc.busmonitor.mainrecycler.MainRecyclerItemTouchHelper;
import com.zyc.busmonitor.mainrecycler.SpacesRecyclerViewItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        mData.add("901");
        mData.add("907");
        mData.add("1001");
        mData.add("777");
        mData.add("234");
        mData.add("307");

        //region RecyclerView初始化
        RecyclerView rv=  findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
//      GridLayoutManager layoutManager=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        adapter=new MainRecyclerAdapter(mData);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
        ItemTouchHelper helper=new ItemTouchHelper(new MainRecyclerItemTouchHelper(adapter));
        helper.attachToRecyclerView(rv);


        //region 设置RecyclerView Item边距
        HashMap<String, Integer> spacesVelue = new HashMap<>();
        spacesVelue.put(SpacesRecyclerViewItemDecoration.TOP_SPACE, 10);
        spacesVelue.put(SpacesRecyclerViewItemDecoration.BOTTOM_SPACE, 20);
        spacesVelue.put(SpacesRecyclerViewItemDecoration.LEFT_SPACE, 10);
        spacesVelue.put(SpacesRecyclerViewItemDecoration.RIGHT_SPACE, 10);
        rv.addItemDecoration(new SpacesRecyclerViewItemDecoration(spacesVelue));
        //endregion
        //endregion

    }

}
