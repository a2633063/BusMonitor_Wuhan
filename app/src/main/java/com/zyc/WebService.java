package com.zyc;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class WebService {


    public static String WebConnect(String uri) {
        String result;

        result = httpGet(uri);
        if (result == null) result = httpGet(uri);
        return result;
    }
    public static String WebConnectPost(String uri,String str) {
        String result;

//        try {
//            str += URLEncoder.encode(str, "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            //e.printStackTrace();
//        }

        result = httpPost(uri,str);
        if (result == null) result = httpPost(uri,str);
        return result;
    }

    private static String httpGet(String str) {
        Log.v("WebService_Tag", str);
        String result = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(str);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");// 设置请求方式
            connection.setRequestProperty("Charset", "UTF-8");// 设置编码格式
            connection.setConnectTimeout(1000);// 连接超时时间
            connection.setReadTimeout(1000);// 读取超时时间
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

    public static String httpPost(String uri,String str){


        String result = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();//打开链接
            connection.setRequestMethod("POST");// 设置请求方式
            connection.setConnectTimeout(2000);// 连接超时时间
            connection.setReadTimeout(2000);// 读取超时时间

            connection.setRequestProperty("Charset", "UTF-8");// 设置编码格式
            // 设置请求的头
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            // 设置请求的头
            connection.setRequestProperty("Content-Length", String.valueOf(str.getBytes().length));

            connection.setDoOutput(true); // 发送POST请求必须设置允许输出
            connection.setDoInput(true); // 发送POST请求必须设置允许输入

            //获取输出流

            OutputStream os = connection.getOutputStream();
            os.write(str.getBytes());
            os.flush();

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
