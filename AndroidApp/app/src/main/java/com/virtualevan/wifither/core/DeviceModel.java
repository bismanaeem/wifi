package com.virtualevan.wifither.core;

/**
 * Created by VirtualEvan on 01/04/2017.
 */

public class DeviceModel {
    private String mac;
    private String name;
    private Boolean active; //switch

    /*CONSTRUCTOR*/
    public DeviceModel(String device, String mac, Boolean active){
        this.name = device;
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
    public String getName(){
        return name;
    }

    public void setDevice(String name){
        this.name = name;
    }

    /*DEVICE*/
    public Boolean getSwitch(){
        return active;
    }

    public void setSwitch(Boolean active){
        this.active = active;
    }
}
