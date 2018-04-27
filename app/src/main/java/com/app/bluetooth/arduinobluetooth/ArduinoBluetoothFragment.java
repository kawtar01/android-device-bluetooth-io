package com.app.bluetooth.arduinobluetooth;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by kawtar on 27/04/2018.
 */

public class ArduinoBluetoothFragment extends Fragment implements ArduinoBluetoothContract.View {

    private static final String TAG = ArduinoBluetoothFragment.class.getName();
    private ArduinoBluetoothPresenter mActionsListener;
    private TextView textView;
    private EditText editText;
    private Button stopButton;
    private Button clearButton;
    private Button sendButton;
    private Button startButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mRoot = inflater.inflate(R.layout.arduino_bluetooth_fragment, container, false);
        startButton = (Button) mRoot.findViewById(R.id.buttonStart);
        sendButton = (Button) mRoot.findViewById(R.id.buttonSend);
        clearButton = (Button) mRoot.findViewById(R.id.buttonClear);
        stopButton = (Button) mRoot.findViewById(R.id.buttonStop);
        editText = (EditText) mRoot.findViewById(R.id.editText);
        textView = (TextView) mRoot.findViewById(R.id.textView);
        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActionsListener = new ArduinoBluetoothPresenter(this);
        handleClickOnButton();
    }
    private void handleClickOnButton() {
        final Context context = this.getContext();
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionsListener.onClickStart();
            }

        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().length()>0){
                    mActionsListener.onClickSend(editText.getText().toString());
                }

            }

        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionsListener.onClickClear();
            }

        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    mActionsListener.onClickStop();
                }
                catch (IOException e){
                    Toast.makeText(context,"Error occured while trying to close the bluetooth connection",Toast.LENGTH_SHORT).show();
                }

            }

        });
    }
    @Override
    public void clearEditText() {
        textView.setText("");
    }

    @Override
    public void setUiEnabled(boolean enableUI) {
        startButton.setEnabled(!enableUI);
        sendButton.setEnabled(enableUI);
        stopButton.setEnabled(enableUI);
        textView.setEnabled(enableUI);
    }

    @Override
    public void appendToEditText(String text) {
        textView.append(text);
    }

    @Override
    public void startActivityBluetooth(Intent enableAdapter, int requestCode) {
        startActivityForResult(enableAdapter, requestCode);
    }

    @Override
    public void setToastMsg(String msg) {
        Toast.makeText(this.getContext(),msg,Toast.LENGTH_SHORT).show();
    }
}
