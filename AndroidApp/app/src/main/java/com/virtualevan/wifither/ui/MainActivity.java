package com.virtualevan.wifither.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.virtualevan.wifither.R;
import com.virtualevan.wifither.core.Client;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Switch sw_wifi = (Switch) this.findViewById( R.id.sw_wifi );


        sw_wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditText et_ip = (EditText) findViewById( R.id.et_ip );
                EditText et_port = (EditText) findViewById( R.id.et_port );
                if( isChecked ){
                    new Client().execute("1", et_ip.getText().toString(), et_port.getText().toString() );
                }
                else{
                    new Client().execute("0", et_ip.getText().toString(), et_port.getText().toString() );
                }
            }
        });
    }
}
