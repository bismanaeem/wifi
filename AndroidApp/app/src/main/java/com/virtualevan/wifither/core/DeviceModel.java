package com.virtualevan.wifither.core;

/**
 * Custom item of DevicesList
 * TODO:Includes Name and MAC
 */
//Custom element of the DevicesList/DevicesAdapter
public class DeviceModel {
    private String mac;
    private String name;
    //TODO private Boolean active; //switch

    /*TODO CONSTRUCTOR*/
    public DeviceModel(String device, String mac/*TODO, Boolean active*/){
        this.name = device;
        this.mac = mac;
        //this.active = active;
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

    /*TODO DEVICE
    public Boolean getSwitch(){
        return active;
    }

    public void setSwitch(Boolean active){
        this.active = active;
    }*/
}
