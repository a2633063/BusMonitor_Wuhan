package com.zyc.busmonitoritem;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zyc.buslist.BusList;
import com.zyc.buslist.BusStation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BusMonitorItem extends LinearLayout {
    public final static String Tag = "BusFragment";

    //region 控件
    BusList busList;
    TextView tvBus;
    TextView tvStationStartEnd;
    TextView tvStationTime;
    TextView tvFirstBus;
    TextView tvSecondBus;
    TextView tvErr;

    LinearLayout llAutoRefresh;
    LinearLayout llRefresh;
    LinearLayout llDirection;

    ImageView ivRefresh;
    ImageView ivAutoRefresh;

    TextView tvAutoRefresh;
    //endregion
    private ObjectAnimator objectAnimator;
    private boolean isRefresh = false;
    private boolean isAutoRefresh = false;
    private int AutoRefresh = 15;


    private BusLine bus = null;
    private List<BusStation> mDataList = new ArrayList<>();


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
                            String url = String.format(getResources().getString(R.string.url_bus), bus.getLine() );
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
                        tvErr.setVisibility(GONE);
                        busList.setVisibility(VISIBLE);
                        //region 登录返回空数据
                        if (result == null) {
                            throw new JSONException("无数据返回");
                        }
                        //endregion
//                        Log.d(Tag, "result:" + result);

                        JSONObject jsonObject = new JSONObject(result);

                        if (!jsonObject.has("resultCode")
                                || !jsonObject.has("data")
                                || !jsonObject.getString("resultCode").equals("1")
                        ) {
                            throw new JSONException("更新数据失败");
                        }
                        JSONObject jsonData = jsonObject.getJSONObject("data");
                        JSONArray jsonStops = jsonData.getJSONArray("stops");

                        if(bus.getLineId()==null){
                            bus.setLineId(jsonData.getString("lineId"));
                        }else if(bus.getLine2Id()==null){
                            bus.setLine2Id(jsonData.getString("line2Id"));
                        }

                        int stopsNum =  jsonStops.length();
                        //region 更新车辆信息 站点信息
                        tvBus.setText(jsonData.getString("lineName"));
                        tvStationStartEnd.setText(jsonData.getString("startStopName")
                                + " → " + jsonData.getString("endStopName")
                                );
                        tvStationTime.setText(stopsNum + "站  "+jsonData.getString("firstTime")
                                + "-" + jsonData.getString("lastTime")
                                + "  " + jsonData.getString("price")
                                + (jsonData.getString("price").endsWith("元")?"":"元")
                        );
                        tvStationStartEnd.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        tvStationStartEnd.setSingleLine(true);
                        tvStationStartEnd.setSelected(true);
                        tvStationStartEnd.setFocusable(true);
                        tvStationStartEnd.setFocusableInTouchMode(true);
                        //endregion

                        //region 更新车站信息
                        busList.clear();
                        for (int i = 0; i < jsonStops.length(); i++) {
                            BusStation b = new BusStation(jsonStops.getJSONObject(i).getString("stopName"));
                            busList.addBusStation(b);
                        }

                        if (busList.getSelected() >= busList.getCount())
                            busList.setSelected(busList.getCount() - 1);
                        //endregion

                        //region 更新车辆所有实时信息
                        int firstBus = 0;
                        int secondBus = 0;
                        firstBus = 9999;
                        secondBus = 9999;
                        try {

                            int selectBus = busList.getSelected();

                            Log.d(Tag, "selectBus:" + selectBus);
                            JSONArray jsonBuses = jsonData.getJSONArray("buses");
                            for (int i = 0; i < jsonBuses.length(); i++) {
                                String str = jsonBuses.getString(i);
                                Log.d(Tag, str);
                                String[] arr = str.split("\\|");
                                if (arr.length != 6)
                                    break;//throw new JSONException("数据错误");   //能够获取到车站信息但无法获取到位置时 显示车站但不显示实时位置 需要验证
//                                int id = Integer.valueOf(arr[0]);
                                String id = arr[0];
                                int busStation = Integer.valueOf(arr[2]);
                                int isStation = Integer.valueOf(arr[3]);
                                Log.d(Tag, "车辆" + id + "站点:" + busStation + "是否到站:" + isStation);

                                busStation = busStation - 1;
                                Log.d(Tag, "站点:" + busList.getItem(busStation).getName());
                                //region 更新车辆到站信息
                                if (isStation == 1) {//到站
                                    busList.getItem(busStation).setArrive(busList.getItem(busStation).getArrive() + 1);
                                } else {//未到站
                                    busStation = busStation - 1;
                                    busList.getItem(busStation).setPass(busList.getItem(busStation).getPass() + 1);
                                }
                                //endregion
                                Log.d(Tag, "busStation:" + busStation);
                                //region 到站剩余站更新
                                //region 计算到站站数
                                if (busStation == selectBus) {
                                    if (isStation == 1) {//到站
                                        firstBus = 0;
                                    }
                                } else if (busStation == selectBus - 1) {
                                    if (isStation == 0) {//wei到站
                                        firstBus = 1;
                                    } else if (firstBus > 1) {
                                        firstBus = 2;
                                    }
                                } else if (busStation < selectBus) {
                                    int temp = selectBus - busStation;
                                    if (temp < firstBus) {
                                        secondBus = firstBus;
                                        firstBus = temp;
                                    } else if (temp < selectBus) secondBus = temp;
                                }
                                //endregion
                                //endregion
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            firstBus = 9999;
                            secondBus = 9999;
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            firstBus = 9999;
                            secondBus = 9999;
                        }


                        //region 第1辆车到站提示
                        if (firstBus == 0) {
                            tvFirstBus.setText("到站");
                        } else if (firstBus == 1) {
                            tvFirstBus.setText("将至");
                        } else if (firstBus > busList.getCount()) {
                            tvFirstBus.setText("无");
                        } else {
                            tvFirstBus.setText(firstBus + "站");
                        }
                        //endregion
                        //region 第2辆车到站提示
                        if (secondBus == 0) {
                            tvSecondBus.setText("到站");
                        } else if (secondBus == 1) {
                            tvSecondBus.setText("将至");
                        } else if (secondBus > busList.getCount()) {
                            tvSecondBus.setText("无");
                        } else {
                            tvSecondBus.setText(secondBus + "站");
                        }
                        //endregion

                        //endregion

                        busList.notifyDataSetChanged();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        tvErr.setVisibility(VISIBLE);
                        busList.setVisibility(GONE);
                    }

                    break;
                //endregion

            }
        }
    };
    //endregion

    public void setBus(BusLine b) {
        bus = b;
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
        tvErr = findViewById(R.id.tv_err);

        tvBus = findViewById(R.id.tv_bus);
        tvStationStartEnd = findViewById(R.id.tv_station);
        tvStationTime = findViewById(R.id.tv_station_time);
        tvFirstBus = findViewById(R.id.tv_first_bus);
        tvSecondBus = findViewById(R.id.tv_second_bus);

        //region 底部按钮
        tvAutoRefresh = findViewById(R.id.tv_auto_refresh);

        ivRefresh = findViewById(R.id.iv_refresh);
        ivAutoRefresh = findViewById(R.id.iv_auto_refresh);

        llAutoRefresh = findViewById(R.id.ll_auto_refresh);
        llRefresh = findViewById(R.id.ll_refresh);
        llDirection = findViewById(R.id.ll_direction);

        llAutoRefresh.setOnClickListener(llClickListener);
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

        busList = findViewById(R.id.busList);
        busList.setDataList(mDataList);
        busList.notifyDataSetChanged();
        busList.setSelected(-1);
        busList.setOnItemClickListener(new BusList.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String data) {
                refresh();
                if (isAutoRefresh) setAutoRefresh(AutoRefresh);
            }
        });
        //endregion

        tvErr.setVisibility(VISIBLE);
        busList.setVisibility(GONE);
        init();
    }

    //region 点击事件
    View.OnClickListener llClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.ll_direction) {
                bus.setDirection(bus.getDirection() == 0 ? 1 : 0);
//                    busList.setSelected(busList.getCount()-busList.getSelected()-1);
                busList.setOpposite();
//                    break;

                refresh();
                if (isAutoRefresh) setAutoRefresh(AutoRefresh);
            } else if (id == R.id.ll_refresh) {
                refresh();
                if (isAutoRefresh) setAutoRefresh(AutoRefresh);
            } else if (id == R.id.ll_auto_refresh) {
                setAutoRefresh(isAutoRefresh ? 0 : AutoRefresh);
            }
        }
    };
    //endregion

    void init() {
        if (bus != null)
            tvBus.setText(bus.getLineName());
        tvStationStartEnd.setText("");
        tvStationTime.setText("");
        tvFirstBus.setText("无");
        tvSecondBus.setText("无");
//        busList.setOnItemClickListener(new BusList.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position, String data) {
//                bus.setSelected(position);
////                refresh();
////                if (isAutoRefresh) setAutoRefresh(AutoRefresh);
//            }
//        });
        refresh();
    }

    public void refresh() {
        handler.sendEmptyMessage(1);
    }

    CountDownTimer timer;

    public void setAutoRefresh(int time) {

        if (time == 0) {
            isAutoRefresh = false;
            tvAutoRefresh.setText("自动");
            if (timer != null)
                timer.cancel();
        } else {
            isAutoRefresh = true;
            AutoRefresh = time;
            if (timer != null) timer.cancel();
            timer = new CountDownTimer(AutoRefresh * 1000 + 300, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tvAutoRefresh.setText(millisUntilFinished / 1000 + "s");
                }

                @Override
                public void onFinish() {
                    timer.cancel();
                    refresh();
                    timer.start();
                }
            };
            timer.start();
        }
        ivAutoRefresh.setImageResource(isAutoRefresh ? R.drawable.ic_auto_refresh_select_24dp : R.drawable.ic_auto_refresh_24dp);
        tvAutoRefresh.setTextColor(getResources().getColor(isAutoRefresh ? R.color.bottom_text_select_color : R.color.bottom_text_color));

    }

    public void setSelected(int pos){
        busList.setSelected(pos);
    }
    public int getSelect(){
        return busList.getSelected();
    }

}
