package com.virtualevan.wifither.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
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

public class ManageDialogFragment extends DialogFragment {

    //Used for Edit device
    static ManageDialogFragment newInstance(int position) {
        ManageDialogFragment deviceDialog = new ManageDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("position", position);

        deviceDialog.setArguments(args);

        return deviceDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Resources res = getResources();
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder( getActivity() );

        dlgBuilder.setTitle( res.getString( R.string.select_action ) );
        dlgBuilder.setNegativeButton( res.getString( R.string.cancel ), null );

        dlgBuilder.setPositiveButton(res.getString(R.string.edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((DevicesActivity)getActivity()).modifyDevice( getArguments().getInt("position") );
            }
        });

        dlgBuilder.setNeutralButton(res.getString(R.string.remove), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((DevicesActivity)getActivity()).removeDevice( getArguments().getInt("position") );
            }
        });

        return dlgBuilder.create();
    }
}