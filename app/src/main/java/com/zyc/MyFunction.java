package com.zyc;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyFunction {

    public static boolean writeFileInit() {
        try {
            File sdCardDir = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/BUS");
            if (!sdCardDir.exists()) sdCardDir.mkdir();
            sdCardDir = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/BUS/城际铁路纸坊东站to豹澥公交停车场");
            if (!sdCardDir.exists()) sdCardDir.mkdir();
            sdCardDir = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/BUS/豹澥公交停车场to城际铁路纸坊东站");
            if (!sdCardDir.exists()) sdCardDir.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean writeFileSdcard(String path, String fileName, String message) {
        try {
            File sdCardDir = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/BUS" + (path == null ? "" : ("/" + path)));
            if (!sdCardDir.exists()) {
                sdCardDir.mkdir();
            }
            // FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);
            FileOutputStream fout = new FileOutputStream(sdCardDir + "/" + fileName, true);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
            fout.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String writeFileSdcardUri() {
        String uri = null;
        try {
            File sdCardDir = new File(Environment.getExternalStorageDirectory().getCanonicalPath() + "/BB333s_Diag");
            if (!sdCardDir.exists()) {
                writeFileInit();
            }
            uri = sdCardDir.getPath().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;

    }


    public static String readFileSdcard(String uri) {

        try {
            // 如果手机插入了SD卡，而且应用程序具有访问SD的权限
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // 获取SD卡对应的存储目录
                File sdCardDir = Environment.getExternalStorageDirectory();
                // 获取指定文件对应的输入流
                FileInputStream fis = new FileInputStream(writeFileSdcardUri() + "/" + uri);
                // 将指定输入流包装成BufferedReader
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                StringBuilder sb = new StringBuilder("");
                String line = null;
                // 循环读取文件内容
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                // 关闭资源
                br.close();
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
