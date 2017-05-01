package com.virtualevan.wifither.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.virtualevan.wifither.R;
import com.virtualevan.wifither.core.Client;
import com.virtualevan.wifither.core.DeviceModel;
import com.virtualevan.wifither.core.Server;

import java.util.ArrayList;

/**
 * Created by VirtualEvan on 31/03/2017.
 */

public class DevicesAdapter extends ArrayAdapter<DeviceModel> implements AdapterView.OnItemLongClickListener{
    private String ip;
    private String port;

    public DevicesAdapter(Context context, ArrayList<DeviceModel> macs, String ip, String port){
        super( context, 0, macs );
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> l, View v, final int position, long id) {
        //Sitting here waiting to be overridden
        return true;
    }

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

        // Populate the data into the template view using the data object
        tv_device.setText(device.getName());
        tv_mac.setText(device.getMac());
        sw_device.setChecked(device.getSwitch());


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
                                new Client().execute( "6"+device.getMac(), ip, port );
                            }
                            else {
                                new Client().execute( "7"+device.getMac(), ip, port );
                            }
                            device.setSwitch( isChecked );
                            firstTime = false;
                            new Server( sw_device, isChecked? 0 : 1 ).execute( ip, port );
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
