package com.virtualevan.wifither.core;

/**
 * Custom item of DevicesList
 * Includes Name, MAC and Switch status
 */
//Custom element of the DevicesList/DevicesAdapter
public class DeviceModel {
    private String mac;
    private String name;
    private Boolean sw; //switch
    private OnSwitchChangedListener onSwitchChanged = null;


    /*CONSTRUCTOR*/
    public DeviceModel(String device, String mac, Boolean status){
        this.name = device;
        this.mac = mac;
        this.sw = status;
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

    public Boolean getSwitch(){
        return sw;
    }

    public void setSwitchPassive(Boolean status){
        this.sw = status;
    }

    //Set switch which triggers listener
    public void setSwitch(Boolean status){
        this.sw = status;
        if (onSwitchChanged != null) {
            onSwitchChanged.onSwitchChanged();
        }
    }


    //Switch changed listener
    public interface OnSwitchChangedListener{
        void onSwitchChanged();
    }

    public void setOnSwitchChanged(OnSwitchChangedListener onSwitchChanged) {
        this.onSwitchChanged = onSwitchChanged;
    }

    //Equals comparison
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + mac.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DeviceModel)) {
            return false;
        }

        DeviceModel device = (DeviceModel) obj;

        return device.mac.equals(mac);
    }

}


