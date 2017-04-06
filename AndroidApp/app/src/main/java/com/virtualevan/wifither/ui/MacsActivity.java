package com.virtualevan.wifither.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.virtualevan.wifither.R;
import com.virtualevan.wifither.core.Client;
import com.virtualevan.wifither.core.DeviceModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class MacsActivity extends AppCompatActivity {

    private ArrayList<DeviceModel> macs;
    private MacsAdapter macsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macs);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        ListView lv_macslist = (ListView) this.findViewById( R.id.lv_macslist );
        lv_macslist.setLongClickable( true );
        macs = new ArrayList<DeviceModel>();
        macsAdapter = new MacsAdapter( this, macs );
        lv_macslist.setAdapter( macsAdapter );

        FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        FloatingActionButton fab_apply = (FloatingActionButton) findViewById(R.id.fab_apply);

        //Remove or Edit onItemLongClickListener
        lv_macslist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectAction( position );
                macsAdapter.notifyDataSetChanged();
                return false;
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMac();
                macsAdapter.notifyDataSetChanged();
            }
        });

        fab_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyChanges();
                //TODO:Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences.Editor saver = getPreferences( Context.MODE_PRIVATE ).edit();
        Gson gson = new Gson();

        String serialized_macs = gson.toJson( macs );
        saver.putString( "mac_list", serialized_macs );
        saver.apply();
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences loader = this.getPreferences( Context.MODE_PRIVATE );
        Gson gson = new Gson();

        String serialized_macs = loader.getString( "mac_list", "[]" );

        macs.clear();
        macs.addAll( (ArrayList<DeviceModel>) gson.fromJson( serialized_macs, new TypeToken<ArrayList<DeviceModel>>(){}.getType() ) );
        macsAdapter.notifyDataSetChanged();
    }

    public void selectAction(final int position){
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder( this );
        final Resources res = getResources();

        dlgBuilder.setTitle( res.getString( R.string.select_action ) );
        dlgBuilder.setNegativeButton( res.getString( R.string.cancel ), null );
        dlgBuilder.setPositiveButton(res.getString(R.string.edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                modifyMac( position );
            }
        });
        dlgBuilder.setNeutralButton(res.getString(R.string.remove), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeMac( position );
            }
        });
        dlgBuilder.create().show();
    }

    public void addMac(String device, String mac){
        macsAdapter.add( new DeviceModel( device, mac, false ) );
        macsAdapter.notifyDataSetChanged();
    }

    public void addMac(){
        final Resources res = getResources();
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder( this );
        final AlertDialog alertDialog;
        LayoutInflater factory = LayoutInflater.from(this);

        /*Loading dialog layout*/
        final View dialogView = factory.inflate(R.layout.custom_dialog_device, null);
        final EditText et_device = (EditText) dialogView.findViewById( R.id.et_device );
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
                alertDialog.getButton( AlertDialog.BUTTON_POSITIVE ).setEnabled( checkMac( et_mac.getText().toString() ) );
            }
        });

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( et_device.getText().toString().trim().isEmpty() ){
                    Toast.makeText( MacsActivity.this, res.getString( R.string.empty_device ), Toast.LENGTH_SHORT ).show();
                }
                else {
                    addMac( et_device.getText().toString(), et_mac.getText().toString().toUpperCase() );
                    alertDialog.dismiss();
                }
            }
        });


    }

    public Boolean checkMac( String mac ) {
        return Pattern.compile("([0-9a-fA-F][0-9a-fA-F]:){5}[0-9a-fA-F][0-9a-fA-F]").matcher(mac).matches();
    }

    public void modifyMac( final int position ){
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder( this );
        final Resources res = getResources();
        final AlertDialog alertDialog;
        LayoutInflater factory = LayoutInflater.from(this);

        /*Loading dialog layout*/
        final View dialogView = factory.inflate(R.layout.custom_dialog_device, null);
        final EditText et_device = (EditText) dialogView.findViewById( R.id.et_device );
        final EditText et_mac = (EditText) dialogView.findViewById( R.id.et_mac );

        final DeviceModel device = macs.get( position );
        dlgBuilder.setTitle( res.getString( R.string.edit_device) );
        et_device.setText( device.getDevice() );
        et_mac.setText( device.getMac() );
        //TODO:et_mac.setSelection( et_mac.getText().length() );
        dlgBuilder.setView( dialogView );
        dlgBuilder.setNegativeButton( res.getString( R.string.cancel ), null );
        dlgBuilder.setPositiveButton(res.getString(R.string.edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                device.setDevice(et_device.getText().toString());
                device.setMac(et_mac.getText().toString().toUpperCase());
                //TODO:macs.set( position, et_mac.getText().toString().toUpperCase() );
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
                alertDialog.getButton( AlertDialog.BUTTON_POSITIVE ).setEnabled( checkMac( et_mac.getText().toString() ) );
            }
        });

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.show();
    }

    public void removeMac( final int position ){
        macs.remove( position );
        macsAdapter.notifyDataSetChanged();
    }

    public void applyChanges(){
        //TODO:wifi restart
    }

}
