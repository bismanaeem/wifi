package com.virtualevan.wifither.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.virtualevan.wifither.R;
import com.virtualevan.wifither.core.Client;
import com.virtualevan.wifither.core.DeviceModel;
import com.virtualevan.wifither.core.Server;

import java.util.ArrayList;

/**
 * Custom adapter used to custom items as a list
 * Shows Device, MAC, Switch and LoadBar of each item
 */
//A custom adapter which allows the list to have elements with their bound Switches asn progress bars
public class DevicesAdapter extends ArrayAdapter<DeviceModel> implements AdapterView.OnItemLongClickListener{
    private String ip;
    private String port;
    private String pass;

    public DevicesAdapter(Context context, ArrayList<DeviceModel> macs, String ip, String port, String pass){
        super( context, 0, macs );
        this.ip = ip;
        this.port = port;
        this.pass = pass;
    }

    //Function bridging with DevicesActivity
    @Override
    public boolean onItemLongClick(AdapterView<?> l, View v, final int position, long id) {
        //Sitting here waiting to be overridden
        return true;
    }

    //Function which load the view and returns it to be rendered
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DeviceModel device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_item_listview_device, parent, false);
        }
        // Lookup view for data population
        TextView tv_device = (TextView) convertView.findViewById(R.id.device);
        TextView tv_mac = (TextView) convertView.findViewById(R.id.mac);
        final Switch sw_device = (Switch) convertView.findViewById(R.id.sw_device);
        final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);


        // Populate the data into the template view using the data object
        tv_device.setText(device.getName());
        tv_mac.setText(device.getMac());
        //TODO
        //sw_device.setChecked(device.getSwitch());


        // Cache row position inside the button using `setTag`
        sw_device.setTag(position);
        // Attach the click event handler
        sw_device.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                sw_device.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    boolean firstTime = true;
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //firstTime prevents the Switch from being triggered during the rollback
                        if(firstTime) {
                            DeviceModel device = getItem( (Integer) buttonView.getTag() );
                            if( isChecked ) {
                                new Client().execute( "6"+device.getMac(), ip, port, pass );
                            }
                            else {
                                new Client().execute( "7"+device.getMac(), ip, port, pass );
                            }
                            //TODO: device.setSwitch( isChecked );
                            firstTime = false;
                            new Server( sw_device, isChecked? 0 : 1, progressBar ).execute( ip, port );
                        }

                    }
                });

                return false;
            }

        });

        // Return the completed view to render on screen
        return convertView;
    }
}
