package com.example.codetector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    Thread t;
    int count;
    byte[] buffer = new byte[256];
    int info;
    String pp = "PP";
    String level;
    String information;



    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    //create object for hc05
    //set object hc05 equal to mac address for real HC05 module
    BluetoothDevice hc05 = btAdapter.getRemoteDevice("98:D3:31:F9:74:CC");
    //set btSocket to null value
    BluetoothSocket btSocket = null;



    String readMessage;

    //unique number for identifying bluetooth module
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final TextView coLevel=(TextView)findViewById(R.id.textViewCO);
        final TextView levelOutput=(TextView) findViewById(R.id.levelOutput);
        final TextView infoOutput=(TextView) findViewById(R.id.TextViewInformation);


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
                                levelOutput.setText(String.valueOf("Exposure: "+ level));
                                infoOutput.setText(String.valueOf(information));



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

        //intReadMessage = intReadMessage + 450;

        if(intReadMessage < 20)
        {
            level = "Normal";
            information = "PP level is normal";
        }

        if(intReadMessage < 50 && intReadMessage > 20)
        {
            level = "Low";
            information = "PP level is safe for exposure over 6-8 hours";
        }

        if(intReadMessage > 50 && intReadMessage < 60){
            level = "Moderate";
            information = "Max workplace exposure";
        }

        if(intReadMessage > 60 && intReadMessage < 125)
        {
            level = "High";
            information = "Headache after 1-2 hours";
        }

        if(intReadMessage > 125 && intReadMessage < 225)
        {
            level = "Very High";
            information = "CO poisoning symptoms after 2-3 hours, reduce level as soon as possible";
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(100);

        }

        if(intReadMessage > 225 && intReadMessage < 425)
        {
            level = "Unsafe";
            information = "Life threatening after 3 hours of exposure, reduce level as soon as possible";
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);

        }

        if(intReadMessage > 425 && intReadMessage < 900)
        {
            level = "Very Unsafe";
            information = "Dizziness, nausea, and convulsions within 45 minutes, reduce level immediately";
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(300);

        }

        if(intReadMessage > 900 && intReadMessage < 1100)
        {
            level = "Extremely Unsafe";
            information = "Loss of consciousness at this level for longer than 1 hour";
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(400);

        }

        if(intReadMessage > 1100)
        {
            level = "Seek Medical attention";
            information = "Phone emergency services, seek fresh air";
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);

        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    public void onStart (View view){
        createBluetoothConnection();
        //ReceiveBtData();
        t.start();
    }

}
