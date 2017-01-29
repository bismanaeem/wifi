package com.virtualevan.wifither.core;

import android.location.Address;
import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by VirtualEvan on 29/01/2017.
 */

public class Client extends AsyncTask<String, Void, Boolean> {
    @Override
    public Boolean doInBackground(String... message) {
        DatagramSocket ds = null;

        try
        {
            ds = new DatagramSocket();
            DatagramPacket dp;
            dp = new DatagramPacket(message[0].getBytes(), message[0].length(), InetAddress.getByName("192.168.0.4"), 4848);
            ds.setBroadcast(true);
            ds.send(dp);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (ds != null)
            {
                ds.close();
            }
        }
        return null;
    }

    @Override
    public void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }
}
