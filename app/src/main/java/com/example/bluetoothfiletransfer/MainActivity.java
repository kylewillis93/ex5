package com.example.bluetoothfiletransfer;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    int REQUEST_ENABLE_BT = 1;
    public boolean bluetooth_on = false;
    private BluetoothAdapter bluetoothAdapter;
    private ConnectBluetooth connectBluetooth;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
                Log.i("Bluetooth Status", "Bluetooth now enabled");
                this.bluetooth_on = true;
            } else {
                Log.i("Bluetooth Status", "Bluetooth rejected");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectBluetooth =  new ConnectBluetooth(bluetoothAdapter, this);
        if (bluetoothAdapter != null) {
            //Device supports bluetooth
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                Log.i("Bluetooth Status", "Bluetooth already enabled");
                this.bluetooth_on = true;
            }
        }
        else {
            // show toaster stating device does not support bluetooth
        }

//        if (this.bluetooth_on) {
//        ConnectBluetooth connectBluetooth = new ConnectBluetooth(bluetoothAdapter, this);
//        connectBluetooth.connect();
//        //Start a timer and timeout after no success has been broadcasted in t seconds
//            // connectBluetooth.timeout()
//        //Then show toaster, informing of timeout
//        }
    }

    public void connectOnClick(View view) {
        Log.i("connect on clikc", "thisss called");
        if (bluetooth_on) {
            connectBluetooth.connect();
        }
    }

    public void upOnClick(View view) {
        connectBluetooth.sendData("Up");
    }

    public void downOnClick(View view) {
        connectBluetooth.sendData("Down");
    }

    public void leftOnClick(View view) {
        connectBluetooth.sendData("Left");
    }

    public void rightOnClick(View view) {
        connectBluetooth.sendData("Right");
    }
}
