package com.virtualevan.wifither.core;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.virtualevan.wifither.ui.MainActivity;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by VirtualEvan on 31/01/2017.
 */

public class Server extends AsyncTask<String, Void, Boolean> {
    private boolean e = false;

    @Override
    public Boolean doInBackground(String... data){

        byte[] message = new byte[4096];
        DatagramPacket packet = new DatagramPacket(message, message.length);
        DatagramSocket socket = null;
        String test="";

        try
        {
            socket = new DatagramSocket( Integer.parseInt(data[1]) );
            socket.setSoTimeout(3000);

            socket.receive(packet);

            test = new String(message, 0, packet.getLength());
                Log.v("RECEIVED",test);
            //Intent intent = new Intent();
            //intent.setAction(Main.MESSAGE_RECEIVED);
            //intent.putExtra(Main.MESSAGE_STRING, new String(message, 0, packet.getLength()));
            //Main.MainContext.getApplicationContext().sendBroadcast(intent);

        }
        catch (Exception e)
        {
            Log.v("TIMEOUT","TIMEOUT");
        }
        finally
        {
            if (socket != null)
            {
                socket.close();
            }
        }

        return null;

    }

}
