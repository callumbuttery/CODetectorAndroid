package com.example.codetector;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.icu.util.Output;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    Button btn;

    //unique number for identifying all bluetooth serial boards
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=(TextView) findViewById(R.id.textViewCO);


    }



    public void ReceiveBtData(View view){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        //used to find MAC addresses
        System.out.println(btAdapter.getBondedDevices());

        //create object for hc05
        //set object hc05 equal to mac address for real HC05 module
        BluetoothDevice hc05 = btAdapter.getRemoteDevice("98:D3:31:F9:74:CC");
        //verify above mac address is actually for HC05 by printing the name received from the MAC address
        System.out.println(hc05.getName());

        //set btSocket to null value
        BluetoothSocket btSocket = null;
        //connectionAttempts used for number of times tried to connect to bluetooth device loop
        int connectionAttempts = 0;

        byte[] buffer = new byte[256];
        int info;

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


        //Receiving input from bluetooth
        try {
            InputStream inputStream = btSocket.getInputStream();

            //clear input buffer incase of previous stored data
            inputStream.skip(inputStream.available());


            while (btSocket.isConnected()) {

                //reading the bluetooth input
                info = inputStream.read(buffer);
                //assigning read value from the buffer to a string
                String readMessage = new String(buffer, 0, info);



                //New textview object
                //TextView tv = (TextView) findViewById(R.id.textViewCO);
                //Display variable
                //tv.setText(readMessage);
                //Console log
                System.out.println(readMessage);


                try {
                    Thread.sleep(3200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        //Close socket once done with code
        try {
            btSocket.close();
            //Print connection
            System.out.println(btSocket.isConnected());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void onStart(View view){
            ReceiveBtData( view);

    }
}
