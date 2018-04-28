package com.app.bluetooth.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by kawtar on 27/04/2018.
 */

public class ArduinoBluetoothPresenter implements ArduinoBluetoothContract.UserActionsListener {

    private static final String TAG = ArduinoBluetoothPresenter.class.getName();
    private ArduinoBluetoothContract.View view;
    private BluetoothDevice device;
    private BluetoothAdapter bluetoothAdapter;
    private ConnectThread connectThread;
    private IOBTThread threadIOConnection;

    ArduinoBluetoothPresenter(ArduinoBluetoothContract.View view) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.view = view;
    }

    private boolean initBT()
    {
        boolean found=false;
        if (bluetoothAdapter == null) {
            view.setToastMsg("Device doesnt Support Bluetooth");
        }
        else{
            if(!bluetoothAdapter.isEnabled()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                view.startActivityBluetooth(enableBtIntent, 0);
            }
            else
            {
                found = startBTDeviceDiscovery();
            }
        }

        return found;
    }

    public void onClickStart() {
        if(initBT())
        {
            connectThread = new ConnectThread(device,this);
            connectThread.run();
        }
    }


    public void onClickSend(String msgToSend) {
        threadIOConnection.write(msgToSend.getBytes());
    }

    public void onClickStop() throws IOException {
        threadIOConnection.cancel();
        view.setUiEnabled(false);
        view.appendToEditText("\nConnection Closed!\n");
    }

    public void onClickClear() {
        view.clearEditText();
    }

    @Override
    public UUID getSSPUUID() {
        return UUID.fromString(view.getContext().getResources().getString(R.string.SERIAL_PORT_SERVICE_ID));
    }

    @Override
    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    @Override
    public void manageMyConnectedSocket(BluetoothSocket mmSocket) {

        view.setUiEnabled(true);
        view.appendToEditText("\nConnection Opened!\n");
        threadIOConnection = new IOBTThread(mmSocket, this);
        threadIOConnection.run();
    }

    @Override
    public void setBTConnection(String msg) {
        view.setToastMsg("Unable to connect to BT device");
    }

    @Override
    public Handler getBTHandler() {
        return new Handler();
    }

    @Override
    public boolean startBTDeviceDiscovery() {
        boolean found = false;
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if(bondedDevices.isEmpty())
        {
            view.setToastMsg("Please Pair the Device first");
        }
        else
        {
            for (BluetoothDevice iterator : bondedDevices)
            {
                String DEVICE_ADDRESS = view.getContext().getResources().getString(R.string.DEVICE_ADDRESS);
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device=iterator;
                    found=true;
                    break;
                }
            }
        }
        return found;
    }
}