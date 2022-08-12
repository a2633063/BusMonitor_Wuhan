package com.zyc.busmonitoritem;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class WebService {


    public static String WebConnect(String uri) {
        String result;

        result = httpGet(uri);
        if (result == null) result = httpGet(uri);
        if (result == null) result = httpGet(uri);
        return result;
    }


    private static String httpGet(String str) {
        Log.v("WebService_Tag", str);
        String result = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(str);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1000);// 连接超时时间
            connection.setReadTimeout(1000);// 读取超时时间
            connection.setRequestMethod("GET");// 设置请求方式
            connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            connection.setRequestProperty("User-agent","Mozilla/5.0 (Linux; Android 10; NOH-AN00 Build/HUAWEINOH-AN00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/88.0.4324.93 Mobile Safari/537.36"); //设置"User-Agent
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
            connection.setRequestProperty("Referer","http://wbus.wuhancloud.cn/myAround.jsp");
            connection.setRequestProperty("Charset", "UTF-8");// 设置编码格式
            InputStream in = connection.getInputStream();
            //下面对获取到的输入流进行读取
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            result = response.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            result = null;
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

}  
