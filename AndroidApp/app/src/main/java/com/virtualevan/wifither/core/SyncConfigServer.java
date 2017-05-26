package com.virtualevan.wifither.core;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.virtualevan.wifither.R;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

/**
 * Receives actual config from the router
 * and updates the view of the app
 *
 * 0 MAC filter disabled
 * 1 MAC filter set to deny
 * 2 MAC filter set to allow
 */

public class SyncConfigServer extends AsyncTask<String, Void, Boolean> {
    private int rollback;
    private View object;
    private ProgressBar progressBar;
    private String messageString="";
    private boolean portError;

    //Receives the pressed button/switch/spinner, its status and its bound progress bar, then puts them in a local variable
    public SyncConfigServer(View v, int rollback, ProgressBar progressBar ){
        this.object = v;
        this.rollback = rollback;
        this.progressBar = progressBar;
    }

    //Disables the view to prevent the user interaction, and enables the progress bar
    @Override
    public void onPreExecute(){
        object.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    //Wait 10 seconds for a response or timeout
    @Override
    public Boolean doInBackground(String... data){

        //Prepare a packet to be received
        byte[] message = new byte[4096];
        DatagramPacket packet = new DatagramPacket(message, message.length);
        DatagramSocket socket = null;

        try
        {
            //Create socket where the packet will be received
            socket = new DatagramSocket( Integer.parseInt(data[1]) );
            socket.setSoTimeout(10000);

            //Listen for 10 seconds
            socket.receive(packet);

            messageString = new String(message, 0, packet.getLength());
                Log.d("RECEIVED",messageString);
        }
        catch (SocketTimeoutException ste)
        {
            Log.d("TIMEOUT","TIMEOUT");
            return false;
        }
        catch (NumberFormatException nfe)
        {
            portError = true;
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
                //Close socket when finished
                socket.close();
            }
        }

        return true;

    }

    //If a packet is received before 10 seconds, show a confirmation error,
    //otherwise show an error message and do rollback to the previous position of the view.
    //Other non related error also show an error message
    @Override
    public void onPostExecute( Boolean result ){
        if(result){
            Log.d("CONNECTION","OK");
            try{
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
                //Used to print the message if an Exception is not thrown
                result = false;
            }
            catch (NumberFormatException nfe){
                Snackbar.make(object , object.getResources().getString(R.string.wrong_password), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
            finally{
                //messageString successfully built? print : dont
                if(!result){
                    Toast.makeText(object.getContext() , messageString, Toast.LENGTH_LONG).show();
                }
            }
        }
        else{
            Log.d("CONNECTION","FAIL");
            if(object instanceof Switch){
                ((Switch) object).setChecked( rollback!=0 );
            }
            else if(object instanceof Spinner){
                ((Spinner) object).setSelection( rollback );
            }
            if(portError){
                Toast.makeText( object.getContext(), object.getResources().getString(R.string.port_error ), Toast.LENGTH_LONG ).show();
            }
            else{
                Snackbar.make(object , object.getResources().getString(R.string.timeout), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        }
        //Disable the progress bar and enable de view
        progressBar.setVisibility(View.INVISIBLE);
        object.setEnabled(true);

    }

}
