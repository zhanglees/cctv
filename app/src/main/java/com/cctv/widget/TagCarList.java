package com.cctv.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.bean.InventoryBean;
import com.cctv.adapter.CarListAdapter;
import com.cctv.device.R;
import com.cctv.adapter.RealListAdapter;

import java.util.ArrayList;
import java.util.List;


public class TagCarList extends LinearLayout {

    private Context mContext;
    private TableRow mTagRealRow;
    private ImageView mTagRealImage;
    private TextView mListTextInfo;

    private TextView mMinRSSIText, mMaxRSSIText;

    private List<InventoryBean> data;
    private CarListAdapter mRealListAdapter;
    private ListView mTagRealList;

    private View mTagsRealListScrollView;
    //add by lei.li 2016/12/26
    private TextView mUIDText;

    private WindowManager wm;

    private OnItemSelectedListener mOnItemSelectedListener;
    private int mMinRSSI, mMaxRSSI;
    private int mTotalTag;

    public interface OnItemSelectedListener {
        public void onItemSelected(View arg1, int arg2, long arg3);
    }

    public TagCarList(Context context, AttributeSet attrs) {
        super(context, attrs);
        initContext(context);
    }

    public TagCarList(Context context) {
        super(context);
        initContext(context);
    }

    @SuppressWarnings("deprecation")
    private void initContext(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.car_tag_list, this);

        data = new ArrayList<>();

        mTagsRealListScrollView = findViewById(R.id.car_tags_real_list_scroll_view);
        wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        LayoutParams lp = (LayoutParams) mTagsRealListScrollView
                .getLayoutParams();
        lp.height = (int) (wm.getDefaultDisplay().getHeight() / 2.7);
        mTagsRealListScrollView.setLayoutParams(lp);
        mTagsRealListScrollView.invalidate();
        // mTagsRealListScrollView.setVisibility(View.GONE);
        // mUIDText = (TextView) findViewById(R.id.uid_text);

        mTagRealRow = (TableRow) findViewById(R.id.car_table_row_tag_real);
        mTagRealImage = (ImageView) findViewById(R.id.car_image_prompt);
        mTagRealImage.setImageDrawable(getResources()
                .getDrawable(R.drawable.up));
        mListTextInfo = (TextView) findViewById(R.id.car_list_text_info);
        mListTextInfo.setText(getResources().getString(R.string.open_tag_list));

        mMinRSSIText = (TextView) findViewById(R.id.car_min_rssi_text);
        mMaxRSSIText = (TextView) findViewById(R.id.car_max_rssi_text);

        mTagRealRow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                LayoutParams lp = (LayoutParams) mTagsRealListScrollView
                        .getLayoutParams();

                if (lp.height <= wm.getDefaultDisplay().getHeight() / 2) {
                    // mTagsRealListScrollView.setVisibility(View.VISIBLE);

                    lp.height = (int) (wm.getDefaultDisplay().getHeight() / 1.5);
                    mTagsRealListScrollView.setLayoutParams(lp);
                    mTagsRealListScrollView.invalidate();

                    mTagRealImage.setImageDrawable(getResources().getDrawable(
                            R.drawable.down));
                    mListTextInfo.setText(getResources().getString(
                            R.string.close_tag_list));
                } else {
                    // mTagsRealListScrollView.setVisibility(View.GONE);

                    lp.height = (int) (wm.getDefaultDisplay().getHeight() / 2.7);
                    mTagsRealListScrollView.setLayoutParams(lp);
                    mTagsRealListScrollView.invalidate();

                    mTagRealImage.setImageDrawable(getResources().getDrawable(
                            R.drawable.up));
                    mListTextInfo.setText(getResources().getString(
                            R.string.open_tag_list));
                }
            }
        });

        mTagRealList = (ListView) findViewById(R.id.car_tag_real_list_view);
        mRealListAdapter = new CarListAdapter(mContext, data);
        mTagRealList.setAdapter(mRealListAdapter);

        mTagRealList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (mOnItemSelectedListener != null)
                    mOnItemSelectedListener.onItemSelected(arg1, arg2, arg3);
            }

        });
    }

    public void setOnItemSelectedListener(
            OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    public final void clearData() {
        mMinRSSI = mMaxRSSI = 0;
        mTotalTag = 0;
        mMinRSSIText.setText("0dBm");
        mMaxRSSIText.setText("0dBm");
        data.clear();
        mRealListAdapter.notifyDataSetChanged();
    }

    public final void addData(InventoryBean bean) {
        if (bean.getEpc() == null) {
            return;
        }
        mTotalTag++;
        if(bean.getRssi()!=null&&(!bean.getRssi().equals("")))
        {
            setMaxMinRSSI(Integer.parseInt(bean.getRssi()));
        }

        for (InventoryBean datum : data) {
            if (bean.getEpc().equals(datum.getEpc())) {
                datum.addTimes();
                mRealListAdapter.notifyDataSetChanged();
                return;
            }
        }
        data.add(bean);
        mRealListAdapter.notifyDataSetChanged();
    }

    public final void moveData(InventoryBean bean)
    {
        if (bean.getEpc() == null) {
            return;
        }
        mTotalTag--;
        if(bean.getRssi()!=null&&(!bean.getRssi().equals("")))
        {
            setMaxMinRSSI(Integer.parseInt(bean.getRssi()));
        }
        InventoryBean temData;
        for (InventoryBean datum : data) {
            if (bean.getEpc().replaceAll(" ", "").toLowerCase().equals(datum.getEpc())) {
                datum.addTimes();
                data.remove(datum);
                mRealListAdapter.notifyDataSetChanged();
                return;
            }
        }

        mRealListAdapter.notifyDataSetChanged();
    }

    public List<InventoryBean> getData(){
        return data;
    }

    public int getTotalTag() {
        return mTotalTag;
    }

    public int getCountTag() {
        return mRealListAdapter.getCount();
    }

    private void setMaxMinRSSI(int nRSSI) {
        if (mMaxRSSI < nRSSI) {
            mMaxRSSI = nRSSI;
        }

        if (mMinRSSI <= 0) {
            mMinRSSI = nRSSI;
        } else if (mMinRSSI > nRSSI) {
            mMinRSSI = nRSSI;
        }
        mMinRSSIText.setText((mMinRSSI - 129) + "dBm");
        mMaxRSSIText.setText((mMaxRSSI - 129) + "dBm");
    }

    private void invaildate() {
        if (mUIDText != null) {
            mUIDText.setWidth(mRealListAdapter.mWidthest);
            Log.e("change the width", mUIDText.getWidth() + "::::::::::::::");
            mUIDText.invalidate();
        }
    }

    // add by lei.li 2016/11/11
    private int lengthestData() {
        int widest = 0;
        for (InventoryBean itm : data) {
            if (widest < itm.getEpc().length())
                widest = itm.getEpc().length();
        }

        return widest * 16;
    }
}