package com.cctv.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bean.InventoryBean;
import com.bean.OrderItemsBean;
import com.cctv.UHFApplication;
import com.cctv.activity.CarLoadActivity;
import com.cctv.device.R.id;
import com.cctv.device.R.layout;
import com.cctv.widget.TagCarList;

import java.util.List;

import static com.cctv.device.R.id.car_id_text_item;

public class CarListAdapter extends BaseAdapter {
    //add by lei.li 2016/11/10
    public static int mWidthest = 0;

    private LayoutInflater mInflater;

    private Context mContext;

    private List<InventoryBean> listMap;

    private UHFApplication mApp;

    public final class ListItemView {
        public TextView mIdText;
        public TextView mEpcText;
        public TextView mPcText;
        public TextView mTimesText;
        public TextView mRssiText;
        public TextView mFreqText;

        public TextView mEpcNewInfo;
        public TextView mBoxNum;
        public TextView mWeight;
        public TextView mSize;
        public TextView mDepartMent;
        public TextView mEqumentOrder;

        public ImageView mMoveBtn;
    }

    private  InventoryBean mMoveben;


    private View.OnClickListener mOnClickListener;

    public CarListAdapter(Context context, List<InventoryBean> listMap) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.listMap = listMap;

        mApp = (UHFApplication) ((Activity) context).getApplication();
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

    //自定义一个回调接口来实现Click和LongClick事件
    public interface OnItemClickListener  {
        void onItemClick(View v, int position);
        void onItemLongClick(View v);
    }
    private OnItemClickListener mOnItemClickListener;//声明自定义的接口

    //定义方法并暴露给外面的调用者
    public void setOnItemClickListener(OnItemClickListener  listener) {
        this.mOnItemClickListener  = listener;
    }




    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ListItemView listItemView = null;
        if (convertView == null) {
            listItemView = new ListItemView();
            convertView = mInflater.inflate(layout.car_tag_list_item, null);
            listItemView.mIdText = (TextView) convertView.findViewById(car_id_text_item);
            listItemView.mEpcText = (TextView) convertView.findViewById(id.car_epc_text_item);



            listItemView.mPcText = (TextView) convertView.findViewById(id.car_pc_text_item);
            listItemView.mTimesText = (TextView) convertView.findViewById(id.car_times_text_item);
            listItemView.mRssiText = (TextView) convertView.findViewById(id.car_rssi_text_item);
            listItemView.mFreqText = (TextView) convertView.findViewById(id.car_freq_text_item);


            listItemView.mEpcNewInfo = (TextView)convertView.findViewById(id.car_epc_num_item) ;
            listItemView.mBoxNum =(TextView)convertView.findViewById(id.car_pc_box_item) ;
            listItemView.mWeight = (TextView)convertView.findViewById(id.car_device_weight_item);
            listItemView.mSize =(TextView)convertView.findViewById(id.car_device_size_item);
            listItemView.mDepartMent = (TextView)convertView.findViewById(id.car_depart_item);
            listItemView.mEqumentOrder = (TextView)convertView.findViewById(id.car_order_item);

            listItemView.mMoveBtn = (ImageView)convertView.findViewById(id.image_unload);
            listItemView.mMoveBtn.setOnClickListener(mOnClickListener);
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }

        //add by lei.li 2016/
        final InventoryBean bean = listMap.get(position);

        listItemView.mIdText.setText(String.valueOf(position + 1));
        listItemView.mEpcText.setText(bean.getEpc());
        listItemView.mPcText.setText(bean.getPc());
        listItemView.mTimesText.setText(String.valueOf(bean.getTimes()));

        if(bean.getOrderitem()!=null)
        {
            OrderItemsBean dataBean = bean.getOrderitem();
            listItemView.mEpcNewInfo.setText(dataBean.getEpcname());
            listItemView.mBoxNum.setText(String.valueOf(dataBean.getBoxnum()));
            listItemView.mWeight.setText(String.valueOf(dataBean.getWeight()));
            listItemView.mSize.setText(dataBean.getSize());
            listItemView.mDepartMent.setText(dataBean.getDepartname());
            listItemView.mEqumentOrder.setText(dataBean.getTransportnum());

            mMoveben = bean;
            listItemView.mMoveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //removeBen(mMoveben);
                    removeData(position,bean);
                }
            });
        }

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

    public void removeBen(InventoryBean bean)
    {
       // ((TagCarList) mContext).mInventoryList = mTagRealList.getData();
        //((CarLoadActivity) mContext)
    }

    public void removeData(int position,InventoryBean argBean) {
        listMap.remove(position);
        //删除动画
       // notifyItemRemoved(position);
        mApp.addUnlodTag(argBean.getOrderitem().getOrderitemid());
        notifyDataSetChanged();
    }

    public List<InventoryBean> GetCurrentData()
    {
        return  listMap;
    }


}
