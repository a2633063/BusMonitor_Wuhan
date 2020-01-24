package com.zyc.busmonitor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.zyc.busmonitor.addbus.AddBusActivity;
import com.zyc.busmonitor.mainrecycler.MainRecyclerAdapter;
import com.zyc.busmonitor.mainrecycler.SideRecyclerAdapter;
import com.zyc.busmonitor.mainrecycler.SideRecyclerItemTouchHelper;
import com.zyc.busmonitor.mainrecycler.SpacesRecyclerViewItemDecoration;
import com.zyc.busmonitoritem.BusLine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.zyc.MyFunction.getLocalVersionName;

public class MainActivity extends AppCompatActivity {
    public final static String Tag = "MainActivity";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    TextView log;

    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    RecyclerView mainRecyclerView;

    List<BusLine> mData = new ArrayList<>();
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

        //region 设置/关于/退出按钮
        //region 设置按钮
//        findViewById(R.id.tv_setting).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                startActivity(new Intent(MainActivity.this, SettingActivity.class));
//                drawerLayout.closeDrawer(GravityCompat.START);//关闭侧边栏
//
//            }
//        });
        //endregion
        //region 退出按钮
        findViewById(R.id.tv_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //endregion
        //region 关于按钮
        findViewById(R.id.tv_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);//关闭侧边栏
                popupwindowInfo();
            }
        });
        //endregion

        //region 打赏
        navigationView.getHeaderView(0).findViewById(R.id.tv_reward)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawerLayout.closeDrawer(GravityCompat.START);//关闭侧边栏
                        popupwindowInfo();
                    }
                });
        //endregion
        //endregion


        //region 获取存储的公交数据
        mSharedPreferences = getSharedPreferences("setting", 0);
        mEditor = mSharedPreferences.edit();
        String busLineStr = mSharedPreferences.getString("busLine", null);
        if (busLineStr != null) {
            try {
                JSONObject jsonObject = new JSONObject(busLineStr);
                if (jsonObject.has("data")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject j = (JSONObject) jsonArray.get(i);
                        mData.add(new BusLine(j.getString("LineName"),
                                j.getString("LineNo"), j.getInt("Direction"), j.getInt("Selected")));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mData.clear();
            }

        }
        if (mData.size() == 0)
            mData.add(new BusLine("907", "907", 1));
        //endregion

        //region RecyclerView初始化
        mainRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//      GridLayoutManager layoutManager=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        adapter = new MainRecyclerAdapter(mData);
        mainRecyclerView.setLayoutManager(layoutManager);
        mainRecyclerView.setAdapter(adapter);

//        //设置长按拖动排序
//        ItemTouchHelper helper = new ItemTouchHelper(new MainRecyclerItemTouchHelper(adapter));
//        helper.attachToRecyclerView(mainRecyclerView);

        // 设置RecyclerView Item边距
        mainRecyclerView.addItemDecoration(new SpacesRecyclerViewItemDecoration(10, 10, 10, 20));

        //endregion
        //region 侧边RecyclerView初始化
        RecyclerView sideRecyclerView = findViewById(R.id.side_recyclerView);
        LinearLayoutManager sideLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//      GridLayoutManager layoutManager=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        sideAdapter = new SideRecyclerAdapter(mData, adapter);
        sideRecyclerView.setLayoutManager(sideLayoutManager);
        sideRecyclerView.setAdapter(sideAdapter);

        //设置长按拖动排序
        ItemTouchHelper sideHelper = new ItemTouchHelper(new SideRecyclerItemTouchHelper(sideAdapter, adapter));
        sideHelper.attachToRecyclerView(sideRecyclerView);

        // 设置RecyclerView Item边距
        sideRecyclerView.addItemDecoration(new SpacesRecyclerViewItemDecoration(10, 10, 10, 20));

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

    @Override
    protected void onDestroy() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();//实例一个JSON数组
            for (int i = 0; i < adapter.getItemCount(); i++) {
                BusLine b = adapter.get(i);
                JSONObject j = new JSONObject();
                j.put("LineName", b.getLineName());
                j.put("LineNo", b.getLineNo());
                j.put("Selected", b.getSelected());
                j.put("Direction", b.getDirection());
                jsonArray.put(j);
            }
            jsonObject.put("data", jsonArray);
            jsonObject.put("count", jsonArray.length());
            mEditor.putString("busLine", jsonObject.toString());
            mEditor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.onDestroy();
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
        if (id == R.id.menu_add) {
//            Intent intent = new Intent(MainActivity.this, AddBusActivity.class);
//            startActivity(intent);
            startActivityForResult(new Intent(MainActivity.this, AddBusActivity.class), 1);

            return true;
        }else if(id ==R.id.menu_metro){

        }
        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region 新增线路,接受AddBusActivity反馈的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode != RESULT_OK) return;
        //region 新增设备返回
        if (requestCode == 1) {
            BusLine b = (BusLine) intent.getSerializableExtra("busline");
            Log.d(Tag, b.getLineName());
            mData.add(0, b);
            adapter.notifyItemInserted(0);
//            sideAdapter.notifyItemInserted(0);
            mainRecyclerView.scrollToPosition(0);
//            adapter.notifyDataSetChanged();
            sideAdapter.notifyDataSetChanged();
        }
        //endregion
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("mac") && intent.getStringExtra("mac") != null)
            setIntent(intent);// must store the new intent unless getIntent() will return the old one
    }
    //endregion


    //region 弹窗
    private void popupwindowInfo() {

        final View popupView = getLayoutInflater().inflate(R.layout.popupwindow_main_info, null);
        final PopupWindow window = new PopupWindow(popupView, MATCH_PARENT, MATCH_PARENT, true);//wrap_content,wrap_content


        //region 控件初始化

        TextView tv_version = popupView.findViewById(R.id.tv_version);
        tv_version.setText("APP当前版本:" + getLocalVersionName(this));

        //region 支付宝跳转
        ImageView imageView = popupView.findViewById(R.id.alipay);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String intentFullUrl = "intent://platformapi/startapp?saId=10000007&" +
                        "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2Ffkx06093fjxuqmwbco9oka9%3F_s" +
                        "%3Dweb-other&_t=1472443966571#Intent;" +
                        "scheme=alipayqr;package=com.eg.android.AlipayGphone;end";
                Intent intent = null;
                try {
                    intent = Intent.parseUri(intentFullUrl, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException e) {
//                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "失败,支付宝有安装?", Toast.LENGTH_SHORT).show();
                }


                startActivity(intent);
            }
        });
        //endregion
        //region 作者github跳转
        TextView tv_author = popupView.findViewById(R.id.tv_author);
        tv_author.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tv_author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "跳转作者github...", Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse("https://github.com/a2633063");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        //endregion
        //region app页面跳转
        popupView.findViewById(R.id.btn_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://github.com/a2633063/BusMonitor_Wuhan");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        //endregion

        //region window初始化
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.alpha(0xffff0000)));
        window.setOutsideTouchable(true);
        window.getContentView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                window.dismiss();
                return true;
            }
        });
        //endregion
        //endregion
        window.update();
        window.showAtLocation(popupView, Gravity.CENTER, 0, 0);

    }
    //endregion

}
