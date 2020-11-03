package com.cctv.device;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.bean.InventoryBean;
import com.bean.OperateBean;
import com.cctv.widget.PopupMenu;
import com.rfid.RFIDReaderHelper;
import com.rfid.rxobserver.RXObserver;
import com.rfid.rxobserver.ReaderSetting;
import com.cctv.device.R.layout;
import com.cctv.UHFApplication;
import com.cctv.dialogwin.SpinerPopWindow;//SpinerPopWindow.SpinerPopWindow;//SpinerPopWindow;
import com.cctv.widget.LogList;
import com.cctv.widget.TagRealList;
import com.util.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;


public class LodingList extends AppCompatActivity {
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




    private PopupMenu popupMenu;
    private ImageView iv_menu;
    private TextView title[] = new TextView[2];
    private ViewPager mPager;
    private com.cctv.widget.PopupMenu.MENUITEM cur_item = com.cctv.widget.PopupMenu.MENUITEM.ITEM1;
    private List<View> listViews;
    public List<InventoryBean> mInventoryList = new ArrayList<>();
    public List<OperateBean> mOperateList = new ArrayList<>();

    public void refresh() {
        //clearText();
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main_list);

        popupMenu = new PopupMenu(this);

        iv_menu = (ImageView) findViewById(R.id.iv_menu);
        iv_menu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupMenu.showLocation(R.id.iv_menu);
                popupMenu.setOnItemClickListener(new PopupMenu.OnItemClickListener() {

                    @Override
                    public void onClick(PopupMenu.MENUITEM item, String str) {
                        if (item == PopupMenu.MENUITEM.ITEM1) {
                            //InitViewPager(PopupMenu.MENUITEM.ITEM1);
                        } else if (item == PopupMenu.MENUITEM.ITEM2) {
                            //InitViewPager(PopupMenu.MENUITEM.ITEM2);
                        } else if (item == PopupMenu.MENUITEM.ITEM3) {
                            Intent intent;
                            //intent = new Intent().setClass(MainActivityList.this, MainActivity.class);
                            //startActivity(intent);
                        } else if (item == PopupMenu.MENUITEM.ITEM4) {
                            //askForOut();
                        } else if (item == PopupMenu.MENUITEM.ITEM5) {
                            if (str.equals("English")) {
                                PreferenceUtil.commitString("language", "en");
                            } else if (str.equals("中文")) {
                                PreferenceUtil.commitString("language", "zh");
                            }
                            Intent intent = new Intent(LodingList.this, LodingList.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            LodingList.this.startActivity(intent);

                        } else if (item == PopupMenu.MENUITEM.ITEM_add1) {
                           // saveExcel();
                        }
                    }
                });
            }
        });

        InitTextView();
        InitViewPager(PopupMenu.MENUITEM.ITEM1);
    }

    private void InitViewPager(PopupMenu.MENUITEM item) {
        cur_item = item;
        mPager = (ViewPager) findViewById(R.id.vPager);
        listViews = new ArrayList<>();
        LayoutInflater mInflater = getLayoutInflater();
        mInflater= LayoutInflater.from(this);

       // View v1 = LayoutInflater.from(this).inflate(R.layout.lay1, layout, false);

        if (item == PopupMenu.MENUITEM.ITEM1) {
            View temp = mInflater.inflate(layout.lay1,null);
            listViews.add(temp);
            //listViews.add(mInflater.inflate(R.layout.lay1, null));
            listViews.add(mInflater.inflate(R.layout.lay2, null));
        } else if (item == PopupMenu.MENUITEM.ITEM2) {
            //listViews.add(mInflater.inflate(R.layout.lay3, null));
            //listViews.add(mInflater.inflate(R.layout.lay4, null));
        }
        mPager.setAdapter(new MyPagerAdapter(listViews));
        mPager.setCurrentItem(0);
        //mPager.setOnPageChangeListener(new MainActivityList.MyOnPageChangeListener());
        //currIndex = 0;
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
       // public void destroyItem(View arg0, int arg1, Object arg2) {
       //     ((ViewPager) arg0).removeView(mListViews.get(arg1));
       // }

        public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
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
       // public Object instantiateItem(View arg0, int arg1) {
       //     ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
       //     return mListViews.get(arg1);
       // }

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

    private void InitTextView() {
        title[0] = (TextView) findViewById(R.id.tab_index1);
        title[1] = (TextView) findViewById(R.id.tab_index2);

        title[0].setOnClickListener(new MyOnClickListener(0));
        //title[1].setOnClickListener(new MyOnClickListener(1));
    }

    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        //mReaderHelper.registerObserver(mObserver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mReaderHelper.unRegisterObserver(mObserver);
    }

    private RXObserver mObserver = new RXObserver() {

    };
}