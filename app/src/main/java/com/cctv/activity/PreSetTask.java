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
import com.nativec.tools.ModuleManager;
import com.nativec.tools.SerialPort;
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
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;


public class PreSetTask extends AppCompatActivity {

    private TableRow mDropTask;
    private SpinerPopWindow mSpinerWindow;
    private TextView mTaskTextView;
    private int carNum = 2;
    private static final String defatTask = "请选择任务";

    private List<String> mTaskList = new ArrayList<String>();

    private String typeLoad="load" ;
    private String taskValue;

    private List<TaskBean> tasks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_set_task);
        ((UHFApplication) getApplication()).addActivity(this);
        Intent intent = getIntent();
        typeLoad = intent.getStringExtra("type");//装载方式
        SetView();
        SetData();
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

                if (taskValue.equals(defatTask)||taskValue=="no data") {
                    Toast.makeText(
                            getApplicationContext(),
                            "task not select",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                try {

                    Intent intent;
                    if(typeLoad=="load")
                    {
                        intent = new Intent().setClass(PreSetTask.this, CarLoadActivity.class);
                    }
                    else
                    {
                        intent = new Intent().setClass(PreSetTask.this, MainActivityList.class);
                    }
                    startActivity(intent);
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
}