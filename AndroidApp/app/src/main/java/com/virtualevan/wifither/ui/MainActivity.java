package com.virtualevan.wifither.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.virtualevan.wifither.R;
import com.virtualevan.wifither.core.Client;
import com.virtualevan.wifither.core.Server;

/*****************************************/
// Connectivity
//
// 0 Turn off wifi
// 1 Turn on wifi
// 2 Disable mac filter
// 3 Set MAC filter to deny
// 4 Set MAC filter to allow
// 5 Update MAC list
//
/*****************************************/



public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner sp_macfilter = (Spinner) findViewById(R.id.sp_macfilter);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> macfilter_adapter = ArrayAdapter.createFromResource(this,
                R.array.sp_macfilter, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        macfilter_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sp_macfilter.setAdapter(macfilter_adapter);

        Switch sw_wifi = (Switch) this.findViewById( R.id.sw_wifi );
        Button bt_update = (Button) this.findViewById( R.id.bt_update );
        sw_wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setEnabled(false);

                EditText et_ip = (EditText) findViewById( R.id.et_ip );
                EditText et_port = (EditText) findViewById( R.id.et_port );

                if( isChecked ) {
                    new Client().execute( "1", et_ip.getText().toString(), et_port.getText().toString() );
                }
                else {
                    new Client().execute( "0", et_ip.getText().toString(), et_port.getText().toString() );
                }

                new Server().execute( et_ip.getText().toString(), et_port.getText().toString() );

                buttonView.setEnabled(true);

            }
        });

        sp_macfilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //id are [0-2] values +2 to make it match the selected message value
                new Client().execute( Long.toString(id+2), "192.168.0.166", "4848");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });

        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity( new Intent( MainActivity.this, MacsActivity.class) );

                //EditText et_mac = (EditText) findViewById( R.id.et_mac );
                //EditText et_ip = (EditText) findViewById( R.id.et_ip );
                //EditText et_port = (EditText) findViewById( R.id.et_port );

                //new Client().execute( "01-00-5e-00-00-02,01-00-5e-00-00-16,01-00-5e-00-00-fb,01-00-5e-00-00-fc,01-00-5e-7f-ff-fa", et_ip.getText().toString(), et_port.getText().toString() );
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences.Editor saver = getPreferences( Context.MODE_PRIVATE ).edit();
        EditText et_ip = (EditText) findViewById( R.id.et_ip );
        EditText et_port = (EditText) findViewById( R.id.et_port );
        Switch sw_wifi = (Switch) findViewById( R.id.sw_wifi );
        Spinner sp_macfilter = (Spinner) findViewById( R.id.sp_macfilter );

        saver.putString( "ip", et_ip.getText().toString() );
        saver.putString( "port", et_port.getText().toString() );
        saver.putBoolean( "wifi_status", sw_wifi.isChecked() );
        saver.putInt( "mac_filter", sp_macfilter.getSelectedItemPosition() );

        saver.apply();
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences loader = getPreferences( Context.MODE_PRIVATE );
        EditText et_ip = (EditText) findViewById( R.id.et_ip );
        EditText et_port = (EditText) findViewById( R.id.et_port );
        Switch sw_wifi = (Switch) findViewById( R.id.sw_wifi );
        Spinner sp_macfilter = (Spinner) findViewById( R.id.sp_macfilter );

        et_ip.setText( loader.getString( "ip", null ) );
        et_port.setText( loader.getString( "port", null ) );
        sw_wifi.setChecked( loader.getBoolean( "wifi_status", false ) );
        sp_macfilter.setSelection( loader.getInt( "mac_filter", 0 ));

    }
}
