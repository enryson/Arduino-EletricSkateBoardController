package com.example.enrys.bluetoothcontroller;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.widget.TextView;
import org.w3c.dom.Text;
import static android.R.attr.value;
import static android.R.id.progress;


public class MainActivity extends AppCompatActivity {

    SeekBar  mySeekBar;
    Button ButtonBTConect, ButtonLed1,ButtonLed2;
    TextView textView;
    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothDevice mBluetoothDevice = null;
    BluetoothSocket mBluetoothSocket = null;
    ConnectedThread connectedThread;
    InputStream mmInStream = null;
    OutputStream mmOutStream = null;

    boolean conection = false;
    public static int oldvalue;
    private static final String TAG = "-->";
    private static final int BT_ACTIVATE_REQUEST = 1;
    private static final int BT_CONNECT_REQUEST = 2;
    private static String MAC = null;
    private Handler mHandler = new Handler();
    private static final String MESSAGE_READ = null;


    UUID My_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView = (TextView)findViewById(R.id.textView);
        ButtonBTConect = (Button)findViewById(R.id.ButtonBTConect);
        ButtonLed1 = (Button)findViewById(R.id.ButtonLed1);
        ButtonLed2 = (Button)findViewById(R.id.ButtonLed2);
        mySeekBar = (SeekBar) findViewById(R.id.mSeekBar);
        mySeekBar.setMax(TransportMediator.KEYCODE_MEDIA_RECORD);
        mySeekBar.setProgress(60);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null){
            Toast.makeText(getApplicationContext(), "dispositivo bluetooth nao encontrado",Toast.LENGTH_LONG).show();

        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BT_ACTIVATE_REQUEST);
        }

        //botao conexao bluetooth
        ButtonBTConect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator v2 = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
                v2.vibrate(100);
                if (conection){
                    //Disconect
                    try {

                        mBluetoothSocket.close();
                        conection = true;
                        Toast.makeText(getApplicationContext(), "Device Desconectado : " , Toast.LENGTH_LONG).show();

                        //mudando nome do botao conecao
                        ButtonBTConect.setText("conectar");

                    }catch(IOException erro){
                        Toast.makeText(getApplicationContext(), "Erro Desconectado : "+ erro, Toast.LENGTH_LONG).show();

                    }

                }   else    {
                   //Conect
                    Intent open_list = new Intent(MainActivity.this, DeviceList.class);
                    startActivityForResult(open_list, BT_CONNECT_REQUEST);
                }

            }
        });

        ButtonLed1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator v2 = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
                v2.vibrate(100);
                if (conection){
                    connectedThread.write("LED1");
                }   else {
                    Toast.makeText(getApplicationContext(), "Device Desconectado : " , Toast.LENGTH_LONG).show();
                }
            }
        });

        ButtonLed2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator v2 = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
                v2.vibrate(100);
                if (conection){

                    connectedThread.write("LED2");
                }   else {
                    Toast.makeText(getApplicationContext(), "Device Desconectado : " , Toast.LENGTH_LONG).show();
                }
            }
        });

        //SEEKBAR ACELERADOR
        mySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override


            public void onProgressChanged(SeekBar seekBar, int progress , boolean fromUser) {
/*
                Vibrator v = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
                v.vibrate((progress/10)-3);
                if (progress < 40) {
                    seekBar.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                }
                if (progress > 40) {
                    seekBar.getProgressDrawable().setColorFilter(Color.rgb(0,153,0), PorterDuff.Mode.SRC_IN);
                }
                if (progress > 100) {
                    seekBar.getProgressDrawable().setColorFilter(Color.rgb(255,153,0), PorterDuff.Mode.SRC_IN);
                }
                if (progress > 120) {
                    seekBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
                }
*/

                try {
                    connectedThread.write(new StringBuilder(String.valueOf((progress * 1) + 30)).append("n").toString());
                    MainActivity.oldvalue = value;
                }catch (Exception e) {
                    connectedThread.write(new StringBuilder(String.valueOf((progress * 1) + 30)).append("n").toString());
                }
                if (MainActivity.oldvalue <= 90) {
                    MainActivity.oldvalue = 90;
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(50);
                connectedThread.write(new StringBuilder(String.valueOf((40))).append("n").toString());
                //connectedThread.write(new StringBuilder(String.valueOf((progress * 1) + 30)).append("n").toString());
            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case BT_ACTIVATE_REQUEST:
                if(resultCode == Activity.RESULT_OK){
                    Toast.makeText(getApplicationContext(), "bluetooth Ativado",Toast.LENGTH_LONG).show();
                }   else {
                    Toast.makeText(getApplicationContext(), "bluetooth nao ativado",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case BT_CONNECT_REQUEST:
                if (resultCode == Activity.RESULT_OK){
                    MAC = data.getExtras().getString(DeviceList.MAC_ADRESS);


                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(MAC);
                    try {
                        mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(My_UUID);

                        mBluetoothSocket.connect();

                        conection = true;

                        connectedThread = new ConnectedThread(mBluetoothSocket);
                        connectedThread.start();

                        //mudando nome do botao conecao
                        ButtonBTConect.setText("Desconectar");


                        Toast.makeText(getApplicationContext(), "Conectado : "+ MAC, Toast.LENGTH_LONG).show();

                    }catch(IOException erro){
                        conection = false;
                        Toast.makeText(getApplicationContext(), "Erro Desconectado : "+ MAC, Toast.LENGTH_LONG).show();
                    }

                }   else    {
                    Toast.makeText(getApplicationContext(), "Falha MAC",Toast.LENGTH_LONG).show();
                }
        }
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;


        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()


            // Keep listening to the InputStream until an exception occurs
            /*while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(RESULT_OK, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }*/
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String outputwrite ) {
            byte[] msgBuffer = outputwrite.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }*/
    }
}
