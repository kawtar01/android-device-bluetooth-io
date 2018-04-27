package com.app.bluetooth.arduinobluetooth;

import android.content.Context;
import android.content.Intent;

import java.io.IOException;

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
    }

}
