package com.zyc.busmonitor;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.zyc.MyFunction;
import com.zyc.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public final static String Tag = "MainActivity";
    ArrayList<Integer> listInt = new ArrayList<>();
    TextView log;
    //region Handler
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                //region 返回数据
                case 1:
                    String result = (String) msg.obj;
                    try {
                        //region 登录返回空数据
                        if (result == null) {
                            throw new JSONException("无数据返回");
                        }
                        //endregion
//                        Log.d(Tag, "result:" + result);

                        JSONObject jsonObject = new JSONObject(result);

                        if (jsonObject.has("resultCode") && jsonObject.getInt("resultCode") == 1
                                && jsonObject.has("data")
                        ) {
                            JSONObject jsonData = jsonObject.getJSONObject("data");
                            if (jsonData.has("buses")) {
                                int direction = jsonData.getInt("direction");
                                String startStopName = jsonData.getString("startStopName");
                                String endStopName = jsonData.getString("endStopName");


                                JSONArray jsonBuses = jsonData.getJSONArray("buses");
                                for (int i = 0; i < jsonBuses.length(); i++) {
                                    String str = jsonBuses.getString(i);
                                    Log.d(Tag, str);
                                    String[] arr = str.split("\\|");
                                    if (arr.length != 6) throw new JSONException("数据错误");
                                    int id = Integer.valueOf(arr[0]);
                                    int busStation = Integer.valueOf(arr[2]);
                                    int isStation = Integer.valueOf(arr[3]);
//                                    Log.d(Tag, "车辆" + id + "站点:" + busStation + "是否到站:" + isStation);

                                    if (isStation == 1) {
                                        if (!listInt.contains((Object) id)) {
                                            listInt.add(id);

                                            Date date = new Date();
                                            String currentDateandDate = new SimpleDateFormat("yyyyMMdd").format(date);
                                            String currentDateandTime = new SimpleDateFormat("HH:mm:ss").format(date);
                                            Log.d(Tag, id + "时间:" + currentDateandTime + "到站:" + busStation);
                                            MyFunction.writeFileSdcard(startStopName + "to" + endStopName + "/" + currentDateandDate, busStation + ".txt", currentDateandTime + "\r\n");
                                            log.setText("正在更新数据....");
                                        }
                                    } else {
                                        if (listInt.contains(id))
                                            try {
                                                listInt.remove((Object) id);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                    }

                                }
                            }

                        } else
                            throw new JSONException("更新数据失败");
                            log.setText("更新数据成功");

                    } catch (JSONException e) {
                        e.printStackTrace();
                        log.setText("更新数据失败");
                    }


                    break;
                //endregion
                case 100:
                    /* 开启一个新线程，在新线程里执行耗时的方法 */
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = WebService.WebConnect("http://bus.wuhancloud.cn:9087/website/web/420100/line/027-907-1.do");
                            handler.sendMessageDelayed(msg, 0);// 执行耗时的方法之后发送消给handler
                        }

                    }).start();
                    handler.sendEmptyMessageDelayed(101, 6000);
                    break;
                case 101:
                    /* 开启一个新线程，在新线程里执行耗时的方法 */
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = WebService.WebConnect("http://bus.wuhancloud.cn:9087/website/web/420100/line/027-907-0.do");
                            handler.sendMessageDelayed(msg, 0);// 执行耗时的方法之后发送消给handler
                        }

                    }).start();
                    handler.sendEmptyMessageDelayed(100, 6000);
                    break;
            }
        }
    };
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        handler.sendEmptyMessageDelayed(100, 0);
//        writeFileInit();

        log = findViewById(R.id.log);

        BusFragment prefFragment = null;
        prefFragment = new BusFragment("907",1);
        //加载PrefFragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        prefFragment = new ButtonSettingFragment(deviceNum);
        transaction.add(R.id.frameLayout, prefFragment);
        transaction.commit();

    }
}
