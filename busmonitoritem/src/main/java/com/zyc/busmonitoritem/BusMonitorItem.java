package com.zyc.busmonitoritem;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyc.WebService;
import com.zyc.buslist.BusList;
import com.zyc.buslist.BusStation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BusMonitorItem extends LinearLayout {
    public final static String Tag = "BusFragment";

    //region Description
    ImageView ivRefresh;
    BusList busList;
    TextView tvBus;
    TextView tvStationStartEnd;
    TextView tvStationTime;
    TextView tv_first_bus;
    TextView tv_second_bus;

    LinearLayout llRefresh;
    LinearLayout llDirection;
    //endregion
    private ObjectAnimator objectAnimator;
    private boolean isRefresh = false;


    private List<BusStation> mDataList = new ArrayList<>();

    private String bus = null;
    private int direction = 0;

    //region Handler
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                //region 获取公交数据
                case 1:
                    if (!objectAnimator.isStarted()) objectAnimator.start();
                    isRefresh = true;
                    /* 开启一个新线程，在新线程里执行耗时的方法 */
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = String.format(getResources().getString(R.string.url_bus), bus, direction);
                            Log.d(Tag, "URL:" + url);
                            Message msg = new Message();
                            msg.what = 2;
                            msg.obj = WebService.WebConnect(url);
                            handler.sendMessageDelayed(msg, 0);// 执行耗时的方法之后发送消给handler
                        }

                    }).start();
                    break;
                //endregion

                //region 返回数据
                case 2:
                    isRefresh = false;
                    String result = (String) msg.obj;
                    Log.d(Tag, "result:" + result);
                    try {
                        //region 登录返回空数据
                        if (result == null) {
                            throw new JSONException("无数据返回");
                        }
                        //endregion
//                        Log.d(Tag, "result:" + result);

                        JSONObject jsonObject = new JSONObject(result);

                        if (!jsonObject.has("resultCode")
                                || !jsonObject.has("data")
                                || jsonObject.getInt("resultCode") != 1
                        ) {
                            throw new JSONException("更新数据失败");
                        }
                        JSONObject jsonData = jsonObject.getJSONObject("data");

                        int stopsNum = jsonData.getInt("stopsNum");
                        //region 更新车辆信息 站点信息
                        tvBus.setText(jsonData.getString("lineName"));
                        tvStationStartEnd.setText(jsonData.getString("startStopName")
                                + " → " + jsonData.getString("endStopName")
                                + "   " + stopsNum + "站");
                        tvStationTime.setText(jsonData.getString("firstTime")
                                + "-" + jsonData.getString("lastTime")
                                + "  票价 " + jsonData.getString("price") + "元"
                        );
                        //endregion

                        //region 更新车站信息
                        JSONArray jsonStops = jsonData.getJSONArray("stops");
                        busList.clear();
                        for (int i = 0; i < jsonStops.length(); i++) {
                            BusStation b = new BusStation(jsonStops.getJSONObject(i).getString("stopName"));
                            busList.addBusStation(b);
                        }

                        if (busList.getSelected() >= busList.getCount())
                            busList.setSelected(busList.getCount() - 1);
                        //endregion

                        //region 更新车辆所有实时信息
                        int firstBus = 9999;
                        int secondBus = 9999;
                        int seclectBus = busList.getSelected();
                        JSONArray jsonBuses = jsonData.getJSONArray("buses");
                        for (int i = 0; i < jsonBuses.length(); i++) {
                            String str = jsonBuses.getString(i);
                            Log.d(Tag, str);
                            String[] arr = str.split("\\|");
                            if (arr.length != 6) throw new JSONException("数据错误");
                            int id = Integer.valueOf(arr[0]);
                            int busStation = Integer.valueOf(arr[2]);
                            int isStation = Integer.valueOf(arr[3]);
                            Log.d(Tag, "车辆" + id + "站点:" + busStation + "是否到站:" + isStation);

                            //region 更新车辆到站信息
                            if (isStation == 1) {//到站
                                busList.getItem(busStation - 1).setArrive(busList.getItem(busStation - 1).getArrive() + 1);
                            } else {
                                busList.getItem(busStation - 2).setPass(busList.getItem(busStation - 2).getPass() + 1);
                            }
                            //endregion

                            //region 到站剩余站更新
                            //region 计算到站站数
                            if (busStation == seclectBus + 1) {
                                if (isStation == 1) {//到站
                                    firstBus = 0;
                                } else {
                                    firstBus = 1;
                                }
                            } else if (busStation < seclectBus + 1) {
                                int temp = seclectBus + 1 - busStation;
                                if (temp < firstBus) {
                                    secondBus = firstBus;
                                    firstBus = temp;
                                } else if (temp < seclectBus) secondBus = temp;
                            }
                            //endregion
                            //endregion
                        }
                        //region 第1辆车到站提示
                        if (firstBus == 0) {
                            tv_first_bus.setText("到站");
                        } else if (firstBus == 1) {
                            tv_first_bus.setText("将至");
                        } else if (firstBus > busList.getCount()) {
                            tv_first_bus.setText("无");
                        } else {
                            tv_first_bus.setText(firstBus + "站");
                        }
                        //endregion
                        //region 第2辆车到站提示
                        if (secondBus == 0) {
                            tv_second_bus.setText("到站");
                        } else if (secondBus == 1) {
                            tv_second_bus.setText("将至");
                        } else if (secondBus > busList.getCount()) {
                            tv_second_bus.setText("无");
                        } else {
                            tv_second_bus.setText(secondBus + "站");
                        }
                        //endregion

                        //endregion

                        busList.notifyDataSetChanged();


                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                    break;
                //endregion

            }
        }
    };
    //endregion

    public void setBus(String b) {
        bus = b;
        init();
    }

    public void setBus(int d) {
        direction = d;
        init();
    }

    public void setBus(String b, int d) {
        bus = b;
        direction = d;
        init();
    }

    public BusMonitorItem(@NonNull Context context) {
        super(context);
    }

    public BusMonitorItem(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.view_bus_monitor_item, this);

        //region 控件初始化
        ivRefresh = findViewById(R.id.iv_refresh);
        tvBus = findViewById(R.id.tv_bus);
        tvStationStartEnd = findViewById(R.id.tv_station);
        tvStationTime = findViewById(R.id.tv_station_time);
        tv_first_bus = findViewById(R.id.tv_first_bus);
        tv_second_bus = findViewById(R.id.tv_second_bus);

        //region 底部按钮
        llRefresh = findViewById(R.id.ll_refresh);
        llDirection = findViewById(R.id.ll_direction);

        llRefresh.setOnClickListener(llClickListener);
        llDirection.setOnClickListener(llClickListener);
        //endregion


        //region 刷新icon旋转动画效果
        objectAnimator = ObjectAnimator.ofFloat(ivRefresh, "rotation", 0f, 360f);//添加旋转动画，旋转中心默认为控件中点
        objectAnimator.setDuration(800);//设置动画时间
        objectAnimator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
        objectAnimator.setRepeatCount(1);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                Log.d(Tag, "onAnimationStart"
                        + "start:" + objectAnimator.isStarted()
                        + "  pause:" + objectAnimator.isPaused()
                        + "  running:" + objectAnimator.isRunning());
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                Log.d(Tag, "onAnimationEnd"
                        + "start:" + objectAnimator.isStarted()
                        + "  pause:" + objectAnimator.isPaused()
                        + "  running:" + objectAnimator.isRunning());
                if (isRefresh) {
                    objectAnimator.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d(Tag, "onAnimationCancel"
                        + "start:" + objectAnimator.isStarted()
                        + "  pause:" + objectAnimator.isPaused()
                        + "  running:" + objectAnimator.isRunning());

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.d(Tag, "onAnimationRepeat"
                        + "start:" + objectAnimator.isStarted()
                        + "  pause:" + objectAnimator.isPaused()
                        + "  running:" + objectAnimator.isRunning());
            }
        });
        //endregion


        tvBus.setText(bus);
        tvStationStartEnd.setText("");
        tvStationTime.setText("");
        tv_first_bus.setText("无");
        tv_second_bus.setText("无");

        busList = findViewById(R.id.busList);
        busList.setDataList(mDataList);
        busList.notifyDataSetChanged();
        busList.setSelected(9999);
        busList.setOnItemClickListener(new BusList.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String data) {
                handler.sendEmptyMessage(1);
            }
        });
        //endregion


    }

    //region 点击事件
    View.OnClickListener llClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.ll_direction) {
                direction = (direction == 0 ? 1 : 0);
//                    busList.setSelected(busList.getCount()-busList.getSelected()-1);
                busList.setOpposite();
//                    break;

                handler.sendEmptyMessage(1);
            } else if (id == R.id.ll_refresh) {
                handler.sendEmptyMessage(1);
            }
        }
    };
    //endregion

    void init(){
        tvBus.setText(bus);
        tvStationStartEnd.setText("");
        tvStationTime.setText("");
        tv_first_bus.setText("无");
        tv_second_bus.setText("无");
        handler.sendEmptyMessage(1);
    }
}
