package com.cctv.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bean.InventoryBean;
import com.cctv.device.R.id;
import com.cctv.device.R.layout;

import java.util.List;

public class CarListAdapter extends BaseAdapter {
    //add by lei.li 2016/11/10
    public static int mWidthest = 0;

    private LayoutInflater mInflater;

    private Context mContext;

    private List<InventoryBean> listMap;

    public final class ListItemView {
        public TextView mIdText;
        public TextView mEpcText;
        public TextView mPcText;
        public TextView mTimesText;
        public TextView mRssiText;
        public TextView mFreqText;
    }

    public CarListAdapter(Context context, List<InventoryBean> listMap) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.listMap = listMap;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listMap.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ListItemView listItemView = null;
        if (convertView == null) {
            listItemView = new ListItemView();
            convertView = mInflater.inflate(layout.car_tag_list_item, null);
            listItemView.mIdText = (TextView) convertView.findViewById(id.car_id_text_item);
            listItemView.mEpcText = (TextView) convertView.findViewById(id.car_epc_text_item);

            //add by lei.li 2016/11/11
			/*if (!listMap.isEmpty()) {
			    listItemView.mEpcText.getLayoutParams().width = lengthestData();
			    mWidthest = listItemView.mEpcText.getLayoutParams().width;
			}*/
            //add by lei.li 2016/11/11

            //add by lei.li 2016/11/14
            //add by lei.li 2016/11/14


            listItemView.mPcText = (TextView) convertView.findViewById(id.car_pc_text_item);
            listItemView.mTimesText = (TextView) convertView.findViewById(id.car_times_text_item);
            listItemView.mRssiText = (TextView) convertView.findViewById(id.car_rssi_text_item);
            listItemView.mFreqText = (TextView) convertView.findViewById(id.car_freq_text_item);
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
            //add by lei.li 2016/11/11
			/*if (!listMap.isEmpty())
			    listItemView.mEpcText.getLayoutParams().width = lengthestData();*/
            //add by lei.li 2016/11/11
        }

        //add by lei.li 2016/

        InventoryBean bean = listMap.get(position);

        listItemView.mIdText.setText(String.valueOf(position + 1));
        listItemView.mEpcText.setText(bean.getEpc());
        listItemView.mPcText.setText(bean.getPc());
        listItemView.mTimesText.setText(String.valueOf(bean.getTimes()));
        try {
            listItemView.mRssiText.setText((Integer.parseInt(bean.getRssi()) - 129) + "dBm");
        } catch (Exception e) {
            listItemView.mRssiText.setText("");
        }
        listItemView.mFreqText.setText(bean.getFreq());
        return convertView;
    }

    /**
     * get lengthest data in listMap
     *
     * @return the max show area
     */
    public int lengthestData() {
        int length = 0;
        for (InventoryBean itm : listMap) {
            if (length < itm.getEpc().length())
                length = itm.getEpc().length();
        }
        return length * 16;
    }


}
