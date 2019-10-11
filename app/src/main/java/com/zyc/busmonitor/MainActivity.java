package com.zyc.busmonitor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.zyc.busmonitor.mainrecycler.MainRecyclerAdapter;
import com.zyc.busmonitor.mainrecycler.MainRecyclerItemTouchHelper;
import com.zyc.busmonitor.mainrecycler.SideRecyclerAdapter;
import com.zyc.busmonitor.mainrecycler.SpacesRecyclerViewItemDecoration;
import com.zyc.busmonitoritem.BusLine;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final static String Tag = "MainActivity";
    TextView log;

    private Toolbar toolbar;
    DrawerLayout drawerLayout;

    private MainRecyclerAdapter adapter;
    private SideRecyclerAdapter sideAdapter;

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
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        log = findViewById(R.id.log);

        //region 侧边栏 初始化
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);

        //endregion


        List<BusLine> mData = new ArrayList<>();
        mData.add(new BusLine("907", "907", 1));
        mData.add(new BusLine("234", "234", 0));
        mData.add(new BusLine("234", "234", 0));
        mData.add(new BusLine("234", "234", 0));
        mData.add(new BusLine("234", "234", 0));
        mData.add(new BusLine("2", "2", 0));

        //region 侧边RecyclerView初始化
        RecyclerView sideRecyclerView = findViewById(R.id.side_recyclerView);
        LinearLayoutManager sideLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//      GridLayoutManager layoutManager=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        sideAdapter = new SideRecyclerAdapter(mData);
        sideRecyclerView.setLayoutManager(sideLayoutManager);
        sideRecyclerView.setAdapter(sideAdapter);

        //设置长按拖动排序
        ItemTouchHelper sideHelper = new ItemTouchHelper(new MainRecyclerItemTouchHelper(adapter));
        sideHelper.attachToRecyclerView(sideRecyclerView);

//        // 设置RecyclerView Item边距
//        sideRecyclerView.addItemDecoration(new SpacesRecyclerViewItemDecoration(10, 10, 10, 20));

        //endregion

        //region RecyclerView初始化
        RecyclerView rv = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//      GridLayoutManager layoutManager=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        adapter = new MainRecyclerAdapter(mData);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);

//        //设置长按拖动排序
//        ItemTouchHelper helper = new ItemTouchHelper(new MainRecyclerItemTouchHelper(adapter));
//        helper.attachToRecyclerView(rv);

        // 设置RecyclerView Item边距
        rv.addItemDecoration(new SpacesRecyclerViewItemDecoration(10, 10, 10, 20));

        //endregion

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //region toolbar 菜单栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_device_settings) {


            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

}
