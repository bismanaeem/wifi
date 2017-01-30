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
    public Boolean doInBackground(String... data) {
        DatagramSocket ds = null;

        try
        {
            ds = new DatagramSocket();
            DatagramPacket dp;
            dp = new DatagramPacket(data[0].getBytes(), data[0].length(), InetAddress.getByName(data[1]), Integer.parseInt(data[2]));
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
