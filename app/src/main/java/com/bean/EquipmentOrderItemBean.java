package com.bean;

import java.util.ArrayList;
import java.util.List;

public class EquipmentOrderItemBean {

    private  int loadtype = 0;

    public void setLoadtype(int loadtype) {
        this.loadtype = loadtype;
    }

    public int getLoadtype() {
        return loadtype;
    }

    private List<CarOrderItemInfoBean> items = new ArrayList<>();

    public void setItems(List<CarOrderItemInfoBean> items) {
        this.items = items;
    }

    public List<CarOrderItemInfoBean> getItems()
    {
        return items;
    }
}


