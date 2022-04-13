package com.zyc.busmonitor.addbus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.zyc.WebService;
import com.zyc.busmonitor.R;
import com.zyc.busmonitoritem.BusLine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddBusActivity extends AppCompatActivity {
    public final static String Tag = "AddBusActivity";

    List<BusLine> mData = new ArrayList<>();
    private AddBusAdapter adapter;
    private ListView lv;

    private EditText editText;
    private ImageButton imgbtn_return;

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
                            String url = String.format(getResources().getString(R.string.url_search_bus), editText.getText());
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
                        //region 返回空数据
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
                        JSONArray jsonArray = jsonData.getJSONArray("lines");

                        if (jsonArray == null || jsonArray.length() == 0)
                            throw new JSONException("搜索无数据");
                        lv.setVisibility(View.VISIBLE);
                        mData.clear();
                        for (int i = 0; i < jsonArray.length() && i < 20; i++) {
                            JSONObject j = jsonArray.getJSONObject(i);
                            BusLine b = new BusLine(j.getString("lineName"), j.getString("lineNo"),-1);
                            b.setStartStopName(j.getString("startStopName"));
                            b.setEndStopName(j.getString("endStopName"));
                            b.setFirstTime(j.getString("firstTime"));
                            b.setLastTime(j.getString("lastTime"));
                            b.setLineId(j.getString("lineId"));
                            b.setLine2Id(null);
                            //b.setStopsNum(j.getInt("stopsNum"));
                            mData.add(b);
                        }
                        adapter.notifyDataSetChanged();
                        //region 更新车辆信息 站点信息

                        //endregion


                        //endregion

                        adapter.notifyDataSetChanged();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        lv.setVisibility(View.INVISIBLE);
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
        setContentView(R.layout.activity_add_bus);


        //region listview
        lv = findViewById(R.id.listview);
        adapter = new AddBusAdapter(this, mData);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //返回数据
                Intent intent = new Intent();
                intent.putExtra("busline", adapter.getItem(position));

                setResult(RESULT_OK, intent);

                finish();
            }
        });
        //endregion

        imgbtn_return = findViewById(R.id.imgbtn_return);
        imgbtn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        editText = findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeMessages(1);
            }

            @Override
            public void afterTextChanged(Editable s) {
                handler.sendEmptyMessageDelayed(1, 200);
            }
        });
    }
}
