package com.virtualevan.wifither.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.virtualevan.wifither.R;
import com.virtualevan.wifither.core.Client;
import com.virtualevan.wifither.core.DeviceModel;
import com.virtualevan.wifither.core.Server;

import java.util.ArrayList;
import java.util.regex.Pattern;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class DevicesActivity extends AppCompatActivity {

    private ArrayList<DeviceModel> devices;
    private DevicesAdapter devicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        //Sets the toolbar name
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Load views and  create custom adapter
        ListView lv_macslist = (ListView) this.findViewById( R.id.lv_macslist );
        lv_macslist.setLongClickable( true );
        devices = new ArrayList<DeviceModel>();
        devicesAdapter = new DevicesAdapter( this, devices, getIntent().getStringExtra( "ip" ), getIntent().getStringExtra( "port" ), getIntent().getStringExtra( "pass" ) );
        lv_macslist.setAdapter(devicesAdapter);
        final FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_speed_dial);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        //Remove or Edit onItemLongClickListener
        lv_macslist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectAction( position );
                devicesAdapter.notifyDataSetChanged();
                return false;
            }
        });

        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch( menuItem.getItemId() ) {
                    case R.id.action_apply:
                        applyChanges();
                        new Server( fabSpeedDial, 0, progressBar ).execute( getIntent().getStringExtra( "ip" ), getIntent().getStringExtra( "port" ) );
                        break;
                    case R.id.action_add:
                        addDevice();
                        devicesAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
    }

    //Save device objects using json
    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences.Editor saver = getPreferences( Context.MODE_PRIVATE ).edit();
        Gson gson = new Gson();

        String serialized_macs = gson.toJson(devices);
        saver.putString( "device_list", serialized_macs );
        saver.apply();
    }

    //Save device objects using json
    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences loader = this.getPreferences( Context.MODE_PRIVATE );
        Gson gson = new Gson();

        String serialized_macs = loader.getString( "device_list", "[]" );

        devices.clear();
        devices.addAll( (ArrayList<DeviceModel>) gson.fromJson( serialized_macs, new TypeToken<ArrayList<DeviceModel>>(){}.getType() ) );
        devicesAdapter.notifyDataSetChanged();
    }

    //Alertdialog for edit or delete
    public void selectAction(final int position){
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder( this );
        final Resources res = getResources();

        dlgBuilder.setTitle( res.getString( R.string.select_action ) );
        dlgBuilder.setNegativeButton( res.getString( R.string.cancel ), null );
        dlgBuilder.setPositiveButton(res.getString(R.string.edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                modifyDevice( position );
            }
        });
        dlgBuilder.setNeutralButton(res.getString(R.string.remove), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeDevice( position );
            }
        });
        dlgBuilder.create().show();
    }

    //Add a device to the devices adapter
    public void addDevice(String device, String mac){
        devicesAdapter.add( new DeviceModel( device, mac, false ) );
        devicesAdapter.notifyDataSetChanged();
    }

    //Alertdialog which obtains the info for the device creation
    public void addDevice(){
        final Resources res = getResources();
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder( this );
        final AlertDialog alertDialog;
        LayoutInflater factory = LayoutInflater.from(this);

        /*Loading dialog layout*/
        final View dialogView = factory.inflate(R.layout.custom_dialog_device, null);
        final EditText et_device = (EditText) dialogView.findViewById( R.id.et_name );
        final EditText et_mac = (EditText) dialogView.findViewById( R.id.et_mac );

        dlgBuilder.setTitle( res.getString( R.string.new_device ) );
        dlgBuilder.setView( dialogView );
        dlgBuilder.setNegativeButton( res.getString( R.string.cancel ), null );
        dlgBuilder.setPositiveButton( res.getString( R.string.add ), null);
        alertDialog = dlgBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
        Button bt_add = (Button) alertDialog.getButton( AlertDialog.BUTTON_POSITIVE );
        bt_add.setEnabled( false );

        et_mac.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Disable button if the MAC does not match the pattern
                alertDialog.getButton( AlertDialog.BUTTON_POSITIVE ).setEnabled( checkMac( et_mac.getText().toString() ) );
            }
        });

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( et_device.getText().toString().trim().isEmpty() ){
                    Toast.makeText( DevicesActivity.this, res.getString( R.string.empty_device ), Toast.LENGTH_SHORT ).show();
                }
                else {
                    addDevice( et_device.getText().toString(), et_mac.getText().toString().toUpperCase() );
                    alertDialog.dismiss();
                }
            }
        });


    }

    //Pattern check for the MAC specified during device creations and edits
    public Boolean checkMac( String mac ) {
        return Pattern.compile("([0-9a-fA-F][0-9a-fA-F]:){5}[0-9a-fA-F][0-9a-fA-F]").matcher(mac).matches();
    }

    //Alertdialog for device edits
    public void modifyDevice( final int position ){
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder( this );
        final Resources res = getResources();
        final AlertDialog alertDialog;
        LayoutInflater factory = LayoutInflater.from(this);

        /*Loading dialog layout*/
        final View dialogView = factory.inflate(R.layout.custom_dialog_device, null);
        final EditText et_name = (EditText) dialogView.findViewById( R.id.et_name );
        final EditText et_mac = (EditText) dialogView.findViewById( R.id.et_mac );

        final DeviceModel device = devices.get( position );
        dlgBuilder.setTitle( res.getString( R.string.edit_device) );
        et_name.setText( device.getName() );
        et_mac.setText( device.getMac() );
        et_name.setSelection( et_name.getText().length() );
        dlgBuilder.setView( dialogView );
        dlgBuilder.setNegativeButton( res.getString( R.string.cancel ), null );
        dlgBuilder.setPositiveButton(res.getString(R.string.edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                device.setDevice(et_name.getText().toString());
                device.setMac(et_mac.getText().toString().toUpperCase());
            }
        });
        alertDialog = dlgBuilder.create();

        et_mac.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Disable button if the MAC does not match the pattern
                alertDialog.getButton( AlertDialog.BUTTON_POSITIVE ).setEnabled( checkMac( et_mac.getText().toString() ) );
            }
        });

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }

    //Remove devices from the adapter
    public void removeDevice( final int position ){
        devices.remove( position );
        devicesAdapter.notifyDataSetChanged();
    }

    //Restart the router
    public void applyChanges(){
        new Client().execute( "0", getIntent().getStringExtra( "ip" ), getIntent().getStringExtra( "port" ), getIntent().getStringExtra( "pass" ) );
    }

}
