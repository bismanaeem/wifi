package com.virtualevan.wifither.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;

import com.virtualevan.wifither.R;
import com.virtualevan.wifither.core.Client;
import com.virtualevan.wifither.core.Server;

/*****************************************/
// Connectivity
//
// 0 Wifi restart
// 1 Turn off wifi
// 2 Turn on wifi
// 3 Disable mac filter
// 4 Set MAC filter to deny
// 5 Set MAC filter to allow
// 6 Add Device
// 7 Remove Device
//
/*****************************************/



public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        Spinner sp_macfilter = (Spinner) findViewById(R.id.sp_macfilter);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> macfilter_adapter = ArrayAdapter.createFromResource(this,
                R.array.sp_macfilter, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        macfilter_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        sp_macfilter.setAdapter(macfilter_adapter);

        //Load views
        final Button bt_manage = (Button) findViewById( R.id.bt_manage );
        final Button bt_config = (Button) findViewById( R.id.bt_config );
        final Switch sw_wifi = (Switch) findViewById( R.id.sw_wifi );
        final EditText et_ip = (EditText) findViewById( R.id.et_ip );
        final EditText et_port = (EditText) findViewById( R.id.et_port );
        final EditText et_pass = (EditText) findViewById( R.id.et_pass );

        //Prevents the Switch from being triggered by a logical check
        sw_wifi.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
            sw_wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                boolean firstTime = true;
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //firstTime prevents the Switch from being triggered during the rollback
                    if(firstTime) {
                        if (isChecked) {
                            new Client().execute("2", et_ip.getText().toString().trim(), et_port.getText().toString().trim(), et_pass.getText().toString().trim());
                        } else {
                            new Client().execute("1", et_ip.getText().toString().trim(), et_port.getText().toString().trim(), et_pass.getText().toString().trim());
                        }
                        firstTime = false;
                        new Server( sw_wifi, isChecked? 0 : 1, progressBar ).execute( et_ip.getText().toString(), et_port.getText().toString() );
                    }
                }
            });

            return false;
            }

        });

        //Prevents the Spinner from being triggered by a logical selection
        sp_macfilter.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final Spinner sp_macfilter = (Spinner) findViewById(R.id.sp_macfilter);
                final int selectedItem = sp_macfilter.getSelectedItemPosition();

                sp_macfilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    EditText et_ip = (EditText) findViewById( R.id.et_ip );
                    EditText et_port = (EditText) findViewById( R.id.et_port );
                    boolean firstTime = true;
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //firstTime prevents the Spinner from being triggered during the rollback
                        if (firstTime) {
                            //id are [0-2] values +2 to make it match the selected message value
                            new Client().execute(Long.toString(id + 3), et_ip.getText().toString().trim(), et_port.getText().toString().trim(), et_pass.getText().toString().trim());
                            firstTime = false;
                            new Server( sp_macfilter, selectedItem, progressBar ).execute( et_ip.getText().toString(), et_port.getText().toString() );
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        //Do nothing
                    }
                });

                return false;
            }

        });

        //Start the activity
        bt_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sends ip and port to DevicesActivity
                Intent devicesActivity = new Intent( MainActivity.this, DevicesActivity.class);
                devicesActivity.putExtra( "ip", ((EditText) findViewById( R.id.et_ip )).getText().toString().trim() );
                devicesActivity.putExtra( "port", ((EditText) findViewById( R.id.et_port )).getText().toString().trim() );
                devicesActivity.putExtra( "pass", ((EditText) findViewById( R.id.et_pass )).getText().toString().trim() );

                startActivity( devicesActivity );
            }
        });

        //Enable the URI configuration
        bt_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!et_ip.isEnabled()) {
                    et_ip.setEnabled(true);
                    et_port.setEnabled(true);
                    et_pass.setEnabled(true);
                    bt_config.setText(getResources().getString( R.string.bt_config_confirm ));
                }
                else {
                    et_ip.setEnabled(false);
                    et_port.setEnabled(false);
                    et_pass.setEnabled(false);
                    bt_config.setText(getResources().getString( R.string.bt_config ));

                    //Saves the configuration when confirmed
                    SharedPreferences.Editor saver = getPreferences( Context.MODE_PRIVATE ).edit();
                    saver.putString( "ip", et_ip.getText().toString().trim() );
                    saver.putString( "port", et_port.getText().toString().trim() );
                    saver.apply();
                }
            }
        });
    }


    //Save the configuration
    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences.Editor saver = getPreferences( Context.MODE_PRIVATE ).edit();
        EditText et_ip = (EditText) findViewById( R.id.et_ip );
        EditText et_port = (EditText) findViewById( R.id.et_port );
        EditText et_pass = (EditText) findViewById( R.id.et_pass );
        Switch sw_wifi = (Switch) findViewById( R.id.sw_wifi );
        Spinner sp_macfilter = (Spinner) findViewById( R.id.sp_macfilter );

        if(!et_ip.isEnabled()){
            saver.putString( "ip", et_ip.getText().toString().trim() );
            saver.putString( "port", et_port.getText().toString().trim() );
            saver.putString( "pass", et_pass.getText().toString().trim() );
        }
        saver.putBoolean( "wifi_status", sw_wifi.isChecked() );
        saver.putInt( "mac_filter", sp_macfilter.getSelectedItemPosition() );

        saver.apply();
    }

    //Load the configuration
    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences loader = getPreferences( Context.MODE_PRIVATE );
        EditText et_ip = (EditText) findViewById( R.id.et_ip );
        EditText et_port = (EditText) findViewById( R.id.et_port );
        EditText et_pass = (EditText) findViewById( R.id.et_pass );
        Switch sw_wifi = (Switch) findViewById( R.id.sw_wifi );
        Spinner sp_macfilter = (Spinner) findViewById( R.id.sp_macfilter );

        et_ip.setText( loader.getString( "ip", null ) );
        et_port.setText( loader.getString( "port", null ) );
        et_pass.setText( loader.getString( "pass", null ) );
        sw_wifi.setChecked( loader.getBoolean( "wifi_status", false ) );
        sp_macfilter.setSelection( loader.getInt( "mac_filter", 0 ) );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu( menu );

        this.getMenuInflater().inflate( R.menu.main_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        final EditText et_ip = (EditText) findViewById( R.id.et_ip );
        final EditText et_port = (EditText) findViewById( R.id.et_port );
        final EditText et_pass = (EditText) findViewById( R.id.et_pass );

        switch( menuItem.getItemId() ) {
            case R.id.action_apply:
                new Client().execute( "0", et_ip.getText().toString().trim(), et_port.getText().toString().trim(), et_pass.getText().toString().trim() );
                new Server( progressBar, 0, progressBar ).execute( et_ip.getText().toString().trim(), et_port.getText().toString().trim() );
                break;
            case R.id.action_sync:
                //TODO: SYNC
                new Client().execute( "0", et_ip.getText().toString().trim(), et_port.getText().toString().trim(), et_pass.getText().toString().trim() );
                break;
        }

        return true;
    }
}
