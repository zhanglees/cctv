package com.cctv;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.rfid.config.ERROR;
import com.cctv.device.MainActivity;
import com.util.BeeperUtils;
import com.util.PreferenceUtil;

import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UHFApplication extends Application {

    //add by lei.li 2016/11/12
    private static Context mContext;
    //add by lei.li 2016/11/12

    private Socket mTcpSocket = null;
    private BluetoothSocket mBtSocket = null;
    private OnRefreshMonitorCallback mOnRefreshMonitorCallback;

    public ArrayList<CharSequence> mMonitorListItem = new ArrayList<CharSequence>();

    private List<String> mTags = new ArrayList<>();

    public final void writeMonitor(String strLog, int type) {
        Date now = new Date();
        SimpleDateFormat temp = new SimpleDateFormat("kk:mm:ss:SSS");
        SpannableString tSS = new SpannableString(temp.format(now) + ":\n"
                + strLog);
        tSS.setSpan(new ForegroundColorSpan(type == ERROR.SUCCESS ? Color.BLACK
                        : Color.RED), 0, tSS.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mMonitorListItem.add(tSS);
        if (mMonitorListItem.size() > 1000)
            mMonitorListItem.remove(0);

        if (mOnRefreshMonitorCallback != null) {
            mOnRefreshMonitorCallback.onRefresh();
        }
    }

    private List<Activity> activities = new ArrayList<Activity>();

    public void addTag(String tag) {
        if (!mTags.contains(tag)) {
            mTags.add(tag);
        }
    }

    public List<String> getTags() {
        return mTags;
    }

    public void clearTags() {
        mTags.clear();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        PreferenceUtil.init(mContext);
        BeeperUtils.init(mContext);

		/*CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());*/
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //ControlGPIO.newInstance().JNIwriteGPIO(0);
        for (Activity activity : activities) {
            try {
                activity.finish();
            } catch (Exception e) {
                ;
            }
        }

        try {
            if (mTcpSocket != null)
                mTcpSocket.close();
            if (mBtSocket != null)
                mBtSocket.close();
        } catch (IOException e) {
        }

        mTcpSocket = null;
        mBtSocket = null;
        mOnRefreshMonitorCallback = null;
        if (MainActivity.mSerialPort != null) {
            Log.e("close serial", "serial");
            MainActivity.mSerialPort.close();
        }

        if (BluetoothAdapter.getDefaultAdapter() != null)
          //  BluetoothAdapter.getDefaultAdapter().disable();
        BeeperUtils.release();
        System.exit(0);
    }

    ;

    public void setTcpSocket(Socket socket) {
        this.mTcpSocket = socket;
    }

    public void setBtSocket(BluetoothSocket socket) {
        this.mBtSocket = socket;
    }

    public static Context getContext() {
        return mContext;
    }

    public interface OnRefreshMonitorCallback {
        void onRefresh();
    }

    public void setOnRefreshMonitor(OnRefreshMonitorCallback callback) {
        this.mOnRefreshMonitorCallback = callback;
    }
}

