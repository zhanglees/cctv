package com.cctv.device;

import android.os.Bundle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;//android.support.v4.view.PagerAdapter;
import androidx.viewpager.widget.ViewPager;//android.support.v4.view.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;//android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.InventoryBean;
import com.bean.OperateBean;
import com.module.interaction.RXTXListener;
import com.nativec.tools.ModuleManager;
import com.rfid.RFIDReaderHelper;
import com.rfid.config.ERROR;
import com.rfid.rxobserver.RXObserver;
import com.rfid.rxobserver.ReaderSetting;
import com.cctv.UHFApplication;
import com.cctv.dialogwin.CustomedDialog;

import com.cctv.widget.PopupMenu;
import com.cctv.widget.PopupMenu.MENUITEM;
//import com.uhf.uhf.widget.PopupMenu.OnItemClickListener;
//import com.uhf.uhf.widget.tagpage.PageInventoryReal6B;
//import com.uhf.uhf.widget.tagpage.PageTag6BAccess;
//import com.uhf.uhf.widget.tagpage.PageTagAccess;
import com.util.BeeperUtils;
import com.util.ExcelUtils;
import com.util.PreferenceUtil;
import com.util.StringTool;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class MainActivityList  extends BaseActivity {

    private ViewPager mPager;
    private List<View> listViews;
    private TextView title[] = new TextView[2];
    private int currIndex = 0;

    private TextView mRefreshButton;

    private RFIDReaderHelper mReaderHelper;

    private boolean mKeyF4Pressing = false;

    //test
    public static Activity activity;

    private static ReaderSetting m_curReaderSetting;

    public static int mSaveType = 0;

    private ImageView iv_menu;
    private PopupMenu popupMenu;

    private com.cctv.widget.PopupMenu.MENUITEM cur_item = com.cctv.widget.PopupMenu.MENUITEM.ITEM1;
    public static final String REGION_TYPE = "region_type";
    public static final String FREQUENCY_INTERVAL = "frequency_interval";
    public static final String START_FREQUENCY = "start_frequency";
    public List<InventoryBean> mInventoryList = new ArrayList<>();
    public List<OperateBean> mOperateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        ((UHFApplication) getApplication()).addActivity(this);

        activity = this;

        // Storage Permissions
        ExcelUtils.verifyStoragePermissions(this);

        mRefreshButton = (TextView) findViewById(R.id.refresh);

        mRefreshButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cur_item == PopupMenu.MENUITEM.ITEM1) {
                    if (0 == currIndex) {
                        ((PageInventoryReal) findViewById(R.id.view_PageInventoryReal)).refresh();
                    } else {
                        ((PageTagAccess) findViewById(R.id.view_PageTagAccess)).refresh();
                    }
                } else if (cur_item == PopupMenu.MENUITEM.ITEM2) {
                    if (0 == currIndex) {
                      //  ((PageInventoryReal6B) findViewById(R.id.view_PageInventoryReal6B)).refresh();
                    } else {
                       // ((PageTag6BAccess) findViewById(R.id.view_PageTag6BAccess)).refresh();
                    }
                }
            }
        });

        popupMenu = new PopupMenu(this);

        iv_menu = (ImageView) findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupMenu.showLocation(R.id.iv_menu);
                popupMenu.setOnItemClickListener(new PopupMenu.OnItemClickListener() {

                    @Override
                    public void onClick(PopupMenu.MENUITEM item, String str) {
                        if (item == PopupMenu.MENUITEM.ITEM1) {
                            InitViewPager(PopupMenu.MENUITEM.ITEM1);
                        } else if (item == PopupMenu.MENUITEM.ITEM2) {
                            InitViewPager(PopupMenu.MENUITEM.ITEM2);
                        } else if (item == PopupMenu.MENUITEM.ITEM3) {
                            Intent intent;
                            intent = new Intent().setClass(MainActivityList.this, MainActivity.class);
                            startActivity(intent);
                        } else if (item == PopupMenu.MENUITEM.ITEM4) {
                            askForOut();
                        } else if (item == PopupMenu.MENUITEM.ITEM5) {
                            if (str.equals("English")) {
                                PreferenceUtil.commitString("language", "en");
                            } else if (str.equals("中文")) {
                                PreferenceUtil.commitString("language", "zh");
                            }
                            Intent intent = new Intent(MainActivityList.this, MainActivityList.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MainActivityList.this.startActivity(intent);

                        } else if (item == PopupMenu.MENUITEM.ITEM_add1) {
                            saveExcel();
                        }
                    }
                });
            }
        });

        InitTextView();
        InitViewPager(MENUITEM.ITEM1);

        try {
            mReaderHelper = RFIDReaderHelper.getDefaultHelper();
            mReaderHelper.setRXTXListener(new RXTXListener() {
                @Override
                public void reciveData(byte[] bytes) {
                    if (PreferenceUtil.getBoolean(MonitorActivity.mIsChecked, false)) {
                        String strLog = StringTool.byteArrayToString(bytes, 0, bytes.length);
                        ((UHFApplication) getApplication()).writeMonitor(strLog, ERROR.SUCCESS);
                    }
                }

                @Override
                public void sendData(byte[] bytes) {
                    if (PreferenceUtil.getBoolean(MonitorActivity.mIsChecked, false)) {
                        String strLog = StringTool.byteArrayToString(bytes, 0, bytes.length);
                        ((UHFApplication) getApplication()).writeMonitor(strLog, ERROR.FAIL);
                    }
                }

                @Override
                public void onLostConnect() {
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
            finish();
        }

        m_curReaderSetting = ReaderSetting.newInstance();

        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mReaderHelper.registerObserver(mObserver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mReaderHelper.unRegisterObserver(mObserver);
    }

    private RXObserver mObserver = new RXObserver() {

    };

    private void InitTextView() {
        title[0] = (TextView) findViewById(R.id.tab_index1);
        title[1] = (TextView) findViewById(R.id.tab_index2);

        title[0].setOnClickListener(new MyOnClickListener(0));
        title[1].setOnClickListener(new MyOnClickListener(1));
    }

    private void InitViewPager(PopupMenu.MENUITEM item) {
        cur_item = item;
        mPager = (ViewPager) findViewById(R.id.vPager);
        listViews = new ArrayList<>();
        LayoutInflater mInflater = getLayoutInflater();
        if (item == PopupMenu.MENUITEM.ITEM1) {
            listViews.add(mInflater.inflate(R.layout.lay1, null));
            //listViews.add(mInflater.inflate(R.layout.lay2, null));
        } else if (item == PopupMenu.MENUITEM.ITEM2) {
            //listViews.add(mInflater.inflate(R.layout.lay3, null));
            //listViews.add(mInflater.inflate(R.layout.lay4, null));
        }
        mPager.setAdapter(new MyPagerAdapter(listViews));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
        currIndex = 0;
        title[1].setBackgroundResource(R.drawable.btn_select_background_select_left_down);
        title[0].setBackgroundResource(R.drawable.btn_select_background_select_right);
        title[1].setTextColor(Color.rgb(0x00, 0xBB, 0xF7));
        title[0].setTextColor(Color.rgb(0xFF, 0xFF, 0xFF));
    }

    public class MyPagerAdapter extends PagerAdapter {
        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(ViewGroup arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }

    public class MyOnClickListener implements OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            currIndex = arg0;
            if (0 == currIndex) {
                title[1].setBackgroundResource(R.drawable.btn_select_background_select_left_down);
                title[0].setBackgroundResource(R.drawable.btn_select_background_select_right);
                mSaveType = 0;
            } else {
                title[1].setBackgroundResource(R.drawable.btn_select_background_select_left);
                title[0].setBackgroundResource(R.drawable.btn_select_background_select_right_down);
                mSaveType = 1;
            }

            title[1 - currIndex].setTextColor(Color.rgb(0x00, 0xBB, 0xF7));
            title[currIndex].setTextColor(Color.rgb(0xFF, 0xFF, 0xFF));
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                askForOut();
                return true;
            case KeyEvent.KEYCODE_MENU:
                break;

            case KeyEvent.KEYCODE_F4: {
                if (cur_item == PopupMenu.MENUITEM.ITEM1) {
                    if (0 == currIndex) {
                        if (!mKeyF4Pressing) {
                            mKeyF4Pressing = true;
                            ((PageInventoryReal) findViewById(R.id.view_PageInventoryReal)).startStop(true);
                        }
                    }
                } else if (cur_item == PopupMenu.MENUITEM.ITEM2) {
                    if (0 == currIndex) {
                        if (!mKeyF4Pressing) {
                            mKeyF4Pressing = true;
                           // ((PageInventoryReal6B) findViewById(R.id.view_PageInventoryReal6B)).startStop(true);
                        }
                    }
                }

            }

            break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_F4) {

            if (cur_item == MENUITEM.ITEM1) {
                if (0 == currIndex) {
                    mKeyF4Pressing = false;
                    ((PageInventoryReal) findViewById(R.id.view_PageInventoryReal)).startStop(false);
                }
            } else if (cur_item == MENUITEM.ITEM2) {
                if (0 == currIndex) {
                    mKeyF4Pressing = false;
                   // ((PageInventoryReal6B) findViewById(R.id.view_PageInventoryReal6B)).startStop(false);
                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private void askForOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.alert_diag_title)).
                setMessage(getString(R.string.are_you_sure_to_exit)).
                setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PreferenceUtil.commitInt(MainActivityList.REGION_TYPE, m_curReaderSetting.btRegion);
                                PreferenceUtil.commitInt(MainActivityList.FREQUENCY_INTERVAL, m_curReaderSetting.btUserDefineFrequencyInterval);
                                PreferenceUtil.commitInt(MainActivityList.START_FREQUENCY, m_curReaderSetting.nUserDefineStartFrequency);
                                //close the module
                                ModuleManager.newInstance().setUHFStatus(false);
                                getApplication().onTerminate();
                            }
                        }).setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setCancelable(false).show();
    }

    /**
     * Save the tags as excel file;
     */
    private void saveExcel() {
        CustomedDialog customedDialog = new CustomedDialog(this, R.layout.excel_save_dialog);
        customedDialog.setTags(mInventoryList);
        customedDialog.setOperationTags(mOperateList);
        customedDialog.getDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        BeeperUtils.release();
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
                    mWornDialog = new AlertDialog.Builder(MainActivityList.this)
                            .setCancelable(false)
                            .setTitle("警告")
                            .setMessage("当前电量低于10%")
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    PreferenceUtil.commitInt(MainActivityList.REGION_TYPE, m_curReaderSetting.btRegion);
                                    PreferenceUtil.commitInt(MainActivityList.FREQUENCY_INTERVAL, m_curReaderSetting.btUserDefineFrequencyInterval);
                                    PreferenceUtil.commitInt(MainActivityList.START_FREQUENCY, m_curReaderSetting.nUserDefineStartFrequency);
                                    ModuleManager.newInstance().setUHFStatus(false);
                                    getApplication().onTerminate();
                                }
                            }).show();

                } else if (!mWornDialog.isShowing()) {
                    mWornDialog.show();
                }
            }
        }
    };
}