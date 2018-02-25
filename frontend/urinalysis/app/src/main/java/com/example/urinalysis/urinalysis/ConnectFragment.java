package com.example.urinalysis.urinalysis;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by budiryan on 2/21/18.
 */

public class ConnectFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ConnectFragment";
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    Button b1, b2, b3, b4;
    ListView lv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connect_fragment, container, false);

        BA = BluetoothAdapter.getDefaultAdapter();
        lv = view.findViewById(R.id.listView);

        b1 = view.findViewById(R.id.button);
        b1.setOnClickListener(this);

        b2 = view.findViewById(R.id.button2);
        b2.setOnClickListener(this);

        b3 = view.findViewById(R.id.button3);
        b3.setOnClickListener(this);

        b4 = view.findViewById(R.id.button4);
        b4.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                on(view);
                break;
            case R.id.button2:
                visible(view);
                break;
            case R.id.button3:
                list(view);
                break;
            case R.id.button4:
                off(view);
                break;
        }
    }

    public void on(View v){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getActivity().getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void off(View v){
        BA.disable();
        Toast.makeText(getActivity().getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }


    public  void visible(View v){
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }


    public void list(View v){
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices) list.add(bt.getName());
        Toast.makeText(getActivity().getApplicationContext(), "Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new  ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);

        lv.setAdapter(adapter);
    }
}
