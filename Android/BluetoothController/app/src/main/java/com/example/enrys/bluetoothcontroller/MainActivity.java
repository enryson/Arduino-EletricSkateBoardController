package com.example.enrys.bluetoothcontroller;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.media.TransportMediator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import android.widget.TextView;
import android.widget.ToggleButton;

import static android.R.attr.value;
import static android.os.SystemClock.sleep;
import static android.support.v7.widget.AppCompatDrawableManager.get;


public class MainActivity extends Settings {
    ImageButton settingsbutton,ImgLedOn,conectionBtLogo;
    ProgressBar progressBar2;
    SeekBar  mySeekBar;
    TextView Velocimetro,toptext,textkmh;
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
    private static final int MESSAGE_READ = 3;


    public static String sharedBluetoothMac;

    private boolean registered=false;


    StringBuilder bluetoothdata = new StringBuilder();

    private static String MAC = null;
    //private Handler mHandler = new Handler();
    private Handler mHandler;

    UUID My_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");


    public void LogoConectado() {
        ImageButton btn = (ImageButton)findViewById(R.id.conectionBtLogo);
        btn.setBackgroundResource(R.drawable.btconected);
    }
    public void LogoDesconectado() {
        ImageButton btn = (ImageButton)findViewById(R.id.conectionBtLogo);
        btn.setBackgroundResource(R.drawable.btdisconected);
    }

    public void LedOn() {
        ImageButton btn = (ImageButton)findViewById(R.id.ImgLedOn);
        btn.setBackgroundResource(R.drawable.ledon);
    }
    public void LedOff() {
        ImageButton btn = (ImageButton)findViewById(R.id.ImgLedOn);
        btn.setBackgroundResource(R.drawable.ledoff);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        setContentView(R.layout.activity_main);

        updateBoardName();
        //LogoDinamico();
        /*| View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);*/


        conectionBtLogo = (ImageButton)findViewById(R.id.conectionBtLogo);
        ImgLedOn = (ImageButton)findViewById(R.id.ImgLedOn) ;
        Velocimetro = (TextView)findViewById(R.id.Velocimetro);
        settingsbutton = (ImageButton)findViewById(R.id.settingsbutton);
        mySeekBar = (SeekBar) findViewById(R.id.mSeekBar);
        mySeekBar.setMax(TransportMediator.KEYCODE_MEDIA_RECORD);
        mySeekBar.setProgress(60);

        textkmh = (TextView) findViewById(R.id.textkmh);
        Velocimetro = (TextView) findViewById(R.id.Velocimetro);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/CODEBold.otf");
        Velocimetro.setTypeface(typeface);


        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);


        settingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Settings.class));
            }
        });


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null){
            Toast.makeText(getApplicationContext(), "dispositivo bluetooth nao encontrado",Toast.LENGTH_LONG).show();

        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BT_ACTIVATE_REQUEST);
        }



        //botao conexao bluetooth
        conectionBtLogo.setOnClickListener(new View.OnClickListener() {

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
                        LogoConectado();
                        //ButtonBTConect.setText("conectar");
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

        ImgLedOn.setOnClickListener(new View.OnClickListener() {
            Boolean flag = false;
            @Override
            public void onClick(View v) {


                Vibrator v2 = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
                v2.vibrate(80);
                if (conection){

                    if(flag) {
                        connectedThread.write("L");
                        LedOff();
                        flag = false;
                    } else {
                        connectedThread.write("O");
                        LedOn();
                        flag = true;
                    }
                }   else {
                    Toast.makeText(getApplicationContext(), "Device Desconectado : " , Toast.LENGTH_LONG).show();
                }
            }
        });

        //SEEKBAR ACELERADOR
        mySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress , boolean fromUser) {

                /*

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
                }*/


                Vibrator v = (Vibrator)getSystemService(MainActivity.VIBRATOR_SERVICE);
                int val = progress/10;
                v.vibrate(val);
                //textkmh.setText(""+val);

                if (conection) {
                    try {
                        connectedThread.write(new StringBuilder(String.valueOf((progress * 1) + 30)).append("n").toString());
                        MainActivity.oldvalue = value;
                    } catch (Exception e) {
                        connectedThread.write(new StringBuilder(String.valueOf((progress * 1) + 30)).append("n").toString());
                    }
                    if (MainActivity.oldvalue <= 90) {
                        MainActivity.oldvalue = 90;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(50);
                if (conection) {
                    connectedThread.write(new StringBuilder(String.valueOf((40))).append("n").toString());
                    //this.valueOnMove((progress * 1) + 30);
                    //connectedThread.write(new StringBuilder(String.valueOf((progress * 1) + 30)).append("n").toString());
                }
            }
        });

        mHandler =  new Handler() {
            @Override
            public void handleMessage(Message msg) {                                    //Read data from the handler to recilve voltage
                if (msg.what == MESSAGE_READ){
                    String recilvdata = (String) msg.obj;
                    bluetoothdata.append(recilvdata);
                    int endinformation = bluetoothdata.indexOf("v");
                    if (endinformation >0){
                        String completeData = bluetoothdata.substring(00,endinformation);
                        int informationLeght = completeData.length();
                        if(bluetoothdata.charAt(0)=='{'){
                            String finalData = bluetoothdata.substring(1,informationLeght);
                            double minVolt = 10;
                            double maxVolt = 12.2;
                            double volt = (Double.parseDouble(finalData)-minVolt);
                            int percentage = (int) ((volt*100)/(maxVolt-minVolt));
                            progressBar.setProgress(percentage);
                            textkmh.setText(percentage + "%");
                            textkmh.setText("Voltage : "+ finalData);
                        }
                    }
                    bluetoothdata.delete(0, bluetoothdata.length());

                }
            }
        };

        final Handler handler = new Handler();                                          //Send String to the handler m
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            connectedThread.write("m");
                        } catch (Exception e) {
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 400);



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
                        //ButtonBTConect.setText("Desconectar");
                        Toast.makeText(getApplicationContext(), "Conectado : "+ MAC, Toast.LENGTH_LONG).show();
                        LogoConectado();
                    }catch(IOException erro){
                        conection = false;
                        Toast.makeText(getApplicationContext(), "Erro Desconectado : "+ MAC, Toast.LENGTH_LONG).show();
                    }
                }   else    {
                    Toast.makeText(getApplicationContext(), "Falha MAC",Toast.LENGTH_LONG).show();
                }
        }
    } //execute in every 10 minutes

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
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

                    String btdata = new String(buffer, 0 , bytes);

                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, btdata).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
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
        private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    Intent intent1 = new Intent(MainActivity.this, MainActivity.class);

                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            if(registered) {
                                unregisterReceiver(mReceiver);
                                registered=false;
                            }
                            startActivity(intent1);
                            finish();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            if(registered) {
                                unregisterReceiver(mReceiver);
                                registered=false;
                            }
                            startActivity(intent1);
                            finish();
                            break;
                    }
                }
            }
        };
    }
}
