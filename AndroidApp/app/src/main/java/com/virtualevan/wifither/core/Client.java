package com.virtualevan.wifither.core;


import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by VirtualEvan on 29/01/2017.
 */

public class Client extends AsyncTask<String, Void, Boolean> {
    @Override
    protected Boolean doInBackground(String... data) {
        DatagramSocket datagramSocket = null;

        try
        {
            DESKeySpec keySpec = new DESKeySpec(data[3].getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            //Cipher needs to be created in each thread
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = Base64.encode(cipher.doFinal(data[0].getBytes("UTF8")), Base64.DEFAULT);

            //Create a socket with the given port
            datagramSocket = new DatagramSocket(Integer.parseInt(data[2]));

            //Create datagram to be sent
            DatagramPacket datagramPacket;
            datagramPacket = new DatagramPacket(encrypted, encrypted.length, InetAddress.getByName(data[1]), Integer.parseInt(data[2]));

            //Send datagram
            datagramSocket.send(datagramPacket);
            Log.d("DES", new String(cipher.doFinal(data[0].getBytes("UTF8")), 0, cipher.doFinal(data[0].getBytes("UTF8")).length));
            Log.d("BASE64", new String(encrypted, 0, encrypted.length));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (datagramSocket != null)
            {
                //Close socket when transmission finishes
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
