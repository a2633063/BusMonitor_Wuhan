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


    private BusList busList;
    private BusLine bus = null;
    private List<BusStation> mDataList = new ArrayList<>();

    private boolean scrollToSelectedFlag = true;
    //region Handler
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                //region 请求获取公交数据
                case 1:
                    if (!objectAnimator.isStarted()) objectAnimator.start();
                    isRefresh = true;
                    /* 开启一个新线程，在新线程里执行耗时的方法 */
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String url = String.format(getResources().getString(R.string.url_bus), bus.getLineId());
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

                        //region 返回无效数据!
                        if (!jsonObject.has("resultCode")
                                || !jsonObject.has("data")
                                || !jsonObject.getString("resultCode").equals("1")
                        ) {
                            throw new JSONException("更新数据失败");
                        }
                        //endregion
                        JSONObject jsonData = jsonObject.getJSONObject("data");
                        JSONArray jsonStops = jsonData.getJSONArray("stops");

                        //region 更新车辆信息
                        bus.setLineName(jsonData.getString("lineName"));
                        bus.setLineId(jsonData.getString("lineId"));
                        bus.setLineNo(jsonData.getString("lineNo"));
//                        bus.setDirection(jsonData.getInt("direction"));
                        bus.setStartStopName(jsonData.getString("startStopName"));
                        bus.setEndStopName(jsonData.getString("endStopName"));
                        bus.setFirstTime(jsonData.getString("firstTime"));
                        bus.setLastTime(jsonData.getString("lastTime"));
                        bus.setPrice(jsonData.getString("price"));
                        bus.setLine2Id(jsonData.getString("line2Id"));
                        bus.setStopsNum(jsonStops.length());

                        if (bus.getSelected() >= bus.getStopsNum()) {
                            bus.setSelected(bus.getStopsNum() - 1);
                        }
                        //endregion

                        if (bus.getLine2Id() != null && bus.getLine2Id().length() > 0) {
                            llDirection.setVisibility(VISIBLE);
                        } else {
                            llDirection.setVisibility(INVISIBLE);
                        }

                        //region UI更新车辆信息 站点信息
                        tvBus.setText(bus.getLineName());
                        tvStationStartEnd.setText(bus.getStartStopName() + " → " + bus.getEndStopName());
                        tvStationTime.setText(bus.getStopsNum() + "站  "
                                + bus.getFirstTime() + "-" + bus.getLastTime()
                                + "  " + bus.getPrice() + (bus.getPrice().endsWith("元") ? "" : "元")
                        );
                        tvStationStartEnd.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        tvStationStartEnd.setSingleLine(true);
                        tvStationStartEnd.setSelected(true);
                        tvStationStartEnd.setFocusable(true);
                        tvStationStartEnd.setFocusableInTouchMode(true);
                        //endregion

                        busList.clear();
                        //region 更新车站信息
                        for (int i = 0; i < jsonStops.length(); i++) {
                            BusStation b = new BusStation(jsonStops.getJSONObject(i).getString("stopName"));
                            b.setMetro(jsonStops.getJSONObject(i).getString("metro"));
                            busList.addBusStation(b);
                        }
                        if (bus.getSelected() < 0 || bus.getSelected() >= busList.getCount()) {
                            bus.setSelected(busList.getCount() - 1);
                        }
                        busList.setSelected(bus.getSelected());

                        //endregion

                        //region 更新车辆所有实时信息
                        int firstBus = 0;
                        int secondBus = 0;
                        firstBus = 9999;
                        secondBus = 9999;
                        try {

                            int selectBus = bus.getSelected();

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
                                int air_double = Integer.valueOf(arr[1]);
                                Log.d(Tag, "车辆" + id + "站点:" + busStation + "是否到站:" + isStation);

                                busStation = busStation - 1;
                                Log.d(Tag, "站点:" + busList.getItem(busStation).getName());
                                //region 更新车辆到站信息
                                if (isStation == 1) {//到站
                                    busList.getItem(busStation).setArrive(busList.getItem(busStation).getArrive() + 1);
                                    if (air_double / 10 == 2) {//是否为双层车
                                        busList.getItem(busStation).setArriveDoubleDeck(busList.getItem(busStation).getArriveDoubleDeck() + 1);
                                    }
                                    if (air_double % 10 == 2) {//是否为空调车
                                        busList.getItem(busStation).setArriveAirConditioner(busList.getItem(busStation).getArriveAirConditioner() + 1); //是否为空调车
                                    }
                                } else {//未到站
                                    busStation = busStation - 1;
                                    busList.getItem(busStation).setPass(busList.getItem(busStation).getPass() + 1);
                                    if (air_double / 10 == 2) {//是否为双层车
                                        busList.getItem(busStation).setPassDoubleDeck(busList.getItem(busStation).getPassDoubleDeck() + 1);
                                    }
                                    if (air_double % 10 == 2) {//是否为空调车
                                        busList.getItem(busStation).setPassAirConditioner(busList.getItem(busStation).getPassAirConditioner() + 1); //是否为空调车
                                    }
                                }

                                //endregion
                                Log.d(Tag, "busStation:" + busStation);
                                //region 到站剩余站更新
                                //region 计算到站站数
                                if (busStation == selectBus) {
                                    if (isStation == 1) {//到站
                                        secondBus = firstBus;
                                        firstBus = 0;
                                    }
                                } else if (busStation == selectBus - 1) {
                                    if (isStation == 0) {//wei到站
                                        secondBus = firstBus;
                                        firstBus = 1;
                                    } else if (firstBus > 1) {
                                        secondBus = firstBus;
                                        firstBus = 2;
                                    }
                                } else if (busStation < selectBus) {
                                    int temp = selectBus - busStation;
                                    if (temp < firstBus) {
                                        secondBus = firstBus;
                                        firstBus = temp;
                                    } else if (temp < selectBus)
                                        secondBus = temp;
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

                        if (scrollToSelectedFlag) {
                            busList.scrollToSelected();
                            scrollToSelectedFlag = false;
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        tvErr.setVisibility(VISIBLE);
                        tvErr.setText("获取数据失败\r\n请刷新重试");
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

    public BusLine getBus() {
        return bus;
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
            public void onItemClick(View view, int position, String stationName) {
                bus.setSelected(position);
                refresh();
                if (isAutoRefresh) setAutoRefresh(AutoRefresh);
                if (onBusStationClickListener != null)
                    onBusStationClickListener.onBusStationClick(view, position, stationName);
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

                if (bus.getLine2Id() != null && bus.getLine2Id().length() > 0) {
//                    bus.setDirection(bus.getDirection() == 0 ? 1 : 0);
//                    busList.setSelected(busList.getCount()-busList.getSelected()-1);
                    String temp = bus.getLineId();
                    bus.setLineId(bus.getLine2Id());
                    bus.setLine2Id(temp);
                    bus.setSelected(bus.getStopsNum() - bus.getSelected() - 1);
                    busList.setOpposite();
//                    break;
                    scrollToSelectedFlag = true;
                    refresh();
                    if (isAutoRefresh) setAutoRefresh(AutoRefresh);
                } else {

                }
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

    //region 单击回调事件
    //点击 RecyclerView 某条的监听
    public interface OnBusStationClickListener {
        /**
         * 当RecyclerView某个被点击的时候回调
         *
         * @param view        点击item的视图
         * @param position    车站序号
         * @param stationName 点击得到的数据
         */
        void onBusStationClick(View view, int position, String stationName);
    }

    private OnBusStationClickListener onBusStationClickListener;

    public void setOnBusStationClickListener(OnBusStationClickListener onBusStationClickListener) {
        this.onBusStationClickListener = onBusStationClickListener;
    }
    //endregion

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

}
