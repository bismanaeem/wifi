package com.virtualevan.wifither.core;

/**
 * Created by VirtualEvan on 01/04/2017.
 */

public class DeviceModel {
    private String mac;
    private String device;
    private Boolean active; //switch

    /*CONSTRUCTOR*/
    public DeviceModel(String device, String mac, Boolean active){
        this.device = device;
        this.mac = mac;
        this.active = active;
    }

    /*MAC*/
    public String getMac(){
        return mac;
    }

    public void setMac(String mac){
        this.mac = mac;
    }

    /*DEVICE*/
    public String getDevice(){
        return device;
    }

    public void setDevice(String device){
        this.device = device;
    }

    /*DEVICE*/
    public Boolean getSwitch(){
        return active;
    }

    public void setSwitch(Boolean active){
        this.active = active;
    }
}
