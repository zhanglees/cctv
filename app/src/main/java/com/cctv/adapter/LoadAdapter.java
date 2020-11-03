package com.cctv.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bean.InventoryBean;
import com.bean.OrderItemsBean;
import com.cctv.device.R;

import java.util.List;

public class LoadAdapter extends BaseAdapter {

    public List<OrderItemsBean>  _items ;
    public LayoutInflater _cuttent;
    private Context mContext;

    public LoadAdapter(Context context, List<OrderItemsBean> listMap) {
        this.mContext = context;
        //this._cuttent = context;// LayoutInflater.from(context);
        this._items = listMap;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return _items.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return _items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

     /*   ViewHolder vh = new ViewHolder();

        if(convertView==null){

            convertView = View.inflate(mContext,R.layout.activity_un_item, null);
            vh.iv = (TextView) convertView.findViewById(R.id.task);
            vh.tv = (TextView) convertView.findViewById(R.id.textView1);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder)convertView.getTag();
        }
        vh.iv.setText(_items.get(position).getTasknum());
        vh.tv.setText(_items.get(position).getEpcname());

        return convertView;*/
        convertView = View.inflate(mContext,R.layout.activity_un_item, null);
        TextView task = (TextView) convertView.findViewById(R.id.task);
        TextView epcname = (TextView) convertView.findViewById(R.id.textView1);

        task.setText(_items.get(position).getTasknum());
        epcname.setText(_items.get(position).getEpcname());
        //Log.i(String.valueOf(position), _items.get(position).getEpcname());
        return  convertView;
    }
    static class ViewHolder{
        TextView iv;
        TextView tv;
    }
}
