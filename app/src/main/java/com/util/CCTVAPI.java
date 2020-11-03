package com.util;

import android.util.Log;

import com.bean.OrderItemsBean;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CCTVAPI {

    public static String GetInfoByName(String name,String param)
    {
        URL url = null;
        String result = "";
        try {
            url = new URL("http://192.168.43.142:448/api/EquipmentOrder/"+name);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");//大写
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setDoOutput(true);

            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(param.getBytes());

            if (urlConnection.getResponseCode() == 200) {
                InputStream inputStream = urlConnection.getInputStream();
                String json = dealResponseResult(inputStream);

                JsonObject jsonObject = (JsonObject) new JsonParser().parse(json.toLowerCase());

                String jsonObj = jsonObject.get("data").toString();

                result =  jsonObj;

            }
        } catch (Exception ex) {
            String _message = ex.getMessage();
            Log.d("tag",_message);
            ex.printStackTrace();
            result = _message;
        }
        return  result;
    }

    /*
     * Function  :   处理服务器的响应结果（将输入流转化成字符串）
     * Param     :   inputStream服务器的响应输入流
     */
    private static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }
}
