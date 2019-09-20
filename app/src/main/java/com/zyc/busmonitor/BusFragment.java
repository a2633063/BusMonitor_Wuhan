package com.zyc.busmonitor;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


@SuppressLint("ValidFragment")
public class BusFragment extends Fragment {
    public final static String Tag = "BusFragment";

    //region Description
    BusList busList;
    TextView tvBus;
    TextView tvStationStartEnd;
    TextView tvStationTime;
    TextView tv_first_bus;
    TextView tv_second_bus;

    LinearLayout llRefresh;
    LinearLayout llDirection;
    //endregion


    private List<BusStation> mDataList = new ArrayList<>();

    private String bus = null;
    private int direction = 0;

    public BusFragment(String b, int d) {
        bus = b;
        direction = d;
    }

    //region Handler
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                //region 获取公交数据
                case 1:
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
                        //endregion

                        //region 更新车辆所有实时信息
                        int firstBus = 9999;
                        int secondBus = 9999;
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
                            int seclectBus = busList.getSelected();

                            if (busStation == seclectBus + 1) {
                                if (isStation == 1) {//到站
                                    firstBus = 0;
                                } else {
                                    firstBus = 1;
                                }
                            } else if (busStation < seclectBus + 1) {
                                int temp = seclectBus + 1 - busStation;
                                if (temp < firstBus) {
                                    firstBus = temp;
                                    seclectBus = firstBus;
                                } else if (temp < seclectBus) seclectBus = temp;
                            }
                            //endregion
                            //region 第1辆车到站提示
                            if (firstBus == 0) {
                                tv_first_bus.setText("到站");
                            } else if (firstBus == 1) {
                                tv_first_bus.setText("将至");
                            } else if(firstBus>busList.getChildCount()){
                                tv_first_bus.setText("无");
                            }else{
                                tv_first_bus.setText(firstBus+"站");
                            }
                            //endregion
                            //region 第2辆车到站提示
                            if (seclectBus == 0) {
                                tv_second_bus.setText("到站");
                            } else if (seclectBus == 1) {
                                tv_second_bus.setText("将至");
                            } else if(seclectBus>busList.getChildCount()){
                                tv_second_bus.setText("无");
                            }else{
                                tv_second_bus.setText(seclectBus+"站");
                            }
                            //endregion
                            //endregion
                        }
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bus, container, false);


        mDataList.clear();
        BusStation b;
        b = new BusStation("高");
        mDataList.add(b);
        b = new BusStation("高新");
        b.setArrive(1);
        mDataList.add(b);
        b = new BusStation("高新六");
        mDataList.add(b);
        b = new BusStation("高新六路");
        mDataList.add(b);
        b = new BusStation("高新六路流");
        mDataList.add(b);
        b = new BusStation("高新六路流芳");
        b.setPass(3);
        mDataList.add(b);
        b = new BusStation("高新六路流芳大");
        mDataList.add(b);
        b = new BusStation("高新六路流芳大道");
        mDataList.add(b);
        b = new BusStation("高新六路AVV流芳大道高");
        mDataList.add(b);
        b = new BusStation("高新六A路流芳大道高高");
        mDataList.add(b);
        b = new BusStation("高新六AV路流芳大道高高高");
        mDataList.add(b);
        b = new BusStation("高新六路汪田村");
        mDataList.add(b);
        b = new BusStation("高新六路康一路");
        mDataList.add(b);
        b = new BusStation("高新六路佛祖岭一路");
        mDataList.add(b);
        b = new BusStation("高新六路光谷三路");
        mDataList.add(b);
        b = new BusStation("光谷三路高新四路");
        mDataList.add(b);
        b = new BusStation("光谷三路大吕村");
        mDataList.add(b);
        b = new BusStation("高新二路大吕路");
        mDataList.add(b);
        b = new BusStation("高新二路光谷四路");
        mDataList.add(b);
        b = new BusStation("高新二路驿山南路");
        mDataList.add(b);
        b = new BusStation("高新二路光谷六路");
        mDataList.add(b);
        b = new BusStation("高新二路高科园路");
        b.setPass(1);
        mDataList.add(b);
        b = new BusStation("高新二路高科园二路");
        mDataList.add(b);
        b = new BusStation("高新二路光谷七路");
        mDataList.add(b);
        b = new BusStation("高新二路生物园路");
        mDataList.add(b);
        b = new BusStation("光谷八路蔡吴村");
        b.setArrive(1);
        mDataList.add(b);
        b = new BusStation("桥北路教师小区");
        b.setPass(2);
        mDataList.add(b);
        b = new BusStation("桥北路三眼桥");
        mDataList.add(b);
        b = new BusStation("豹澥公交停车场");
        mDataList.add(b);

        tvBus = view.findViewById(R.id.tv_bus);
        tvStationStartEnd = view.findViewById(R.id.tv_station);
        tvStationTime = view.findViewById(R.id.tv_station_time);
        tv_first_bus = view.findViewById(R.id.tv_first_bus);
        tv_second_bus = view.findViewById(R.id.tv_second_bus);

        llRefresh   =view.findViewById(R.id.ll_refresh);
        llDirection=view.findViewById(R.id.ll_direction);

        llRefresh.setOnClickListener(llClickListener);
                llDirection.setOnClickListener(llClickListener);

        tvBus.setText(bus);
        tvStationStartEnd.setText("");
        tvStationTime.setText("");
        tv_first_bus.setText("无");
        tv_second_bus.setText("无");

        busList = view.findViewById(R.id.busList);
        busList.setDataList(mDataList);
        busList.notifyDataSetChanged();

        handler.sendEmptyMessage(1);
        return view;
    }

    View.OnClickListener llClickListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.ll_direction:
                    direction=direction==0?1:0;
//                    break;
                case R.id.ll_refresh:
                    handler.sendEmptyMessage(1);
                    break;
            }
        }
    };


}
