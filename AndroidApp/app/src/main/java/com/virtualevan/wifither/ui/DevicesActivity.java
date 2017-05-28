package com.virtualevan.wifither.ui;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.virtualevan.wifither.R;
import com.virtualevan.wifither.core.Client;
import com.virtualevan.wifither.core.DeviceModel;
import com.virtualevan.wifither.core.LanguageHandler;
import com.virtualevan.wifither.core.Server;
import com.virtualevan.wifither.core.SyncDevicesServer;

import java.util.ArrayList;
import java.util.Locale;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class DevicesActivity extends AppCompatActivity {

    private ArrayList<DeviceModel> devices;
    private DevicesAdapter devicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_icon);

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
                Animation animation1 = new AlphaAnimation(0.3f, 1.0f);
                animation1.setDuration(4000);
                view.startAnimation(animation1);
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
                    new Client().execute( "0", getIntent().getStringExtra( "ip" ), getIntent().getStringExtra( "port" ), getIntent().getStringExtra( "pass" ) );

                    new Server( fabSpeedDial, 0, progressBar ).execute( getIntent().getStringExtra( "ip" ), getIntent().getStringExtra( "port" ) );

                    break;
                case R.id.action_sync:
                    //TODO: SYNC
                    new Client().execute( "9", getIntent().getStringExtra( "ip" ), getIntent().getStringExtra( "port" ), getIntent().getStringExtra( "pass" ) );

                    new SyncDevicesServer( devices, progressBar ).execute( getIntent().getStringExtra( "ip" ), getIntent().getStringExtra( "port" ) );
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getResources().getConfiguration().getLocales().get(0);
        }
        else {
            locale = getResources().getConfiguration().locale;
        }

        switch( locale.toString() ) {
            case "es":
                menu.findItem(R.id.action_spanish).setEnabled(false);
                break;
            case "en":
                menu.findItem(R.id.action_english).setEnabled(false);
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu( menu );

        this.getMenuInflater().inflate( R.menu.main_menu, menu );
        return true;
    }

    //TODO: MIRAR QUE FUNCIONE BIEN EN DEVICES ACT Y HACERLO PERMANENTE(SALIR Y ENTRAR)
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch( menuItem.getItemId() ) {
            case R.id.action_spanish:
                LanguageHandler.changeLocale(getResources(), "es");
                break;
            case R.id.action_english:
                LanguageHandler.changeLocale(getResources(), "en");
                break;
        }
        recreate();

        return true;
    }

    //Save device objects using json
    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences.Editor saver = getPreferences( Context.MODE_PRIVATE ).edit();
        Gson gson = new Gson();

        //Save locale
        saver.putString( "locale", LanguageHandler.getLocale(getResources()) );

        String serialized_macs = gson.toJson(devices);
        saver.putString( "device_list", serialized_macs );
        saver.apply();
    }

    //Save device objects using json
    @Override
    public void onResume(){
        SharedPreferences loader = getPreferences( Context.MODE_PRIVATE );

        //Load locale before the rest for the activity
        LanguageHandler.changeLocale( getResources(), loader.getString( "locale", "en" ));

        super.onResume();
        Gson gson = new Gson();

        String serialized_macs = loader.getString( "device_list", "[]" );

        devices.clear();
        devices.addAll( (ArrayList<DeviceModel>) gson.fromJson( serialized_macs, new TypeToken<ArrayList<DeviceModel>>(){}.getType() ) );
        devicesAdapter.notifyDataSetChanged();
    }

    //Alertdialog for edit or delete
    public void selectAction(final int position){
        //Get current device
        final DeviceModel device = devices.get( position );

        //Create and show dialog fragment
        FragmentManager fragmentManager = getFragmentManager();
        final ManageDialogFragment dialogFragment = ManageDialogFragment.newInstance( position );
        dialogFragment.show(fragmentManager, "Manage device fragment");
        fragmentManager.executePendingTransactions();
    }

    //Add a device to the devices adapter
    public void addDevice(String device, String mac){
        DeviceModel devToAdd = new DeviceModel( device, mac, false );

        if (devices.contains(devToAdd)){
            Toast.makeText( DevicesActivity.this, getResources().getString( R.string.device_exists ), Toast.LENGTH_SHORT ).show();
        }
        else {
            devicesAdapter.add( devToAdd );
            devicesAdapter.notifyDataSetChanged();
        }
    }

    //Alertdialog which obtains the info for the device creation
    public void addDevice(){
        //Create and show dialog fragment
        Resources res = getResources();
        FragmentManager fragmentManager = getFragmentManager();
        DeviceDialogFragment dialogFragment = DeviceDialogFragment.newInstance( res.getString( R.string.new_device ), res.getString( R.string.add ) );
        dialogFragment.show(fragmentManager, "Add device fragment");
        fragmentManager.executePendingTransactions();
    }

    //Alertdialog for device edits
    public void modifyDevice( final int position ){
        //Get current device
        DeviceModel device = devices.get( position );

        //Create and show dialog fragment
        Resources res = getResources();
        FragmentManager fragmentManager = getFragmentManager();
        DeviceDialogFragment dialogFragment = DeviceDialogFragment.newInstance( res.getString( R.string.edit_device ), res.getString( R.string.edit ), device.getName(), device.getMac(), position );
        dialogFragment.show(fragmentManager, "Edit device fragment");
        fragmentManager.executePendingTransactions();
    }

    //Remove devices from the adapter
    public void removeDevice( final int position ){
        devices.remove( position );
        devicesAdapter.notifyDataSetChanged();
    }

    //Crete de Add listener
    public void setListener(EditText et_name, EditText et_mac, DeviceDialogFragment dialog){
        if( et_name.getText().toString().trim().isEmpty() ){
            Toast.makeText( DevicesActivity.this, getResources().getString( R.string.empty_device ), Toast.LENGTH_SHORT ).show();
        }
        else {
            addDevice( et_name.getText().toString(), et_mac.getText().toString().toUpperCase() );
            dialog.dismiss();
        }
    }

    //Crete de Modify listener
    public void setListener(EditText et_name, EditText et_mac, int position, DeviceDialogFragment dialog){
        //Get current device
        DeviceModel device = devices.get( position );

        if( et_name.getText().toString().trim().isEmpty() ){
            Toast.makeText( DevicesActivity.this, getResources().getString( R.string.empty_device ), Toast.LENGTH_SHORT ).show();
        }
        else {
            device.setDevice(et_name.getText().toString());
            device.setMac(et_mac.getText().toString().toUpperCase());
            devicesAdapter.notifyDataSetChanged();
            dialog.dismiss();
        }
    }

}
