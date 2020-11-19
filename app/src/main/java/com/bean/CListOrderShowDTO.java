package com.bean;

public class CListOrderShowDTO extends InventoryBean {

    private OrderItemsBean orerItem = new OrderItemsBean();

    public CListOrderShowDTO(String epc, String pc, String rssi, String freq) {
        super(epc, pc, rssi, freq);
    }

    public void setOrerItem(OrderItemsBean orerItem) {
        this.orerItem = orerItem;
    }

    public OrderItemsBean getOrerItem()
    {
        return  orerItem;
    }
}
