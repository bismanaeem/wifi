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
import com.virtualevan.wifither.ui.DevicesAdapter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Receives MAC addresses from the router
 * and updates the view of the app
 *
 * Each MAC address is used to set the correspondent
 * switches to true (Switch are set off by default)
 */

public class SyncDevicesServer extends AsyncTask<String, Void, Boolean> {
    private ArrayList<DeviceModel> devices;
    private ProgressBar progressBar;
    private List<String> macsList= new ArrayList<String>();
    private boolean portError;

    //Receives the pressed button/switch/spinner, its status and its bound progress bar, then puts them in a local variable
    public SyncDevicesServer(ArrayList<DeviceModel> devices, ProgressBar progressBar ){
        this.devices = devices;
        this.progressBar = progressBar;
    }

    //Disables the view to prevent the user interaction, and enables the progress bar
    @Override
    public void onPreExecute(){
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

            //Listen for 10 seconds until "done" message
            String messageString = "";
            do {
                socket.receive(packet);
                messageString = new String(message, 0, packet.getLength());
                macsList.add(messageString);
                Log.d("RECEIVED", messageString);
            } while (!messageString.equals("done"));
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
        if(result) {
            try {
                //Turn switches off
                for (DeviceModel device : devices) {
                    device.setSwitch(false);
                }

                //Turn on switches of the received macs
                for (String mac : macsList) {
                    for (DeviceModel device : devices) {
                        if (device.getMac().equals(mac)) {
                            device.setSwitch(true);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            if(portError){
                Toast.makeText( progressBar.getContext(), progressBar.getResources().getString(R.string.port_error ), Toast.LENGTH_LONG ).show();
            }
            else {
                Snackbar.make( progressBar, progressBar.getResources().getString(R.string.timeout), Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        }

        //Disable the progress bar and enable de view
        progressBar.setVisibility(View.INVISIBLE);
    }

}
