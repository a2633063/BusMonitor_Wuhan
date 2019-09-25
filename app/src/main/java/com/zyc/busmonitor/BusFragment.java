package com.zyc.busmonitor;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


@SuppressLint("ValidFragment")
public class BusFragment extends Fragment {
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bus, container, false);


        mDataList.clear();
        BusStation b;
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

        ivRefresh = view.findViewById(R.id.iv_refresh);
        tvBus = view.findViewById(R.id.tv_bus);
        tvStationStartEnd = view.findViewById(R.id.tv_station);
        tvStationTime = view.findViewById(R.id.tv_station_time);
        tv_first_bus = view.findViewById(R.id.tv_first_bus);
        tv_second_bus = view.findViewById(R.id.tv_second_bus);

        llRefresh = view.findViewById(R.id.ll_refresh);
        llDirection = view.findViewById(R.id.ll_direction);

        llRefresh.setOnClickListener(llClickListener);
        llDirection.setOnClickListener(llClickListener);


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
                ;
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


        tvBus.setText(bus);
        tvStationStartEnd.setText("");
        tvStationTime.setText("");
        tv_first_bus.setText("无");
        tv_second_bus.setText("无");

        busList = view.findViewById(R.id.busList);
        busList.setDataList(mDataList);
        busList.notifyDataSetChanged();
        busList.setSelected(30);
        handler.sendEmptyMessage(1);
//
//        String str="{\"resultCode\":\"1\",\"resultDes\":\"\",\"data\":{\"lineName\":\"703\",\"lineId\":\"027-703-1\",\"lineNo\":\"703\",\"direction\":1,\"startStopName\":\"珞喻东路高坡店\",\"endStopName\":\"汉口火车站\",\"firstTime\":\"5:30\",\"lastTime\":\"23:00\",\"intervalTime\":\"\",\"price\":\"1.0~2.0\",\"stopsNum\":31,\"measure\":0.0,\"beBus\":\"\",\"line2Id\":\"027-703-0\",\"stops\":[{\"stopId\":\"027-119\",\"stopName\":\"珞喻东路高坡店\",\"lng\":114.4629221428,\"lat\":30.50899574818192,\"stopOrder\":1,\"metro\":\"\"},{\"stopId\":\"027-217\",\"stopName\":\"珞喻东路长山\",\"stopOrder\":2,\"metro\":\"\"},{\"stopId\":\"027-1684\",\"stopName\":\"珞喻东路油篓口\",\"lng\":114.44476861785293,\"lat\":30.508421086858416,\"stopOrder\":3,\"metro\":\"\"},{\"stopId\":\"027-2769\",\"stopName\":\"珞喻东路森林公园\",\"lng\":114.43904772941026,\"lat\":30.505572734486808,\"stopOrder\":4,\"metro\":\"\"},{\"stopId\":\"027-867\",\"stopName\":\"珞喻东路佳园路\",\"lng\":114.43226364510153,\"lat\":30.5054603803958,\"stopOrder\":5,\"metro\":\"\"},{\"stopId\":\"027-2771\",\"stopName\":\"珞喻东路大黄村\",\"lng\":114.4275741939103,\"lat\":30.505674700059703,\"stopOrder\":6,\"metro\":\"\"},{\"stopId\":\"4201001100403354372411392\",\"stopName\":\"珞喻东路光谷大道\",\"lng\":1830.5617,\"lat\":0.0,\"stopOrder\":7,\"metro\":\"\"},{\"stopId\":\"027-2772\",\"stopName\":\"珞喻东路叶麻店\",\"lng\":114.41902340154655,\"lat\":30.506553002271446,\"stopOrder\":8,\"metro\":\"\"},{\"stopId\":\"027-508\",\"stopName\":\"珞喻路关山口\",\"lng\":114.41162777160059,\"lat\":30.507216913488463,\"stopOrder\":9,\"metro\":\"\"},{\"stopId\":\"4201001164428030492545024\",\"stopName\":\"珞喻路珞雄路\",\"lng\":1830.5817,\"lat\":0.0,\"stopOrder\":10,\"metro\":\"\"},{\"stopId\":\"027-648\",\"stopName\":\"珞喻路光谷广场\",\"lng\":114.40057926222842,\"lat\":30.50562377905755,\"stopOrder\":11,\"metro\":\"\"},{\"stopId\":\"027-2118\",\"stopName\":\"珞喻路鲁巷\",\"lng\":114.3959353301365,\"lat\":30.50732862893219,\"stopOrder\":12,\"metro\":\"\"},{\"stopId\":\"027-1743\",\"stopName\":\"珞喻路吴家湾\",\"lng\":114.39038050832441,\"lat\":30.511976372931475,\"stopOrder\":13,\"metro\":\"\"},{\"stopId\":\"027-I-3256\",\"stopName\":\"珞喻路科技会展中心\",\"lng\":114.3838910355411,\"lat\":30.515921969644914,\"stopOrder\":14,\"metro\":\"\"},{\"stopId\":\"027-2029\",\"stopName\":\"珞喻路马家庄\",\"lng\":114.37826763631153,\"lat\":30.517274742554193,\"stopOrder\":15,\"metro\":\"\"},{\"stopId\":\"027-I-3350\",\"stopName\":\"珞喻路卓刀泉中学\",\"lng\":114.37270626090243,\"lat\":30.518716142426243,\"stopOrder\":16,\"metro\":\"\"},{\"stopId\":\"027-710\",\"stopName\":\"珞喻路广埠屯\",\"lng\":114.360196,\"lat\":30.524635,\"stopOrder\":17,\"metro\":\"\"},{\"stopId\":\"027-18\",\"stopName\":\"武珞路街道口\",\"lng\":114.34608460016322,\"lat\":30.528860846888417,\"stopOrder\":18,\"metro\":\"\"},{\"stopId\":\"027-3313\",\"stopName\":\"武珞路地铁宝通寺站\",\"lng\":114.34089771462524,\"lat\":30.53020945617543,\"stopOrder\":19,\"metro\":\"\"},{\"stopId\":\"027-221\",\"stopName\":\"武珞路丁字桥\",\"stopOrder\":20,\"metro\":\"\"},{\"stopId\":\"027-1435\",\"stopName\":\"武珞路傅家坡客运站\",\"lng\":114.32732920082893,\"lat\":30.53601503269767,\"stopOrder\":21,\"metro\":\"\"},{\"stopId\":\"027-1719\",\"stopName\":\"武珞路大东门\",\"lng\":114.32184552855647,\"lat\":30.538662940721494,\"stopOrder\":22,\"metro\":\"\"},{\"stopId\":\"027-1140\",\"stopName\":\"武珞路阅马场\",\"lng\":114.30558998409666,\"lat\":30.541130039734643,\"stopOrder\":23,\"metro\":\"\"},{\"stopId\":\"027-91596\",\"stopName\":\"鹦鹉大道地铁琴台站\",\"lng\":114.26631714076598,\"lat\":30.558470462555288,\"stopOrder\":24,\"metro\":\"\"},{\"stopId\":\"027-863\",\"stopName\":\"武胜路泰合广场\",\"lng\":114.26879088303511,\"lat\":30.571910301754052,\"stopOrder\":25,\"metro\":\"\"},{\"stopId\":\"027-I-3300\",\"stopName\":\"青年路地铁青年路站\",\"lng\":114.26476294130813,\"lat\":30.58521012426967,\"stopOrder\":26,\"metro\":\"\"},{\"stopId\":\"027-568\",\"stopName\":\"青年路雪松路\",\"lng\":114.26321707427786,\"lat\":30.590775483682723,\"stopOrder\":27,\"metro\":\"\"},{\"stopId\":\"027-683\",\"stopName\":\"青年路机场河\",\"lng\":114.26308465718017,\"lat\":30.600089031997705,\"stopOrder\":28,\"metro\":\"\"},{\"stopId\":\"027-I-3272\",\"stopName\":\"青年路地铁范湖站\",\"lng\":114.26022630057632,\"lat\":30.605873602539987,\"stopOrder\":29,\"metro\":\"\"},{\"stopId\":\"027-1296\",\"stopName\":\"青年路市博物馆\",\"lng\":114.25728584840999,\"lat\":30.607083925575076,\"stopOrder\":30,\"metro\":\"\"},{\"stopId\":\"027-1569\",\"stopName\":\"汉口火车站\",\"stopOrder\":31,\"metro\":\"\"}],\"buses\":[\"21680|11|24|0|114.30317749217167|30.543705817506076\",\"21679|11|31|0|114.23938907394837|30.607810158543348\",\"21689|11|31|0|114.25843809773063|30.61022006510892\",\"21700|11|31|0|114.23926258898861|30.60792361190891\"]}}";
//        Message msg = new Message();
//        msg.what = 2;
//        msg.obj = str;
//        handler.sendMessageDelayed(msg, 200);// 执行耗时的方法之后发送消给handler


        return view;
    }

    //region 点击事件
    View.OnClickListener llClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_direction:
                    direction = (direction == 0 ? 1 : 0);
//                    busList.setSelected(busList.getCount()-busList.getSelected()-1);
                    busList.setOpposite();
//                    break;
                case R.id.ll_refresh:
                    handler.sendEmptyMessage(1);
                    break;
            }
        }
    };
    //endregion


}
