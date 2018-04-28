package com.app.bluetooth.arduinobluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by kawtar on 27/04/2018.
 */

class ArduinoBluetoothContract {
    public interface View {

        Context getContext();

        void clearEditText();

        void setUiEnabled(boolean enableUI);

        void appendToEditText(String text);

        void startActivityBluetooth(Intent enableAdapter, int requestCode);

        void setToastMsg(String msg);
    }
    public interface UserActionsListener {

        void onClickStart();

        void onClickSend(String msgToSend);

        void onClickStop() throws IOException;

        void onClickClear();

        UUID getSSPUUID();

        BluetoothAdapter getBluetoothAdapter();

        void manageMyConnectedSocket(BluetoothSocket mmSocket);

        void setBTConnection(String msg);

        Handler getBTHandler();

        boolean startBTDeviceDiscovery();
    }

}
