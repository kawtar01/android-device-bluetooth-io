package com.app.bluetooth.arduinobluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kawtar on 28/04/2018.
 */

public class IOBTThread extends Thread {

    private final Handler mHandler;
    private final String TAG = IOBTThread.class.getName();
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final ArduinoBluetoothPresenter arduinoBluetoothPresenter;
    private final BluetoothSocket mmSocket;
    private byte[] mmBuffer; // mmBuffer store for the stream

    void cancel() {
        try {
            mmSocket.close();
            mmInStream.close();
            mmOutStream.close();
        } catch (IOException e) {
            arduinoBluetoothPresenter.setBTConnection("Unable to close connection to BT");
        }
    }

    private interface MessageConstants {
        int MESSAGE_READ = 0;
        int MESSAGE_WRITE = 1;
    }

    IOBTThread(BluetoothSocket socket, ArduinoBluetoothPresenter bluetoothPresenter) {

        mHandler = bluetoothPresenter.getBTHandler();
        arduinoBluetoothPresenter = bluetoothPresenter;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        try {
            tmpIn = socket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }
        mmSocket = socket;
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        mmBuffer = new byte[1024];
        int numBytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                // Read from the InputStream.
                numBytes = mmInStream.read(mmBuffer);
                // Send the obtained bytes to the UI activity.
                Message readMsg = mHandler.obtainMessage(
                        MessageConstants.MESSAGE_READ, numBytes, -1,
                        mmBuffer);
                readMsg.sendToTarget();
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                arduinoBluetoothPresenter.setBTConnection("Input stream was disconnected");
                break;
            }
        }
    }

    // Call this from the main activity to send data to the remote device.
    void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
            // Share the sent message with the UI activity.
            Message writtenMsg = mHandler.obtainMessage(
                    MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
            writtenMsg.sendToTarget();
        } catch (IOException e) {
            arduinoBluetoothPresenter.setBTConnection("Couldn't send data to the other device");

        }
    }

}
