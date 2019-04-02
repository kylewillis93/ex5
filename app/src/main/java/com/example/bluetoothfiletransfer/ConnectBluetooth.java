package com.example.bluetoothfiletransfer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;
import java.util.logging.Handler;


public class ConnectBluetooth {
    //TODO add toasters to inform user's of bluetooth state
    //TODO add discovery timeout --> unregisterReceiver

    private final BluetoothAdapter bluetoothAdapter;
    public boolean raspberry_pi_paired = false;
    private final String RASPBERRY_PI_MAC_ADDRESS = "B8:27:EB:73:A1:01";
    protected Context context;
    private boolean connected_to_device = false;
    private ConnectThread connectThread;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i("Debug", "onReceived called");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i("Found Bluetooth Device", "Device name: " + deviceName);
                Log.i("Found Bluetooth Device", "Device Mac Address: "+ deviceHardwareAddress);

                // TODO
                // ***** test ******
//                Message msg = mHandler.obtainMessage();
//                Bundle bundle = new Bundle();
//                bundle.putString("TRACK", "test123");
//                msg.setData(bundle);
//                mHandler.sendMessage(msg);


                if (deviceHardwareAddress.equals(RASPBERRY_PI_MAC_ADDRESS)) {
                    Log.i("Found magic keyboard!", "found "+ deviceName);
//                    this.raspberry_pi_paired = true;
                    startConnectionThread(device);
                    //TODO is this the correct way to handle this?
                    context.unregisterReceiver(receiver);
                    bluetoothAdapter.cancelDiscovery();
                }
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.i("Debug Receiver", "Started discovery");
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i("Debug Receiver", "Finished discovery");
                context.unregisterReceiver(receiver);
            }
        }
    };


    public ConnectBluetooth(BluetoothAdapter btAdaptor, Context context) {
        this.bluetoothAdapter = btAdaptor;
        this.context = context.getApplicationContext();
//        connectThread = new ConnectThread();
    }

    public void sendData(String data) {
        connectThread.sendData(data);
    }


    public void connect() {
//        if (!connected_to_device) {
            Log.i("connecting", "gott to connect()");
            attemptConnectionToPairedRaspberryPi();
//        }
        //this.raspberry_pi_paired = attemptConnectionToPairedRaspberryPi();
//        if (!this.raspberry_pi_paired){
//            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//            IntentFilter filterStart = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//            IntentFilter filterStop = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//            this.context.registerReceiver(receiver, filter);
//            this.context.registerReceiver(receiver, filterStop);
//            this.context.registerReceiver(receiver, filterStart);
//            attemptToFindAndConnectToRaspberryPi();
//        }
    }

    public void startConnectionThread (BluetoothDevice device) {
        connected_to_device = true;
        connectThread = new ConnectThread(device, this.context);
        connectThread.start();
    }

    public void disconnect() {
//        this.context.unregisterReceiver(this.receiver);
        if (this.bluetoothAdapter.isDiscovering()) {
            this.bluetoothAdapter.cancelDiscovery();
        }
        if (connected_to_device) {
            connectThread.cancel();
        }
    }

    public void connectionFailed () {
//        this.context.unregisterReceiver(this.receiver);
        if (this.bluetoothAdapter.isDiscovering()) {
            this.bluetoothAdapter.cancelDiscovery();
        }
        connected_to_device = false;
    }

    public void timeout() {
        this.context.unregisterReceiver(this.receiver);
        if (this.bluetoothAdapter.isDiscovering()) {
            this.bluetoothAdapter.cancelDiscovery();
        }
        //stop connectThread as well --> neccesary?
    }

    private boolean attemptConnectionToPairedRaspberryPi () {
        Set<BluetoothDevice> pairedDevices = this.bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i("Paired Bluetooth device", "Device name: " + deviceName);
                Log.i("Paired Bluetooth device", "Device Mac Address: " + deviceHardwareAddress);
                //Later add code here to skip device connection if raspberry pi mac address is already paired
                //open server socket to wait for raspberry pi to connect
                if (deviceHardwareAddress.equals(RASPBERRY_PI_MAC_ADDRESS)) {
                    Log.i("Connecting","Trying to connect to pi");
                    if (this.bluetoothAdapter.isDiscovering()) {
                        this.bluetoothAdapter.cancelDiscovery();
                    }
                    startConnectionThread(device);
                    return true;
                }
            }
        }
        return false;
    }

    private void attemptToFindAndConnectToRaspberryPi () {
        // Register for broadcasts when a device is discovered.
        Toast.makeText(this.context, "Looking for Raspberry Pi", Toast.LENGTH_LONG).show();
        Log.i("Bluetooth Status", "Starting Discovery");
        boolean startedSucces = this.bluetoothAdapter.startDiscovery();
        Log.i("Debug", "Started successfully " + startedSucces);
    }

}
