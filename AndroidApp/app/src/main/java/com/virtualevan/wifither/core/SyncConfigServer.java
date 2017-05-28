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
    private Switch sw_wifi;
    private Spinner mf_spinner;
    private ProgressBar progressBar;
    private String messageString="";
    private boolean portError;

    //Receives the pressed button/switch/spinner, its status and its bound progress bar, then puts them in a local variable
    public SyncConfigServer(Switch sw_wifi, Spinner mf_spinner, ProgressBar progressBar ){
        this.sw_wifi = sw_wifi;
        this.mf_spinner = mf_spinner;
        this.progressBar = progressBar;
    }

    //Disables the view to prevent the user interaction, and enables the progress bar
    @Override
    public void onPreExecute(){
        sw_wifi.setEnabled(false);
        mf_spinner.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    //Wait 10 seconds for a response or timeout
    @Override
    public Boolean doInBackground(String... data){

        //Prepare a packet to be received
        byte[] message = new byte[1024];
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
                mf_spinner.setSelection( Integer.parseInt(messageString) );
                Toast.makeText(progressBar.getContext() , progressBar.getResources().getString(R.string.sync_successful), Toast.LENGTH_LONG).show();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            Log.d("CONNECTION","FAIL");
            if(portError){
                Toast.makeText( progressBar.getContext(), progressBar.getResources().getString(R.string.port_error ), Toast.LENGTH_LONG ).show();
            }
            else{
                Snackbar.make(progressBar , progressBar.getResources().getString(R.string.timeout), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        }
        //Disable the progress bar and enable de view
        progressBar.setVisibility(View.INVISIBLE);
        sw_wifi.setEnabled(true);
        mf_spinner.setEnabled(true);

    }

}
