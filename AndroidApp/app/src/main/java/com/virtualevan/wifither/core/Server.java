package com.virtualevan.wifither.core;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.virtualevan.wifither.R;
import com.virtualevan.wifither.ui.MainActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * Created by VirtualEvan on 31/01/2017.
 */

public class Server extends AsyncTask<String, Void, Boolean> {
    private int rollback;
    private View object;
    private String messageString="";

    public Server( View v, int rollback ){
        this.object = v;
        this.rollback = rollback;
    }

    @Override
    public void onPreExecute(){
        object.setEnabled(false);
    }

    @Override
    public Boolean doInBackground(String... data){

        byte[] message = new byte[4096];
        DatagramPacket packet = new DatagramPacket(message, message.length);
        DatagramSocket socket = null;


        try
        {
            socket = new DatagramSocket( Integer.parseInt(data[1]) );
            //TODO: CHECKOUT TIME
            socket.setSoTimeout(3000);

            socket.receive(packet);

            messageString = new String(message, 0, packet.getLength());
                Log.d("RECEIVED",messageString);
            //Intent intent = new Intent();
            //intent.setAction(Main.MESSAGE_RECEIVED);
            //intent.putExtra(Main.MESSAGE_STRING, new String(message, 0, packet.getLength()));
            //Main.MainContext.getApplicationContext().sendBroadcast(intent);

        }
        catch (SocketTimeoutException e)
        {
            Log.d("TIMEOUT","TIMEOUT");
            return false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            Log.d("FINALLY","EXECUTED");

            if (socket != null)
            {
                socket.close();
            }
        }

        return true;

    }

    @Override
    public void onPostExecute( Boolean result ){
        if(result){
            Log.d("CONNECTION","OK");
            switch(Integer.parseInt(messageString)){
                case 0:
                    messageString = object.getResources().getString(R.string.wifi_restarted);
                    break;
                case 1:
                    messageString = object.getResources().getString(R.string.wifi_disabled);
                    break;
                case 2:
                    messageString = object.getResources().getString(R.string.wifi_enabled);
                    break;
                case 3:
                    messageString = object.getResources().getString(R.string.mac_filter_disabled);
                    break;
                case 4:
                    messageString = object.getResources().getString(R.string.mac_filter_deny);
                    break;
                case 5:
                    messageString = object.getResources().getString(R.string.mac_filter_allow);
                    break;
                case 6:
                    messageString = object.getResources().getString(R.string.device_added);
                    break;
                case 7:
                    messageString = object.getResources().getString(R.string.device_removed);
                    break;
            }
            Toast.makeText(object.getContext() , messageString, Toast.LENGTH_LONG).show();
        }
        else{
            Log.d("CONNECTION","FAIL");
            if(object instanceof Switch){
                ((Switch) object).setChecked( rollback!=0 );
            }
            else{
                ((Spinner) object).setSelection( rollback );
            }
            Snackbar.make(object , object.getResources().getString(R.string.timeout), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        object.setEnabled(true);
    }

}
