package com.cctv.device;

import com.bean.InventoryBean;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.InventoryBean;
import com.rfid.RFIDReaderHelper;
import com.rfid.config.CMD;
import com.rfid.config.ERROR;
import com.rfid.rxobserver.RXObserver;
import com.rfid.rxobserver.ReaderSetting;
import com.rfid.rxobserver.bean.RXInventoryTag;
import com.cctv.device.R;
import com.cctv.device.R.id;
import com.cctv.device.R.layout;
import com.cctv.UHFApplication;
import com.cctv.device.MainActivityList;
import com.cctv.device.LodingList;
import com.cctv.adapter.AbstractSpinerAdapter.IOnItemSelectListener;
import com.cctv.dialogwin.SpinerPopWindow;
import com.cctv.widget.LogList;
import com.cctv.widget.TagRealList;
import com.util.BeeperUtils;
import com.util.FormatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PageInventoryReal extends LinearLayout {
    private static final String TAG = "PageInventory";

    // fixed by lei.li 2016/11/09
    // private LogList mLogList;
    private LogList mLogList;
    // fixed by lei.li 2016/11/09
    private TextView mStartStop;
    // private TextView mRefreshButton;
    //private LinearLayout mLayoutRealSet;
    private TextView mSessionIdTextView, mInventoriedFlagTextView;
    private TableRow mDropDownRow1, mDropDownRow2;
    //private CheckBox mCbRealSet, mCbRealSession;
    private List<String> mSessionIdList;
    private List<String> mInventoriedFlagList;
    private SpinerPopWindow mSpinerPopWindow1, mSpinerPopWindow2;
    private EditText mRealRoundEditText;
    private TagRealList mTagRealList;
    private RFIDReaderHelper mReaderHelper;
    private int mPos1 = 1, mPos2 = 0;
    private static ReaderSetting m_curReaderSetting;
    private TextView mTagsCountText, mTagsTotalText;
    private TextView mTagsSpeedText, mTagsTimeText, mTagsOpTimeText;
    private Context mContext;

    private RadioGroup mSelectCommand;
    /**
     * 是否是自定义选择模式
     */
    private boolean mIsCustom = true;
    private byte mBtRepeat, mBtSession, mBtTarget;
    /**
     * 是否循环盘存
     */
    private boolean mIsLoop = false;
    private UHFApplication mApp;
    private long mStartTime;
    private long mTotalTime;

    public PageInventoryReal(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mApp = (UHFApplication) ((Activity) context).getApplication();
        LayoutInflater.from(context)
                .inflate(layout.page_inventory_real, this);
        try {
            mReaderHelper = RFIDReaderHelper.getDefaultHelper();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mSessionIdList = new ArrayList<String>();
        mInventoriedFlagList = new ArrayList<String>();
        m_curReaderSetting = ReaderSetting.newInstance();

        mLogList = (LogList) findViewById(id.log_list);
        mStartStop = (TextView) findViewById(id.startstop);
        //mCbRealSet = (CheckBox) findViewById(id.check_real_set);
        //mLayoutRealSet = (LinearLayout) findViewById(id.layout_real_set);
        //mCbRealSession = (CheckBox) findViewById(id.check_real_session);

        mSessionIdTextView = findViewById(id.session_id_text);
        mInventoriedFlagTextView = findViewById(id.inventoried_flag_text);
        mDropDownRow1 = findViewById(id.table_row_session_id);
        mDropDownRow2 = findViewById(id.table_row_inventoried_flag);
        mTagsCountText = findViewById(id.tags_count_text);
        mTagsTotalText = findViewById(id.tags_total_text);
        mTagsSpeedText = findViewById(id.tags_speed_text);
        mTagsTimeText = findViewById(id.tags_time_text);
        mTagsOpTimeText = findViewById(id.tags_op_time_text);
        mTagRealList = findViewById(id.tag_real_list);
        mRealRoundEditText = findViewById(id.real_round_text);

        mStartStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == id.startstop) {
                    startStop(mStartStop.getText().toString()
                            .equals(getResources().getString(R.string.start_inventory)));
                }
            }
        });

        mDropDownRow1.setEnabled(mIsCustom);
        mDropDownRow2.setEnabled(mIsCustom);
        mDropDownRow1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showSpinWindow1();
            }
        });
        mDropDownRow2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showSpinWindow2();
            }
        });
        mSessionIdList.clear();
        mInventoriedFlagList.clear();
        String[] lists = getResources().getStringArray(R.array.session_id_list);
        mSessionIdList.addAll(Arrays.asList(lists));
        lists = getResources().getStringArray(R.array.inventoried_flag_list);
        mInventoriedFlagList.addAll(Arrays.asList(lists));

        mSpinerPopWindow1 = new SpinerPopWindow(mContext);
        mSpinerPopWindow1.refreshData(mSessionIdList, 1);
        mSpinerPopWindow1.setItemListener(new IOnItemSelectListener() {
            @Override
            public void onItemClick(int pos) {
                setSessionIdText(pos);
            }
        });

        mSpinerPopWindow2 = new SpinerPopWindow(mContext);
        mSpinerPopWindow2.refreshData(mInventoriedFlagList, 0);
        mSpinerPopWindow2.setItemListener(new IOnItemSelectListener() {
            public void onItemClick(int pos) {
                setInventoriedFlagText(pos);
            }
        });
        mRealRoundEditText.setText(String.valueOf(1));
        setSessionIdText(mPos1);
        setInventoriedFlagText(mPos2);

        mSelectCommand = findViewById(id.comand_select_radio);
        mSelectCommand.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean custom = checkedId == id.cmd_S0;
                mDropDownRow1.setEnabled(custom);
                mDropDownRow2.setEnabled(custom);
            }
        });

              //  ((LodingList) mContext).mInventoryList = mTagRealList.getData();
        ((MainActivityList) mContext).mInventoryList = mTagRealList.getData();
    }

    public void refresh() {
        clearText();
        mRealRoundEditText.setText("1");
    }


    @SuppressWarnings("deprecation")
    private void refreshStartStop(boolean start) {
        if (start) {
            mStartStop.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.button_disenabled_background));
            mStartStop.setText(getResources()
                    .getString(R.string.stop_inventory));
        } else {
            mStartStop.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.button_background));
            mStartStop.setText(getResources().getString(
                    R.string.start_inventory));
        }
    }

    private Handler mLoopHandler = new Handler();
    private Runnable mLoopRunnable = new Runnable() {
        @Override
        public void run() {
            mLoopHandler.removeCallbacks(this);
            if (mIsCustom) {
                mReaderHelper.customizedSessionTargetInventory(m_curReaderSetting.btReadId,
                        mBtSession,
                        mBtTarget,
                        mBtRepeat);
            } else {
                mReaderHelper.realTimeInventory(m_curReaderSetting.btReadId,
                        mBtRepeat);
            }
            mLoopHandler.postDelayed(this, 2000);
            mStartTime = System.currentTimeMillis();
        }
    };

    private void setSessionIdText(int pos) {
        if (pos >= 0 && pos < mSessionIdList.size()) {
            String value = mSessionIdList.get(pos);
            mSessionIdTextView.setText(value);
            mPos1 = pos;
        }
    }

    private void setInventoriedFlagText(int pos) {
        if (pos >= 0 && pos < mInventoriedFlagList.size()) {
            String value = mInventoriedFlagList.get(pos);
            mInventoriedFlagTextView.setText(value);
            mPos2 = pos;
        }
    }

    private void showSpinWindow1() {
        mSpinerPopWindow1.setWidth(mDropDownRow1.getWidth());
        mSpinerPopWindow1.showAsDropDown(mDropDownRow1);
    }

    private void showSpinWindow2() {
        mSpinerPopWindow2.setWidth(mDropDownRow2.getWidth());
        mSpinerPopWindow2.showAsDropDown(mDropDownRow2);
    }

    public void startStop(boolean start) {
        refreshStartStop(start);
        mIsLoop = start;

        if (start) {
            String strRepeat = mRealRoundEditText.getText().toString();
            if (strRepeat.length() <= 0) {
                Toast.makeText(mContext,
                        getResources().getString(R.string.repeat_empty),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            mBtRepeat = (byte) Integer.parseInt(strRepeat);
            if ((mBtRepeat & 0xFF) <= 0) {
                Toast.makeText(mContext,
                        getResources().getString(R.string.repeat_min),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            mIsCustom = mSelectCommand.getCheckedRadioButtonId() == id.cmd_S0;
            mBtSession = (byte) (mPos1 & 0xFF);
            mBtTarget = (byte) (mPos2 & 0xFF);
            mLoopRunnable.run();
        } else {
            mLoopHandler.removeCallbacks(mLoopRunnable);
        }
    }

    private void clearText() {
        mTotalTime = 0;
        mTagsCountText.setText("0");
        mTagsTotalText.setText("0");
        mTagsSpeedText.setText("0");
        mTagsTimeText.setText("0");
        mTagsOpTimeText.setText("0");
        mTagRealList.clearData();
    }

    private RXObserver mObserver = new RXObserver() {

        @Override
        protected void onInventoryTag(RXInventoryTag tag) {
            final InventoryBean bean = new InventoryBean(tag.strEPC, tag.strPC, tag.strRSSI, tag.strFreq);
            mLoopHandler.post(new Runnable() {
                @Override
                public void run() {

                        mTagRealList.addData(bean);
                        mTagsCountText.setText(String.valueOf(mTagRealList.getCountTag()));
                        mTagsTotalText.setText(String.valueOf(mTagRealList.getTotalTag()));
                        mApp.addTag(bean.getEpc());
                        BeeperUtils.beep(BeeperUtils.BEEPER_SHORT);

                }
            });
        }

        @Override
        protected void onInventoryTagEnd(final RXInventoryTag.RXInventoryTagEnd tagEnd) {
            mLoopHandler.post(new Runnable() {
                @Override
                public void run() {
                    String strLog = FormatUtils.format(tagEnd.cmd, ERROR.SUCCESS);
                    mTagsSpeedText.setText(String.valueOf(tagEnd.mReadRate));
                    long exeTime = System.currentTimeMillis() - mStartTime;
                    mTotalTime += exeTime;
                    mTagsOpTimeText.setText(String.valueOf(exeTime));
                    mTagsTimeText.setText(String.valueOf(mTotalTime));
                    mLogList.writeLog(strLog, ERROR.SUCCESS);
                    BeeperUtils.beep(BeeperUtils.BEEPER);
                    if (mIsLoop) {
                        mLoopRunnable.run();
                    }
                }
            });
        }

        @Override
        protected void onExeCMDStatus(final byte cmd, final byte status) {
            mLoopHandler.post(new Runnable() {
                @Override
                public void run() {
                    String strLog = FormatUtils.format(cmd, status);
                    mLogList.writeLog(strLog, status);
                    if (mIsLoop) {
                        if (cmd == CMD.CUSTOMIZED_SESSION_TARGET_INVENTORY || cmd == CMD.REAL_TIME_INVENTORY) {
                            mLoopRunnable.run();
                        }
                    }
                }
            });
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mReaderHelper.registerObserver(mObserver);
        mReaderHelper.startWith();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mReaderHelper.unRegisterObserver(mObserver);
        mLoopHandler.removeCallbacks(mLoopRunnable);
        mApp.clearTags();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           if (mLogList.tryClose())
                return true;
        }
        Log.d(TAG, "onKeyDown: PageReal");
        return super.onKeyDown(keyCode, event);
    }
}
