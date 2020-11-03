package com.bean;


/**
 * @author naz
 * Email 961057759@qq.com
 * Date 2019/12/30
 */
public class OperateBean {
    /**
     * PC value
     */
    private String pc;
    /**
     * CRC value
     */
    private String crc;
    /**
     * EPC value
     */
    private String epc;
    /**
     * Data in total
     */
    private String data;
    /**
     * Length of data
     */
    private String dataLen;
    /**
     * Antenna ID
     */
    private byte antId;
    /**
     * Operating times
     */
    private int times;

    public OperateBean(String pc, String crc, String epc, String data, String dataLen, byte antId) {
        this.pc = pc;
        this.crc = crc;
        this.epc = epc;
        this.data = data;
        this.dataLen = dataLen;
        this.antId = antId;
        this.times = 1;
    }

    public String getPc() {
        return pc;
    }

    public void setPc(String pc) {
        this.pc = pc;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDataLen() {
        return dataLen;
    }

    public void setDataLen(String dataLen) {
        this.dataLen = dataLen;
    }

    public byte getAntId() {
        return antId;
    }

    public void setAntId(byte antId) {
        this.antId = antId;
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
}
