package com.cctv.device;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bean.OrderItemsBean;
import com.cctv.adapter.LoadAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class UnLoding extends AppCompatActivity {
    private ListView listView;
    private LoadAdapter adapter;
    List<OrderItemsBean> _items;
    EditText txt;
    String _all ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_loding);

        listView = (ListView) findViewById(R.id.listview);


        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    txt = findViewById(R.id.editTextTextPersonName);
                    //List<OrderItemsBean> _items = this.getApiInfo();
                    _items = UnLoding.progressInfo();

                    String _taskId = _items.get(0).getTaskid();
                    String _epc = _items.get(0).getEpcnum();


                    for(int i = 0;i<_items.size();i++)
                    {
                       // if(_epc!=_items.get(i).getEpcnum()&&(i!=0))
                       // {
                            _all = _all+ _items.get(i).getTaskname() + " " + _items.get(i).getEpcname()+" " + _items.get(i).getEpcnum()+"\n";
                        //}

                    }

                    //txt.setText(_all);
                   // adapter = new LoadAdapter(getLayoutInflater(),_items);
                   // adapter._cuttent = getLayoutInflater();
                   // adapter._items = _items;
                   // listView.setAdapter(adapter);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          //  txt.setText(_all);
                            adapter = new LoadAdapter(UnLoding.this,_items);
                            //adapter._cuttent = getLayoutInflater();
                            //adapter._items = _items;
                            listView.setAdapter(adapter);


                        }
                    });



                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally {

                }
            }
        }).start();
        //Gson gson = new Gson();

    }


    private List<OrderItemsBean> getApiInfo() throws IOException {
        URL url = new URL("http://192.168.43.155:448/api/EquipmentOrder/GetEquipmentOrderList");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream stream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream,
                "UTf-8"));
        String sread = null;
        StringBuffer buffer = new StringBuffer();
        while ((sread = bufferedReader.readLine()) != null) {
            buffer.append(sread);
            buffer.append("\r\n");
        }
        String info = buffer.toString();
        if (info != null && info.startsWith("\ufeff")) {
            info = info.substring(1);
        }

        Gson gson = new Gson();
        List<OrderItemsBean> list = gson.fromJson(info, new TypeToken<List<OrderItemsBean>>() {
        }.getType());

        return list;
    }

    public static List<OrderItemsBean> progressInfo() {
        URL url = null;
        try {
            url = new URL("http://192.168.43.142:448/api/EquipmentOrder/GetEquipmentOrderList");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");//大写
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setDoOutput(true);

            String param = "id=075611a4-884f-426e-acae-042f2d971c82";

            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(param.getBytes());

            if (urlConnection.getResponseCode() == 200) {
                InputStream inputStream = urlConnection.getInputStream();
                String json = dealResponseResult(inputStream);

                JsonObject jsonObject = (JsonObject) new JsonParser().parse(json.toLowerCase());

                String jsonObj = jsonObject.get("data").toString();
                Gson gson = new Gson();
                List<OrderItemsBean> base = gson.fromJson(jsonObj, new TypeToken<List<OrderItemsBean>>() {
                }.getType());
               // List<OrderItemsBean> result = base.getResult();

                return  base;

            }
        } catch (Exception ex) {
            String _message = ex.getMessage();
            Log.d("tag",_message);
            ex.printStackTrace();
        }
        return  null;
    }

    /*
     * Function  :   处理服务器的响应结果（将输入流转化成字符串）
     * Param     :   inputStream服务器的响应输入流
     */
    public static String dealResponseResult(InputStream inputStream) {
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