package com.pein.pov.virualleddisplay;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;



/**
 * Created by Deshan on 6/28/2016.
 */
public class BluetoothHandle extends Thread {
    //configure socket connection
    private final BluetoothSocket mmSocket;
    private static BluetoothDevice mmDevice;
    private static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    //communication
    private static OutputStream mmOutStream;

    //get default bluetooth adaptor
    BluetoothAdapter mBluetoothAdapter;

    public  BluetoothHandle(BluetoothDevice device){
        mmDevice = device;
        BluetoothSocket tmp = null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        try{
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        }
        catch (IOException e){
            Log.e("BTConnection","Eroor in Connecting to Socket");
        }
        mmSocket = tmp;
        if(mmSocket==null){
            Log.e("BTConnection","Not Connected");
        }
    }

    private void cancelDiscovery(){
        mBluetoothAdapter.cancelDiscovery();
    }

    //connect device through the socket
    private void connectToDevice(){
        try{
            mmSocket.connect();
        }
        catch (IOException e){
            Log.e("BTSocketConnection","Cannot connect to the socket");
            try{
                mmSocket.close();
            }
            catch (IOException v){
                Log.e("BTSocketConnection","Cannot close the socket");
            }
        }
    }

    //make sending configrations
    private void configOutputStream(){
        OutputStream outSt = null;
        try{
            outSt = mmSocket.getOutputStream();
        }
        catch (IOException e){
            Log.e("BTSocketConnection","Output stream config");
        }
        mmOutStream = outSt;
    }

    /* Call this from the main activity to send data to the remote device */
    public static void write(String sendString) {
        byte [] sendData = sendString.getBytes(Charset.forName("US-ASCII"));
        try {
            mmOutStream.write(sendData);
        } catch (IOException e) {
            Log.e("BTSocketConnection","Writing Output stream faied");
        }
        catch (Exception e){
            Log.e("BTSocketConnection",e.toString());
        }
    }

    public void run(){
        cancelDiscovery();
        connectToDevice();
        configOutputStream();
    }
}

