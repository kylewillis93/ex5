package com.example.bluetoothfiletransfer;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Handler;
import static com.example.bluetoothfiletransfer.Constants.BLUETOOTH_CONNECTED;

import java.util.ArrayList;

// Adds a student to the student array list.


public class ConnectThread extends Thread {
    private BluetoothSocket mmSocket = null;
    private final BluetoothDevice mmDevice;
    private Context context;
    private boolean connected= false;
    private  ArrayList<String> outputBuffer;

    public ConnectThread(BluetoothDevice device, Context context) {
        outputBuffer = new ArrayList<String>();
        this.context = context;
        Log.i("Connection Thread", "connection thread initializing");
        BluetoothSocket tmp = null;
        mmDevice = device;
        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("d374bfaf-e97b-4bf2-af5d-f58038109686"));
//            connected = true;
        } catch (IOException e) {
            Log.e("Bluetooth connection", "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() { ;
        try {
            // Connect to the remote device through the socket. This calls blocks
            // until it succeeds or throws an exception.
            Log.i("Connection Thread", "attempting to create socket connection with pi");
            mmSocket.connect();
            connected = true;
            manageMyConnectedSocket();
//            Toast.makeText(this.context, "Connected to Raspberry Pi", Toast.LENGTH_SHORT).show();
        } catch (IOException connectException) {

            // Unable to connect; close the socket and return.
            try {
                Log.i("Bluetooth connection", "Got here!!!!!!!");
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("Bluetooth Connection", "Could not close the client socket", closeException);
            }
            return;
        }
        // The connection attempt succeeded. Perform work associated with
    }
//
    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("Bluetooth Connection", "Could not close the client socket", e);
        }
    }

    public void sendData(String data) {
        if (connected) {
            outputBuffer.add(data);
        }
    }


    private void manageMyConnectedSocket() {
        try {
            OutputStream outStream = mmSocket.getOutputStream();
            while (true) {

                if (!outputBuffer.isEmpty()){
                    String s = outputBuffer.get(0);
                    outputBuffer.remove(0);
//                    Log.i("writing to bluetooth",s);
                    if (s != null) {
                        outStream.write(s.getBytes());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}