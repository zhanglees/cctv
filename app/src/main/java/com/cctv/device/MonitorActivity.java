package com.cctv.device;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Context;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rfid.RFIDReaderHelper;
import com.rfid.bean.MessageTran;
import com.cctv.device.R;
import com.cctv.UHFApplication;
import com.cctv.widget.HexEditTextBox;
import com.util.Converter;
import com.util.PreferenceUtil;
import com.util.StringTool;

public class MonitorActivity extends BaseActivity {

    public static final String mIsChecked = "MonitorisOpen";

    private Context mContext;

    private TextView mDataSendButton;
    private HexEditTextBox mDataText;
    private HexEditTextBox mDataCheck;

    private CheckBox mCheckOpenMonitor;
    private TextView mRefreshButton;
    private ListView mMonitorList;
    private ArrayAdapter<CharSequence> mMonitorListAdapter;

    private RFIDReaderHelper mReaderHelper;

    private UHFApplication app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        app = (UHFApplication) getApplication();
        app.addActivity(this);
        mContext = this;

        try {
            mReaderHelper = RFIDReaderHelper.getDefaultHelper();
        } catch (Exception e) {
            return;
        }

        mCheckOpenMonitor = (CheckBox) findViewById(R.id.check_open_monitor);
        if (PreferenceUtil.getBoolean(mIsChecked, false)) {
            mCheckOpenMonitor.setChecked(true);
        }

        mCheckOpenMonitor.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceUtil.commitBoolean(mIsChecked, true);
                } else {
                    PreferenceUtil.commitBoolean(mIsChecked, false);
                }
            }
        });


        mMonitorList = (ListView) findViewById(R.id.monitor_list_view);

        mMonitorListAdapter = new ArrayAdapter<CharSequence>(mContext, R.layout.monitor_list_item, app.mMonitorListItem);

        mMonitorList.setAdapter(mMonitorListAdapter);

        refreshMonitor();

        mRefreshButton = (TextView) findViewById(R.id.refresh);
        mRefreshButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mDataText.setText("");
                app.mMonitorListItem.clear();
                mMonitorListAdapter.notifyDataSetChanged();
                //mReader.refreshBuffer();
            }
        });

        mDataText = (HexEditTextBox) findViewById(R.id.data_send_text);

        mDataText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                String[] result = StringTool.stringToStringArray(mDataText.getText().toString().toUpperCase(), 2);
                try {
                    byte[] buf = StringTool.stringArrayToByteArray(result, result.length);
                    MessageTran msgTran = new MessageTran();
                    byte check = msgTran.checkSum(buf, 0, buf.length);
                    mDataCheck.setText("" + Converter.byteToHex((int) (check & 0xFF) / 16) + Converter.byteToHex((check & 0xFF) % 16));
                } catch (Exception e) {
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });

        mDataSendButton = (TextView) findViewById(R.id.send);
        mDataSendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String cmd = mDataText.getText().toString().toUpperCase() + mDataCheck.getText().toString().toUpperCase();
                String[] result = StringTool.stringToStringArray(cmd, 2);
                if (result != null && result.length > 0) {
                    mReaderHelper.sendCommand(StringTool.stringArrayToByteArray(result, result.length));
                } else {
                    Toast.makeText(mContext, "Command not allow empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mDataCheck = (HexEditTextBox) findViewById(R.id.data_send_check);

    }

    @Override
    protected void onStart() {
        super.onStart();
        app.setOnRefreshMonitor(new UHFApplication.OnRefreshMonitorCallback() {
            @Override
            public void onRefresh() {
                refreshMonitor();
            }
        });
    }

    @Override
    protected void onResume() {
        mReaderHelper.startWith();
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        app.setOnRefreshMonitor(null);
    }

    public final void refreshMonitor() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!mCheckOpenMonitor.isChecked()) return;
                mMonitorListAdapter.notifyDataSetChanged();
            }
        });
    }

}