package com.app.bluetooth.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;

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
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private boolean stopThread;

    ArduinoBluetoothPresenter(ArduinoBluetoothContract.View view) {

        this.view = view;
    }

    private boolean initBT()
    {
        boolean found=false;
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            view.setToastMsg("Device doesnt Support Bluetooth");
        }
        if(bluetoothAdapter!=null && !bluetoothAdapter.isEnabled())
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            view.startActivityBluetooth(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        }

        return found;
    }

    private boolean connectWithBT()
    {
        boolean connected=true;
        try {
            UUID PORT_UUID = UUID.fromString(view.getContext().getResources().getString(R.string.SERIAL_PORT_SERVICE_ID));//Serial Port Service ID
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected=false;
        }
        if(connected)
        {
            try {
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return connected;
    }

    public void onClickStart() {
        if(initBT())
        {
            if(connectWithBT())
            {
                view.setUiEnabled(true);
                beginListenForData();
                view.appendToEditText("\nConnection Opened!\n");
            }
            else{
                view.setToastMsg("Unable to connect to BT device");
            }

        }
    }

    private void beginListenForData()
    {
        final Handler handler = new Handler();
        stopThread = false;
        Thread thread  = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopThread)
                {
                    try
                    {
                        int byteCount = inputStream.available();
                        if(byteCount > 0)
                        {
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);
                            final String string=new String(rawBytes,"UTF-8");
                            handler.post(new Runnable() {
                                public void run()
                                {
                                    view.appendToEditText("\nSent Data:"+string+"\n");
                                }
                            });

                        }
                    }
                    catch (IOException ex)
                    {
                        stopThread = true;
                    }
                }
            }
        });

        thread.start();
    }

    public void onClickSend(String msgToSend) {
        String msg = msgToSend.concat("\n");
        try {
            outputStream.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        view.appendToEditText("\nSent Data:"+msg+"\n");

    }

    public void onClickStop() throws IOException {
        stopThread = true;
        outputStream.close();
        inputStream.close();
        socket.close();
        view.setUiEnabled(false);
        view.appendToEditText("\nConnection Closed!\n");
    }

    public void onClickClear() {
        view.clearEditText();
    }
}