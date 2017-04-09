package com.virtualevan.wifither.ui;

import android.content.Context;
import android.view.LayoutInflater;
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
        // Populate the data into the template view using the data object
        tv_device.setText(device.getName());
        tv_mac.setText(device.getMac());

        // Lookup view for data population
        Switch sw_mac = (Switch) convertView.findViewById(R.id.sw_mac);
        // Cache row position inside the button using `setTag`
        sw_mac.setTag(position);
        // Attach the click event handler
        sw_mac.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DeviceModel device = getItem( (Integer) buttonView.getTag() );
                if( isChecked ) {
                    new Client().execute( device.getMac(), ip, port );
                }
                else {
                    new Client().execute( device.getName(), ip, port );
                }

                //new Server().execute( et_ip.getText().toString(), et_port.getText().toString() );

                buttonView.setEnabled(true);

            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
