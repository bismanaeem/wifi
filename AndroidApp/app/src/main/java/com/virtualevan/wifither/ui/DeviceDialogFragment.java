package com.virtualevan.wifither.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.virtualevan.wifither.R;

import java.util.regex.Pattern;

/**
 * Created by VirtualEvan on 23/05/2017.
 */

public class DeviceDialogFragment extends DialogFragment {

    //Used for Add device
    static DeviceDialogFragment newInstance(String title, String button) {
        DeviceDialogFragment deviceDialog = new DeviceDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("button", button);
        args.putString("name", "");
        args.putString("mac", "");
        deviceDialog.setArguments(args);

        return deviceDialog;
    }

    //Used for Edit device
    static DeviceDialogFragment newInstance(String title, String button, String name, String mac, int position) {
        DeviceDialogFragment deviceDialog = new DeviceDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("button", button);
        args.putString("name", name);
        args.putString("mac", mac);
        args.putInt("position", position);

        deviceDialog.setArguments(args);

        return deviceDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Resources res = getResources();
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder( getActivity() );
        LayoutInflater inflater = LayoutInflater.from( getActivity() );

        /*Loading dialog layout*/
        final View dialogView = inflater.inflate(R.layout.device_fragment, null);

        dlgBuilder.setTitle( getArguments().getString("title") );
        final EditText et_name = (EditText) dialogView.findViewById( R.id.et_name );
        final EditText et_mac = (EditText) dialogView.findViewById( R.id.et_mac );

        if ( savedInstanceState == null ){
            et_name.setText( getArguments().getString("name") );
            et_mac.setText( getArguments().getString("mac") );
        }
        else {
            et_name.setText( savedInstanceState.getString( "NAME" ) );
            et_mac.setText( savedInstanceState.getString( "MAC" ) );
        }

        Log.d("AHORA", "VALOR: " +et_name.getText().toString());

        dlgBuilder.setView( dialogView );
        dlgBuilder.setNegativeButton( res.getString( R.string.cancel ), null );
        dlgBuilder.setPositiveButton( getArguments().getString("button"), null);

        et_name.setSelection( et_name.getText().length() );

        return dlgBuilder.create();
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if(window != null){
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         /*Loading dialog layout*/
        return inflater.inflate(R.layout.device_fragment, null);
    }

    @Override
    public void onStart(){
        super.onStart();

        final Button bt_add = ((AlertDialog) this.getDialog()).getButton(Dialog.BUTTON_POSITIVE);
        final EditText et_name = (EditText) this.getDialog().findViewById( R.id.et_name );
        final EditText et_mac = (EditText) this.getDialog().findViewById( R.id.et_mac );
        bt_add.setEnabled( checkMac( et_mac.getText().toString() ) );

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
                bt_add.setEnabled( checkMac( et_mac.getText().toString() ) );
            }
        });

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( getArguments().getString("name") != "") {
                    ((DevicesActivity)getActivity()).setListener(et_name, et_mac, getArguments().getInt("position"), DeviceDialogFragment.this);
                }
                else {
                    ((DevicesActivity)getActivity()).setListener(et_name, et_mac, DeviceDialogFragment.this);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        final EditText et_name = (EditText) this.getDialog().findViewById( R.id.et_name );
        final EditText et_mac = (EditText) this.getDialog().findViewById( R.id.et_mac );

        savedInstanceState.putString("NAME", et_name.getText().toString());
        savedInstanceState.putString("MAC", et_mac.getText().toString());

        super.onSaveInstanceState(savedInstanceState);
    }

    //Pattern check for the MAC specified during device creations and edits
    private Boolean checkMac( String mac ) {
        return Pattern.compile("([0-9a-fA-F][0-9a-fA-F]:){5}[0-9a-fA-F][0-9a-fA-F]").matcher(mac).matches();
    }
}