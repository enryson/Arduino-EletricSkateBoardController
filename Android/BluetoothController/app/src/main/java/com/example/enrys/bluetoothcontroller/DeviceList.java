package com.example.enrys.bluetoothcontroller;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.Set;

import static com.example.enrys.bluetoothcontroller.Settings.et;
import static com.example.enrys.bluetoothcontroller.Settings.sharedValue;

/**
 * Created by enrys on 1/9/2017.
 */

public class DeviceList extends ListActivity {

    private BluetoothAdapter mBluetoothAdapter2 = null;
    static String MAC_ADRESS = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> ArrayBluetooth = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        mBluetoothAdapter2 = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> PairedDevices = mBluetoothAdapter2.getBondedDevices();

        if (PairedDevices.size()>0){
            for(BluetoothDevice btdevice:PairedDevices){
                String NameBTDev = btdevice.getName();
                String BTMAC = btdevice.getAddress();
                ArrayBluetooth.add(NameBTDev + "\n" + BTMAC);
            }
        }
        setListAdapter(ArrayBluetooth);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String generalinformation = ((TextView) v).getText().toString();
        String macAdress = generalinformation.substring(generalinformation.length() - 17);
        //Toast.makeText(getApplicationContext(),"info:" + generalinformation, Toast.LENGTH_LONG ).show();

        Intent macReturn = new Intent();
        macReturn.putExtra(MAC_ADRESS, macAdress );
        setResult(RESULT_OK, macReturn);
        finish();

    }
}
