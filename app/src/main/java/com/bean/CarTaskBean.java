package com.bean;

public class CarTaskBean {
    private String taskid;
    private String carid;
    private int needall =0;

    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }

    public String getTaskid() {
        return taskid;
    }

    public void setCarid(String carid) {
        this.carid = carid;
    }

    public String getCarid() {
        return carid;
    }

    public void setNeedall(int needall) {
        this.needall = needall;
    }

    public int getNeedall() {
        return needall;
    }
}
