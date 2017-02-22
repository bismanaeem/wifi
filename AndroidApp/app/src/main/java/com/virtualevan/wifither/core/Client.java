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
    protected Boolean doInBackground(String... data) {
        DatagramSocket datagramSocket = null;

        try
        {
            datagramSocket = new DatagramSocket(4848);

            DatagramPacket datagramPacket;
            datagramPacket = new DatagramPacket(data[0].getBytes(), data[0].length(), InetAddress.getByName(data[1]), Integer.parseInt(data[2]));

            datagramSocket.send(datagramPacket);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (datagramSocket != null)
            {
                datagramSocket.close();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }
}
