package com.bean;


public class InventoryBean {
    /**
     * epc
     */
    private String epc;
    /**
     * pc
     */
    private String pc;
    /**
     * 识别次数
     */
    private int times;
    /**
     * RSSI
     */
    private String rssi;
    /**
     * 载波频率
     */
    private String freq;
    /**
     * 时间
     */
    private long date;

    public InventoryBean(String epc, String pc, String rssi, String freq) {
        this.epc = epc;
        this.pc = pc;
        this.rssi = rssi;
        this.freq = freq;
        this.times = 1;
        this.date = System.currentTimeMillis();
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public void addTimes() {
        this.times++;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
