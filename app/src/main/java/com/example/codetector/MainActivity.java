package com.example.codetector;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.icu.util.Output;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


import android.os.Vibrator;

public class MainActivity extends AppCompatActivity {


    Thread t;
    int count;
    byte[] buffer = new byte[256];
    int info;
    String pp = "PP";
    String level;



    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    //create object for hc05
    //set object hc05 equal to mac address for real HC05 module
    BluetoothDevice hc05 = btAdapter.getRemoteDevice("98:D3:31:F9:74:CC");
    //set btSocket to null value
    BluetoothSocket btSocket = null;



    String readMessage = "A";

    //unique number for identifying all bluetooth serial boards
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final TextView coLevel=(TextView)findViewById(R.id.textViewCO);
        final TextView levelOutput=(TextView) findViewById(R.id.levelOutput);


        t=new Thread(){

            @Override
            public void run(){


                while(!isInterrupted()){

                    try {
                        Thread.sleep(1000);  //1000ms = 1 sec


                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                count++;

                                ReceiveBtData();
                                coLevel.setText(String.valueOf(readMessage + pp));
                                levelOutput.setText(String.valueOf("Exposure "+ level));



                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        //t.start();
    }

    public void createBluetoothConnection(){

        //connectionAttempts used for number of times tried to connect to bluetooth device loop
        int connectionAttempts = 0;



        //loop while btSocket isn't connected and number of connection attempts has been less than 5
        do {
            try {
                btSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
                System.out.println(btSocket);

                //connect to HC05 server
                //HC05 = server
                //Phone = client
                btSocket.connect();
                //Print connection
                System.out.println(btSocket.isConnected());
            } catch (IOException e) {
                e.printStackTrace();
            }
            connectionAttempts = connectionAttempts + 1;
        } while (!btSocket.isConnected() && connectionAttempts < 5);

    }


    public void ReceiveBtData(){


        //Receiving input from bluetooth
        InputStream inputStream = null;
        try {
            inputStream = btSocket.getInputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //clear input buffer incase of previous stored data



        //while (btSocket.isConnected()) {

        //reading the bluetooth input
        try {
            info = inputStream.read(buffer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //assigning read value from the buffer to a string


        readMessage = new String(buffer, 0, info);
        System.out.println(readMessage);


        int intReadMessage = 0;

        //read readMessage as an int
        try{
            intReadMessage = Integer.parseInt(readMessage);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if(intReadMessage < 40)
        {
            level = "low";
        }

        if(intReadMessage > 40 && intReadMessage < 70){
            level = "Moderate";
        }

        if(intReadMessage > 70 && intReadMessage < 100)
        {
            level = "High";
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(5000);
            System.out.println("Vibrating warning 5 seconds");
        }

        if(intReadMessage > 100)
        {
            level = "Extreme";
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(10000);
            System.out.println("Vibrating warning 10 seconds");
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }




        //Close socket once done with code
        //try {
            //btSocket.close();
            //Print connection
           // System.out.println(btSocket.isConnected());
        //} catch (IOException e) {
            //e.printStackTrace();
        //}
    }


    public void onStart (View view){
        createBluetoothConnection();
        //ReceiveBtData();
        t.start();
    }

}
