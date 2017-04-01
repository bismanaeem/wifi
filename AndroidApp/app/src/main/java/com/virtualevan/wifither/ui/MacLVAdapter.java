package com.virtualevan.wifither.ui;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by VirtualEvan on 31/03/2017.
 */

public class MacLVAdapter extends ArrayAdapter {
    private Context context;

    public MacLVAdapter(Context context, String name, String[] mac){
        super( context, R.layout.custom_item_listview_mac, mac );
        this.context = context;
        this.web = web;
        this.web2 = web2;
        this.imageId = imageId;
        this.isChecked = isChecked;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_item_listview_image_text_text, null, true);
    }
}
