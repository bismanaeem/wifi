package com.virtualevan.wifither.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.virtualevan.wifither.R;
import com.virtualevan.wifither.core.Client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class MacsActivity extends AppCompatActivity {

    private ArrayList<String> macs;
    private ArrayAdapter<String> macsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView lv_macslist = (ListView) this.findViewById( R.id.lv_macslist );
        lv_macslist.setLongClickable( true );
        macs = new ArrayList<String>();
        macsAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_selectable_list_item, macs);
        lv_macslist.setAdapter( macsAdapter );
        FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        FloatingActionButton fab_update = (FloatingActionButton) findViewById(R.id.fab_update);


        //Remove or Edit onItemLongClickListener
        lv_macslist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectAction( position );
                return false;
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMac();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        fab_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMacs();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        SharedPreferences.Editor saver = getPreferences( Context.MODE_PRIVATE ).edit();

        saver.putStringSet( "mac_list", new HashSet<String>( macs ) );
        saver.apply();
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences loader = this.getPreferences( Context.MODE_PRIVATE );
        Set<String> loaded_macs = loader.getStringSet( "mac_list", new HashSet<String>( macs ) );

        macs.clear();
        macs.addAll( loaded_macs );
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

    public void addMac(){
        final EditText et_mac = new EditText( this );
        final Resources res = getResources();
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder( this );
        final AlertDialog alertDialog;

        dlgBuilder.setTitle( res.getString( R.string.add_mac ) );
        dlgBuilder.setView( et_mac );
        dlgBuilder.setNegativeButton( res.getString( R.string.cancel ), null );
        dlgBuilder.setPositiveButton( res.getString( R.string.add ), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addMac( et_mac.getText().toString().toUpperCase() );
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
        alertDialog.getButton( AlertDialog.BUTTON_POSITIVE ).setEnabled( false );
    }

    public Boolean checkMac( String mac ) {
        return Pattern.compile("([0-9a-fA-F][0-9a-fA-F]:){5}[0-9a-fA-F][0-9a-fA-F]").matcher(mac).matches();
    }

    public void addMac(String mac){
        macsAdapter.add( mac );
    }

    public void modifyMac( final int position ){
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder( this );
        final EditText et_mac = new EditText( this );
        final Resources res = getResources();
        final AlertDialog alertDialog;

        dlgBuilder.setTitle( res.getString( R.string.edit_mac) );
        et_mac.setText( macs.get( position ) );
        et_mac.setSelection( et_mac.getText().length() );
        dlgBuilder.setView( et_mac );
        dlgBuilder.setNegativeButton( res.getString( R.string.cancel ), null );
        dlgBuilder.setPositiveButton(res.getString(R.string.edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                macs.set( position, et_mac.getText().toString().toUpperCase() );
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

    public void updateMacs(){
        for(String mac: macs){
            new Client().execute( mac, "192.168.0.4", "4848" );
        }
    }

}
