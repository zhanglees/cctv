package com.cctv.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.bean.OrderItemsBean;
import com.bean.TaskBean;
import com.cctv.UHFApplication;
import com.cctv.adapter.AbstractSpinerAdapter;
import com.cctv.adapter.LoadAdapter;
import com.cctv.device.MainActivityList;
import com.cctv.device.R;
import com.cctv.device.UnLoding;
import com.cctv.dialog.SpinerPopWindow;
import com.cctv.adapter.AbstractSpinerAdapter.IOnItemSelectListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.module.interaction.ReaderHelper;
import com.nativec.tools.ModuleManager;
import com.nativec.tools.SerialPort;
import com.nativec.tools.SerialPortFinder;
import com.rfid.RFIDReaderHelper;
import com.rfid.ReaderDataPackageParser;
import com.rfid.ReaderDataPackageProcess;
import com.util.CCTVAPI;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class PreSetTask extends AppCompatActivity {

    private TableRow mDropTask;
    private SpinerPopWindow mSpinerWindow;
    private TextView mTaskTextView;
    private int carNum = 2;
    private static final String defatTask = "请选择任务";
    private static final String TTYS1 = "ttyS4 (rk_serial)";
    private List<String> mTaskList = new ArrayList<String>();

    private String typeLoad="load" ;
    private String taskValue;
    private ReaderHelper mReaderHelper;
    public static SerialPort mSerialPort = null;
    private List<TaskBean> tasks;
    private List<OrderItemsBean>  orderDatas;
    Intent intentinfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_set_task);
        ((UHFApplication) getApplication()).addActivity(this);
        Intent intent = getIntent();
        typeLoad = intent.getStringExtra("type");//装载方式
        SetView();
        SetData();
        setRfid();
    }

    public static List<TaskBean> progressInfo() {

        String funName = "GetTaskList";
        String arg = "getList";
        List<TaskBean> data = null;
        try
        {
            String jsonResult = CCTVAPI.GetInfoByName(funName,arg);
            Gson gson = new Gson();
            data = gson.fromJson(jsonResult, new TypeToken<List<TaskBean>>() {
            }.getType());
        }
        catch (Exception ex)
        {
            String _message = ex.getMessage();
            Log.d("tag",_message);
            ex.printStackTrace();
        }

        return  data;
    }

    public  void SetView()
    {
        TextView connectButton = (TextView) findViewById(R.id.textview_connect);
        mDropTask = (TableRow) findViewById(R.id.table_row_spiner_comport);
        mTaskTextView = (TextView) findViewById(R.id.comport_text);

        mTaskTextView.setText(defatTask);
        taskValue = defatTask;
        carNum = 2;

        mDropTask.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showPortSpinWindow();
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Log.i("conn", "enter conn");

                if (taskValue.equals(defatTask)) {
                    Toast.makeText(
                            getApplicationContext(),
                            "task not select",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                try {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //Optional<TaskBean> cartOptional = tasks.stream().filter(item -> item.getTasknum().equals(taskValue)).findFirst();
                            //if (cartOptional.isPresent()) {
                            // 存在
                            //     TaskBean cart =  cartOptional.get();
                            //} else {
                            // 不存在
                            //}
                            String taskId = "";
                            for(int i=0;i<tasks.size();i++)
                            {
                                TaskBean obj = tasks.get(i);
                                if(obj.getTasknum().equals(taskValue))
                                    taskId = obj.getId();break;
                            }
                            taskId = "id="+taskId;
                            orderDatas = PreSetTask.getLoadData(taskId);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {if (orderDatas!=null && orderDatas.size() > 0) {
                                    if(typeLoad.equals("load"))
                                    {
                                        intentinfo = new Intent().setClass(PreSetTask.this, CarLoadActivity.class);
                                    }
                                    else
                                    {
                                        intentinfo = new Intent().setClass(PreSetTask.this, MainActivityList.class);
                                    }

                                    intentinfo.putExtra("tasknum",taskValue);
                                    intentinfo.putExtra("orderitemsbean",(Serializable)orderDatas);
                                    startActivity(intentinfo);
                                }
                                else
                                {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "no order data!!!",
                                            Toast.LENGTH_SHORT).show();
                                }
                                }
                            });

                        }
                    }).start();

                    //finish();
                } catch (SecurityException e) {
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.error_security),
                            Toast.LENGTH_SHORT).show();
                } catch (InvalidParameterException e) {
                    Toast.makeText(
                            getApplicationContext(),
                            getResources().getString(R.string.error_configuration),
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void SetData() {
        mSpinerWindow = new SpinerPopWindow(this);
        new Thread(new Runnable() {
            @Override
            public void run() { try {
                    tasks = PreSetTask.progressInfo();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {if (tasks!=null && tasks.size() > 0) {
                                for (int i = 0; i < tasks.size(); i++) {

                                    String infoNum = tasks.get(i).getTasknum();
                                    Log.i("conn", infoNum);
                                    mTaskList.add(infoNum);
                                }
                            }
                            else
                            {
                                String info= "no data";
                                mTaskList.add(info);
                            }
                            mSpinerWindow.refreshData(mTaskList, 0);
                            mSpinerWindow.setItemListener(new IOnItemSelectListener() {
                                public void onItemClick(int pos) {
                                    setTaskText(pos);
                                }
                            });
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }
        }).start();
    }

    private void setTaskText(int pos) {
        if (pos >= 0 && pos < mTaskList.size()) {
            String value = mTaskList.get(pos);
            mTaskTextView.setText(value);
            taskValue = value;

        }
    }

    private void showPortSpinWindow() {
        mSpinerWindow.setWidth(mDropTask.getWidth());
        mSpinerWindow.showAsDropDown(mDropTask);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    /**
     * 电量低警告
     */
    private AlertDialog mWornDialog = null;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取当前电量,范围是 0～100
            int level = intent.getIntExtra("level", 0);
            if (level < 10) {
                if (mWornDialog == null) {
                    mWornDialog = new AlertDialog.Builder(PreSetTask.this)
                            .setCancelable(false)
                            .setTitle("警告")
                            .setMessage("当前电量低于10%")
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();

                } else if (!mWornDialog.isShowing()) {
                    mWornDialog.show();
                }
            }
        }
    };


    private void setRfid()
    {
        try {
            ((UHFApplication) getApplication()).addActivity(this);
            SerialPortFinder mSerialPortFinder = new SerialPortFinder();
            String[] entries = mSerialPortFinder.getAllDevices();
            String[] entryValues = mSerialPortFinder.getAllDevicesPath();
            List<String> mPortList = new ArrayList<String>();

            ModuleManager.newInstance().setUHFStatus(true);
            String[] lists = entries;
            for (int i = 0; i < lists.length; i++) {
                mPortList.add(lists[i]);
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ModuleManager.newInstance().setUHFStatus(false);
            int mPosPort = mPortList.indexOf(TTYS1.toString());
            //if (DEBUG)
            //Log.e(TAG, "test the value of mPosPort::" + mPosPort);

            if (mPosPort < 0) {
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.rs232_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            try {

                mSerialPort = new SerialPort(new File(entryValues[mPosPort]), 115200, 0);

               // if (DEBUG)
                 //   Log.e(TAG, "ttys1 value :::" + entryValues[0]);

                try {
                    mReaderHelper = RFIDReaderHelper.getDefaultHelper();
                    mReaderHelper.setReader(mSerialPort.getInputStream(), mSerialPort.getOutputStream(), new ReaderDataPackageParser(), new ReaderDataPackageProcess());
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (!ModuleManager.newInstance().setUHFStatus(true)) {
                    throw new RuntimeException("UHF RFID power on failure,may you open in other" +
                            " Process and do not exit it");
                }



                //finish();
            } catch (SecurityException e) {
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.error_security),
                        Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.error_unknown),
                        Toast.LENGTH_SHORT).show();
            } catch (InvalidParameterException e) {
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.error_configuration),
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (Exception ex) {

        }
    }


    private static List<OrderItemsBean> getLoadData(String taskid)
    {
        String funName = "GetEquipmentOrderList";
        String arg = taskid;
        List<OrderItemsBean> data = null;
        try
        {
            String jsonResult = CCTVAPI.GetInfoByName(funName,arg);
            Gson gson = new Gson();
            data = gson.fromJson(jsonResult, new TypeToken<List<OrderItemsBean>>() {
            }.getType());
        }
        catch (Exception ex)
        {
            String _message = ex.getMessage();
            Log.d("tag",_message);
            ex.printStackTrace();
        }

        return  data;
    }
}