package com.zyc.busmonitor;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.zyc.WebService;
import com.zyc.busmonitor.addbus.AddBusActivity;
import com.zyc.busmonitor.mainrecycler.MainRecyclerAdapter;
import com.zyc.busmonitor.mainrecycler.SideRecyclerAdapter;
import com.zyc.busmonitor.mainrecycler.SideRecyclerItemTouchHelper;
import com.zyc.busmonitor.mainrecycler.SpacesRecyclerViewItemDecoration;
import com.zyc.busmonitor.news.MainNewsListAdapter;
import com.zyc.busmonitor.news.News;
import com.zyc.busmonitoritem.BusLine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.zyc.MyFunction.getLocalVersionName;

public class MainActivity extends AppCompatActivity {
    public final static String Tag = "MainActivity";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    TextView log;

    //region 控件
    NavigationView nav_view;
    NavigationView nav_news;
    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    RecyclerView mainRecyclerView;
    TextView tv_joinQQGroup;
    TextView tv_tip;
    List<BusLine> mData = new ArrayList<>();
    private MainRecyclerAdapter adapter;
    private SideRecyclerAdapter sideAdapter;


    List<News> NewsList = new ArrayList<>();
    ListView lv_news;
    MainNewsListAdapter newsAdapter;
    //endregion

    int newsPage = 0;
    boolean updateListFlag = false;

    //region Handler
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result;
            switch (msg.what) {
                //region 列表更新标志位
                case 0:
                    updateListFlag = true;

                    if (adapter.getItemCount() < 1) {
                        tv_tip.setVisibility(View.VISIBLE);
                        mainRecyclerView.setVisibility(View.GONE);
                    } else {
                        tv_tip.setVisibility(View.GONE);
                        mainRecyclerView.setVisibility(View.VISIBLE);
                    }
                    break;
                //endregion
                //region 更新公交公告内容
                case 1:
                    final int page = msg.arg1;
                    Log.d(Tag, "获取公告,页码:" + page);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = 2;
                            msg.obj = WebService.WebConnectPost("http://advertisement.whgjzt.com/notice/queryHasPublish.do", "appId=HvBJ1PkBttbqxeBF46Aa&PageSize=10&PageIndex=" + page + "&areaCode=420100");
                            handler.sendMessageDelayed(msg, 0);// 执行耗时的方法之后发送消给handler
                        }
                    }).start();
                    break;
                //endregion
                //region 获取公交公告内容
                case 2:
                    result = (String) msg.obj;
                    Log.d(Tag, "result:" + result);
                    try {
                        //region 登录返回空数据
                        if (result == null) {
                            throw new JSONException("无数据返回");
                        }
                        //endregion

                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("resultCode") && jsonObject.getInt("resultCode") != 1) {
                            String resultDes = jsonObject.getString("resultDes");
                            throw new JSONException("返回码错误:" + resultDes);
                        }

                        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("rows");
//                        if (msg.arg1 == 0)
//                            NewsList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject j = jsonArray.getJSONObject(i);
                            News n = new News(j.getInt("serialNo"), j.getString("topic"), j.getString("simpleText"),
                                    j.getString("publishDate"), j.getString("publishName"), j.getString("contentUrl"));
                            NewsList.add(n);
                        }
                        newsAdapter.notifyDataSetChanged();
                        newsPage++;
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Message msg = new Message();
//                                msg.what = 2;
//                                msg.obj = WebService.WebConnectPost("http://manage.wuhancloud.cn/notice/queryHasPublish.do", "appId=HvBJ1PkBttbqxeBF46Aa&PageSize=10&PageIndex=1&areaCode=420100");
//                                handler.sendMessageDelayed(msg, 0);// 执行耗时的方法之后发送消给handler
//                            }
//                        }).start();
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                    break;
                //endregion

                //region 获取app最新版本
                case 100:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = 101;
                            String res = WebService.WebConnect("https://gitee.com/api/v5/repos/a2633063/BusMonitor_Wuhan/releases/latest");
                            msg.obj = res;
                            handler.sendMessageDelayed(msg, 0);// 执行耗时的方法之后发送消给handler
                        }
                    }).start();
                    break;
                case 101:
                    try {
                        if (msg.obj == null) throw new JSONException("获取版本信息失败,请重试");
                        JSONObject obj = new JSONObject((String) msg.obj);
                        if (!obj.has("tag_name")
                                || !obj.has("name")
                                || !obj.has("body")
                                || !obj.has("created_at")) throw new JSONException("获取最新版本信息失败");

                        final String body = obj.getString("body");
                        final String name = obj.getString("name");
                        final String tag_name = obj.getString("tag_name");
                        final String created_at = obj.getString("created_at");

                        String tag_name_old = getLocalVersionName(MainActivity.this);
                        if (tag_name.equals(tag_name_old)) {
                            Log.d(Tag, "已是最新版本");
                        } else {
                            Log.d(Tag, "当前版本:" + tag_name_old + ",发布版本:" + tag_name);
                            boolean show_ota = true;
                            String[] version_new = tag_name.replaceAll("[^.1234567890]", "").split("\\.");
                            String[] version_old = tag_name_old.replaceAll("[^.1234567890]", "").split("\\.");

                            for (int i = 0; i < version_new.length && i < version_old.length; i++) {
                                try {
                                    int a = Integer.parseInt(version_new[i]);
                                    int b = Integer.parseInt(version_old[i]);
                                    if (b < a) break;
                                    else if (b > a) {
                                        show_ota = false;
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (!show_ota) {
                                Toast.makeText(MainActivity.this, "当前版本暂时未发布，测试中\n当前版本:" + tag_name_old + "\n发布版本:" + tag_name, Toast.LENGTH_LONG).show();
                                break;
                            }

                            String version_no_ask = mSharedPreferences.getString("version_no_ask", "");

                            if (version_no_ask.equals(tag_name)) {
                                Snackbar.make(findViewById(R.id.recyclerView), "APP有更新版本:" + tag_name + "\r\n请更新", Snackbar.LENGTH_LONG)

                                        .setAction("更新", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                updateApp(tag_name, name, body, created_at);
                                            }
                                        }).show();
                            } else {
                                updateApp(tag_name, name, body, created_at);
                            }

                        }

                    } catch (JSONException e) {
//                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "获取最新版本失败", Toast.LENGTH_LONG).show();
                    }
                    break;
                //endregion


                //region 获取app公告
                case 200:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String owner = "a2633063";
                            String repo = "Release";
                            String path = "/notice";
                            String ref = "master";
                            Message msg = new Message();
                            msg.what = 201;
                            String res = WebService.WebConnect("https://gitee.com/api/v5/repos/" + owner + "/" + repo + "/contents" + path);
                            msg.obj = res;
                            handler.sendMessageDelayed(msg, 0);// 执行耗时的方法之后发送消给handler
                        }
                    }).start();
                    break;
                case 201:
                    try {
                        if (msg.obj == null) throw new JSONException("获取公告路径信息失败,请重试");

                        JSONArray obj = new JSONArray((String) msg.obj);

                        String file_name = "BusMonitor_Wuhan.txt";
                        String sha = null;
                        String notice_content_url = null;
                        for (int i = 0; i < obj.length(); i++) {
                            JSONObject jsonFile = obj.getJSONObject(i);
                            if (!jsonFile.getString("type").equals("file")) continue;
                            if (jsonFile.getString("name").equals(file_name)) {
                                sha = jsonFile.getString("sha");
                                notice_content_url = jsonFile.getString("url");
                            }
                        }
                        if (sha == null || notice_content_url == null) break;

                        String notice_no_ask = mSharedPreferences.getString("notice_no_ask", "");
                        if (!notice_no_ask.equals(sha)) {
                            String finalNotice_content_url = notice_content_url;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Message msg = new Message();
                                    msg.what = 202;
                                    String res = WebService.WebConnect(finalNotice_content_url);
                                    msg.obj = res;
                                    handler.sendMessageDelayed(msg, 0);// 执行耗时的方法之后发送消给handler
                                }
                            }).start();
                        }


                    } catch (JSONException e) {
//                        e.printStackTrace();
                        Log.e(Tag, "获取APP公告文件路径失败");
                    }
                    break;
                case 202:
                    try {
                        if (msg.obj == null) throw new JSONException("获取APP公告內容失败,请重试");

                        JSONObject obj = new JSONObject((String) msg.obj);
                        String content = obj.getString("content");
                        content = new String(Base64.decode(content.getBytes(), Base64.DEFAULT));
                        noticeShow(content, obj.getString("sha"));
                    } catch (JSONException e) {
//                        e.printStackTrace();
                        Log.e(Tag, "获取APP公告內容失败");
                    }
                    break;
                //endregion
            }

        }
    };

    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //region 控件初始化
        log = findViewById(R.id.log);
        tv_tip = findViewById(R.id.tv_tip);

        //region 侧边栏 初始化
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //drawerLayout.removeDrawerListener();
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

                if (drawerView.getId() == nav_news.getId()) {
                    if (NewsList.size() < 10) {
                        Message msg = new Message();
                        msg.what = 1;
                        msg.arg1 = newsPage;
                        handler.sendMessageDelayed(msg, 0);// 执行耗时的方法之后发送消给handler

                    }
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        nav_view = findViewById(R.id.nav_view);
        nav_news = findViewById(R.id.nav_news);
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
        nav_view.getHeaderView(0).findViewById(R.id.tv_reward)
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
                        BusLine b = new BusLine(j.getString("LineName"),
                                j.getString("LineNo"), j.getInt("Selected"));
                        b.setLineId(j.getString("LineId"));
                        b.setLine2Id(j.getString("Line2Id"));
                        mData.add(b);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mData.clear();
            }

        }

        //endregion

        //region RecyclerView初始化
        mainRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//      GridLayoutManager layoutManager=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        adapter = new MainRecyclerAdapter(mData);
        mainRecyclerView.setLayoutManager(layoutManager);
        mainRecyclerView.setAdapter(adapter);
        adapter.setOnItemBusStationClickListener(new MainRecyclerAdapter.OnItemBusStationClickListener() {
            @Override
            public void onItemBusStationClick(BusLine bus, int position, String data) {
                handler.sendEmptyMessage(0);
            }
        });
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
        ItemTouchHelper sideHelper = new ItemTouchHelper(new SideRecyclerItemTouchHelper(sideAdapter, adapter, handler));
        sideHelper.attachToRecyclerView(sideRecyclerView);

        // 设置RecyclerView Item边距
        sideRecyclerView.addItemDecoration(new SpacesRecyclerViewItemDecoration(10, 10, 10, 20));

        sideAdapter.setOnItemClickListener(new SideRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                mainRecyclerView.smoothScrollToPosition(position);
            }
        });
        //endregion


        nav_view.getHeaderView(0).findViewById(R.id.tv_joinQQGroup)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        joinQQGroup();
                    }
                });

        //region 公告
        lv_news = findViewById(R.id.lv_news);
        newsAdapter = new MainNewsListAdapter(this, NewsList);
        lv_news.setAdapter(newsAdapter);

        TextView t = new TextView(this);
        t.setTextColor(0xffcccccc);
        t.setText("更多公告内容请到官方查看");
        //t.setTextSize(t.getTextSize()/2);
        t.setGravity(Gravity.CENTER);
        lv_news.addFooterView(t);
        lv_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                intent.putExtra("url", NewsList.get(position).getContentUrl());
                startActivity(intent);
            }
        });
        //endregion
        //endregion

        if (mData.size() == 0) {
            //mData.add(new BusLine("907", "907", 1));
            tv_tip.setVisibility(View.VISIBLE);
            mainRecyclerView.setVisibility(View.GONE);
        } else {
            tv_tip.setVisibility(View.GONE);
            mainRecyclerView.setVisibility(View.VISIBLE);
        }

        //region 获取公告内容
        //Message msg = new Message();
        //msg.what = 1;
        //msg.arg1 = newsPage;
        //handler.sendMessageDelayed(msg, 0);// 执行耗时的方法之后发送消给handler
        //endregion


        //region 获取最新版本
        handler.sendEmptyMessage(100);
        //endregion
        //region 获取公告
        handler.sendEmptyMessage(200);
        //endregion
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onPause() {
        Log.d(Tag, "onPause");
        //region 需要时更新列表
        if (updateListFlag) {
            JSONObject jsonObject = new JSONObject();
            try {
                JSONArray jsonArray = new JSONArray();//实例一个JSON数组
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    BusLine b = adapter.get(i);
                    JSONObject j = new JSONObject();
                    j.put("LineName", b.getLineName());
                    j.put("LineNo", b.getLineNo());
                    j.put("Selected", b.getSelected());
                    j.put("LineId", b.getLineId());
                    j.put("Line2Id", b.getLine2Id());
                    jsonArray.put(j);
                }
                jsonObject.put("data", jsonArray);
                jsonObject.put("count", jsonArray.length());
                mEditor.putString("busLine", jsonObject.toString());
                mEditor.commit();
                updateListFlag = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //endregion
        super.onPause();
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
        int id = item.getItemId();
        if (id == R.id.menu_add) {
            startActivityForResult(new Intent(MainActivity.this, AddBusActivity.class), 1);
            return true;
        } else if (id == R.id.menu_news) {
            drawerLayout.openDrawer(GravityCompat.END);
            return true;
        } else if (id == R.id.menu_alipay) {

            String intentFullUrl = "intent://platformapi/startapp?appId=60000098&url=/www/offline_qrcode.html?source=WH_BUS_APP#Intent;scheme=alipays;package=com.eg.android.AlipayGphone;end";
            Intent intent = null;
            try {
                intent = Intent.parseUri(intentFullUrl, Intent.URI_INTENT_SCHEME);
                startActivity(intent);
            } catch (URISyntaxException e) {
//                    e.printStackTrace();
                Toast.makeText(MainActivity.this, "失败,支付宝有安装?", Toast.LENGTH_SHORT).show();
            }


            return true;
        } else if (id == R.id.menu_subway) {
            Intent intent = new Intent(MainActivity.this, NewsActivity.class);
            intent.putExtra("url", "file:///android_asset/subway.html");
            startActivity(intent);
            return true;
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
            handler.sendEmptyMessage(0);
        }
        //endregion
        super.onActivityResult(requestCode, resultCode, intent);
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
//        ImageView imageView = popupView.findViewById(R.id.alipay);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    String intentFullUrl = "intent://platformapi/startapp?saId=10000007&" +
//                            "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2Ffkx06093fjxuqmwbco9oka9%3F_s" +
//                            "%3Dweb-other&_t=1472443966571#Intent;" +
//                            "scheme=alipayqr;package=com.eg.android.AlipayGphone;end";
//                    Intent intent = null;
//                    intent = Intent.parseUri(intentFullUrl, Intent.URI_INTENT_SCHEME);
//                    startActivity(intent);
//                } catch (URISyntaxException e) {
////                    e.printStackTrace();
//                    Toast.makeText(MainActivity.this, "失败,支付宝有安装?\n请使用支付宝扫码", Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
////                    e.printStackTrace();
//                    Toast.makeText(MainActivity.this, "失败,支付宝有安装?\n请使用支付宝扫码", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
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

        //region 作者github跳转
        TextView tv_qq_group = popupView.findViewById(R.id.tv_qq_group);
        tv_qq_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinQQGroup();
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
        popupView.findViewById(R.id.btn_app2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://gitee.com/a2633063/BusMonitor_Wuhan");
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

    private void updateApp(final String tag_name, final String name, final String body, final String created_at) {

        //region 显示APP更新弹窗
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("请更新版本:" + tag_name)
                .setMessage(name + "\r\n" + body + "\r\n更新日期:" + created_at)
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse("https://github.com/a2633063/BusMonitor_Wuhan/releases/latest");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("网站打不开使用备用地址?")
                                .setPositiveButton("备用地址1", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Uri uri = Uri.parse("https://gitee.com/a2633063/BusMonitor_Wuhan/releases");
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .setNeutralButton("直接下载", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Uri uri = Uri.parse("http://zipzhang.top/BusMonitor_Wuhan/zBus_" + tag_name + ".apk");
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }
                                })
                                .create()
                                .show();
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "不再提示此版本", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mEditor = getSharedPreferences("setting", 0).edit();
                mEditor.putString("version_no_ask", tag_name);
                mEditor.commit();
            }
        });
        alertDialog.show();

        // 在dialog执行show之后才能来设置
        TextView tvMsg = (TextView) alertDialog.findViewById(android.R.id.message);
        String HtmlStr = String.format(getResources().getString(R.string.app_ota_message), name, body, created_at).replace("\n", "<br />");
        Log.d(Tag, HtmlStr);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvMsg.setText(Html.fromHtml(HtmlStr, Html.FROM_HTML_MODE_COMPACT));
        } else {
            tvMsg.setText(Html.fromHtml(HtmlStr));
        }
        //endregion

    }

    private void noticeShow(final String content, final String sha) {

        //region 显示APP公告弹窗
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("公告")
                .setMessage(content)
                .setPositiveButton("确定", null)
                .setNeutralButton("不再显示", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mEditor = getSharedPreferences("setting", 0).edit();
                        mEditor.putString("notice_no_ask", sha);
                        mEditor.commit();
                    }
                })
                .create();
        alertDialog.show();

        // 在dialog执行show之后才能来设置
        TextView tvMsg = (TextView) alertDialog.findViewById(android.R.id.message);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvMsg.setText(Html.fromHtml(content.replace("\n", "<br />"), Html.FROM_HTML_MODE_COMPACT));
        } else {
            tvMsg.setText(Html.fromHtml(content.replace("\n", "<br />")));
        }
        //endregion

    }

    //endregion

    //region 加QQ群
    private void joinQQGroup() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + "orZMb-cWCbpV2qEcfSINdUkUj4CR-beT"));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            try {
                ClipData myClip = ClipData.newPlainText("text", "zbus");
                ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(myClip);
                Toast.makeText(MainActivity.this, "已将加群密码复制到剪贴板!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            Toast.makeText(MainActivity.this, "未安装手Q!", Toast.LENGTH_SHORT).show();
        }
    }
    //endregion
}
