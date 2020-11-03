package com.cctv.device;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.OperateBean;
import com.rfid.RFIDReaderHelper;
import com.rfid.config.CMD;
import com.rfid.config.ERROR;
import com.rfid.rxobserver.RXObserver;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXOperationTag;
import com.cctv.device.R;
import com.cctv.UHFApplication;
import com.cctv.device.MainActivityList;
import com.cctv.adapter.AbstractSpinerAdapter.IOnItemSelectListener;
import com.cctv.dialogwin.SpinerPopWindow;
import com.cctv.widget.HexEditTextBox;
import com.cctv.widget.LogList;
import com.cctv.widget.TagAccessList;
import com.util.FormatUtils;
import com.util.StringTool;

import java.util.ArrayList;
import java.util.List;


public class PageTagAccess extends LinearLayout {

    //fixed by lei.li 2016/11/09
    //private LogList mLogList;
    private LogList mLogList;
    //fixed by lei.li 2016/11/09

    private TextView mGet, mRead, mSelect, mWrite, mLock, mKill;

    //private TextView mRefreshButton;

    private TextView mTagAccessListText;
    private TableRow mDropDownRow;

    private List<String> mAccessList;
    private SpinerPopWindow mSpinerPopWindow;

    private HexEditTextBox mPasswordEditText;
    private EditText mStartAddrEditText;
    private EditText mDataLenEditText;
    private HexEditTextBox mDataEditText;
    private HexEditTextBox mLockPasswordEditText;
    private HexEditTextBox mKillPasswordEditText;

    private RadioGroup mGroupAccessAreaType;
    private RadioGroup mGroupLockAreaType;
    private RadioGroup mGroupLockType;

    private TagAccessList mTagAccessList;

    private RFIDReaderHelper mReaderHelper;

    private int mPos = -1;

    private static ReaderSetting m_curReaderSetting;

    private Handler mHandler;
    private Context mContext;
    private UHFApplication mApp;

    public PageTagAccess(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        // LayoutInflater.from(context).inflate(R.layout.page_tag_access, this);
        mApp = (UHFApplication) ((Activity) context).getApplication();

        try {
            mReaderHelper = RFIDReaderHelper.getDefaultHelper();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mAccessList = new ArrayList<>();
        mHandler = new Handler();

        m_curReaderSetting = ReaderSetting.newInstance();

    }

    public void refresh() {
        mTagAccessListText.setText("");
        mPos = -1;

        mStartAddrEditText.setText("");
        mDataLenEditText.setText("");

        mApp.clearTags();

        //add by lei.li 2017/1/16
        mAccessList.clear();
        mAccessList.add("cancel");
        //add by lei.li 2017/1/16
        //mTagAccessList.clearData();
    }
}


