package com.app.bluetooth.arduinobluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kawtar on 28/04/2018.
 */

public class ConnectThread extends Thread {
    private static final String TAG = ConnectThread.class.getName();
    private final BluetoothSocket mmSocket;
    private final ArduinoBluetoothPresenter arduinoBluetoothPresenter;

    ConnectThread(BluetoothDevice device, ArduinoBluetoothPresenter bluetoothPresenter) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        arduinoBluetoothPresenter = bluetoothPresenter;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(bluetoothPresenter.getSSPUUID());
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
            arduinoBluetoothPresenter.setBTConnection("Socket's create() method failed");
        }

        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        arduinoBluetoothPresenter.getBluetoothAdapter().cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                arduinoBluetoothPresenter.setBTConnection("Unable to connect to BT");
                mmSocket.close();
            } catch (IOException closeException) {
                arduinoBluetoothPresenter.setBTConnection("Unable to close connection to BT");
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        arduinoBluetoothPresenter.manageMyConnectedSocket(mmSocket);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            arduinoBluetoothPresenter.setBTConnection("Unable to close connection to BT");
        }
    }
}