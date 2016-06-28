package com.pein.pov.virualleddisplay;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class DeviceList extends AppCompatActivity {

    //define objects in activity
    ListView deviceList;

    //to make device list to display on list view
    List<String> mArrayAdaptor = new ArrayList<>();
    //to store device
    HashMap<String,BluetoothDevice> bDeviceList = new HashMap<>();

    //to save paire devices
    Set<BluetoothDevice> pairedDevices;

    //get default bluetooth adaptor
    BluetoothAdapter mBluetoothAdapter;

    //handle bluetooth connection
    BluetoothHandle handle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        //define device list
        deviceList = (ListView) findViewById(R.id.deviceList);
        deviceList.setClickable(true);

        //get paired dvices
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = mBluetoothAdapter.getBondedDevices();

        //cal functions
        makeList();
        populateList();
        onClickOnListViewItems();
    }

    //make device list
    private void makeList(){
        if(pairedDevices !=null){
            for(BluetoothDevice device: pairedDevices){
                mArrayAdaptor.add(device.getName() + "  " + device.getAddress() );
                bDeviceList.put(device.getName() + "  " + device.getAddress(),device);
            }
        }
    }


    //set devices to display in listview
    private void populateList(){
        String arr[]= new String[mArrayAdaptor.size()];
        mArrayAdaptor.toArray(arr);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,R.layout.support_simple_spinner_dropdown_item,arr
        );
        deviceList.setAdapter(arrayAdapter);
    }

    //connect to device when click of device list item
    private void onClickOnListViewItems(){
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList =(deviceList.getItemAtPosition(position).toString());
                handle = new BluetoothHandle(bDeviceList.get(selectedFromList));
                handle.start();

                Toast msg = Toast.makeText(getBaseContext(),selectedFromList,Toast.LENGTH_LONG);
                msg.show();

                //go back to main activity while connecting
                Intent intent = new Intent(DeviceList.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
