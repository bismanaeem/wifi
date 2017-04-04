package com.virtualevan.wifither.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.virtualevan.wifither.R;
import com.virtualevan.wifither.core.DeviceModel;

import java.util.ArrayList;

/**
 * Created by VirtualEvan on 31/03/2017.
 */

public class MacsAdapter extends ArrayAdapter<DeviceModel> implements AdapterView.OnItemLongClickListener{
    private Context context;

    public MacsAdapter(Context context, ArrayList<DeviceModel> macs){
        super( context, 0, macs );
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> l, View v, final int position, long id) {
        //Sitting here waiting to be overridden
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DeviceModel mac = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_item_listview_mac, parent, false);
        }
        // Lookup view for data population
        TextView tv_device = (TextView) convertView.findViewById(R.id.device);
        TextView tv_mac = (TextView) convertView.findViewById(R.id.mac);
        // Populate the data into the template view using the data object
        tv_device.setText(mac.getDevice());
        tv_mac.setText(mac.getMac());
        // Return the completed view to render on screen
        return convertView;
    }
}
