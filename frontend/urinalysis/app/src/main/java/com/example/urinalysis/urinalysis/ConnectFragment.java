package com.example.urinalysis.urinalysis;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.urinalysis.urinalysis.models.Category;
import com.example.urinalysis.urinalysis.models.Substance;
import com.example.urinalysis.urinalysis.models.Unit;
import com.example.urinalysis.urinalysis.models.User;
import com.example.urinalysis.urinalysis.models.UserCategoryUnit;
import com.inthecheesefactory.thecheeselibrary.fragment.support.v4.app.StatedFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by budiryan on 2/21/18.
 */

public class ConnectFragment extends StatedFragment{
    private static final String TAG = "ConnectFragment";

    // GUI Components
    private TextView mBluetoothStatus;
    private Button mOnBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mClearBtn;
    public static BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;
    public static Handler mHandler; // Our main handler that will receive callback notifications
    public static ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    public static BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path
    private TextView sensorData;
    private TextView report;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status


    // For calling the API through retrofit
    private Api api;
    private Retrofit retrofit;

    // Getting the data obtained from the embedded device
    private Integer currentUserId;
    private Float currentGlucoseValue;
    private Float currentUrineColorValue;
    private List<User> users;
    private List<Category> categories;
    private List<Unit> units;

    private AlertDialog dialog;
    private Button buttonCancel;
    private Button buttonSend;
    private EditText notesEditText;



    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connect_fragment, container, false);

        retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

        mBluetoothStatus = view.findViewById(R.id.bluetoothStatus);
        mOnBtn = view.findViewById(R.id.on);
        mOffBtn = view.findViewById(R.id.off);
        mListPairedDevicesBtn = view.findViewById(R.id.PairedBtn);
        mClearBtn = view.findViewById(R.id.clear);
        mBTArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mDevicesListView = view.findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        sensorData = view.findViewById(R.id.sensorData);


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.dialog_send_data, null);
        report = mView.findViewById(R.id.send_data_report);
        buttonCancel = mView.findViewById(R.id.btn_cancel);
        buttonSend = mView.findViewById(R.id.btn_send);
        notesEditText = mView.findViewById(R.id.note);

        mBuilder.setView(mView);
        dialog = mBuilder.create();

        // Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);


        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getActivity().getApplicationContext(),
                    "Bluetooth device not found!",Toast.LENGTH_SHORT).show();
        }

        else {
            mOnBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn(v);
                }
            });

            mOffBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    bluetoothOff(v);
                }
            });

            mClearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDevicesListView.setAdapter(null);
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listPairedDevices(v);
                }
            });
        }

        if (!mBTAdapter.isEnabled()){
            mBluetoothStatus.setText("Bluetooth is disabled");
        }
        else {
            mBluetoothStatus.setText("Bluetooth is enabled");
        }


        // Call to get the list of users, categories, units
        Call <UserCategoryUnit> callUserCategoryUnit = api.getUserCategoryUnit();
        callUserCategoryUnit.enqueue(new Callback<UserCategoryUnit>() {
            @Override
            public void onResponse(Call<UserCategoryUnit> call, Response<UserCategoryUnit> response) {
                UserCategoryUnit userCategoryUnit = response.body();
                users = userCategoryUnit.getUsers();
                categories = userCategoryUnit.getCategories();
                units = userCategoryUnit.getUnits();

                mHandler = new Handler(){
                    public void handleMessage(android.os.Message msg){
                        if(msg.what == MESSAGE_READ){
                            String readMessage = "..";
                            try {
                                readMessage = new String((byte[]) msg.obj, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            try {
                                String[] messageArray = readMessage.split("\\|");
                                if (messageArray.length == 4) {
                                    currentUserId = Integer.valueOf(messageArray[1]);
                                    currentGlucoseValue = Float.valueOf(messageArray[2]);
                                    currentUrineColorValue = Float.valueOf(messageArray[3]);
                                    String userVal = String.format("User: %s", String.valueOf(users.get(currentUserId).getName()));
                                    String glucoseVal = String.format("Glucose value: %s mg/dl", messageArray[2]);
                                    String colorVal = String.format("Urine Color value: %s", messageArray[3]);
                                    String reportString = "\n" + userVal + "\n" + glucoseVal + "\n" + colorVal;
                                    report.setText(reportString);
                                    dialog.show();
                                }
                                sensorData.setText(readMessage);
                            }
                            catch (Exception e){
                                Log.e(TAG, "sensor data not valid", e);
                            }
                        }

                        if(msg.what == CONNECTING_STATUS){
                            if(msg.arg1 == 1)
                                mBluetoothStatus.setText("Connected to device: " + (String)(msg.obj));
                            else
                                mBluetoothStatus.setText("Connection failed");
                        }
                    }
                };
            }

            @Override
            public void onFailure(Call<UserCategoryUnit> call, Throwable t) {

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                // Send 2 times for glucose and urine color
                String note = notesEditText.getText().toString();
                Log.d(TAG, "User: " + String.valueOf(users.get(currentUserId).getId()));
                Log.d(TAG, "Units: " + String.valueOf(units.get(0).getId()));
                Log.d(TAG, "Categories: " + String.valueOf(categories.get(0).getId()));
                Log.d(TAG, "Note: " + note);
                // Send glucose
                sendData(v, users.get(currentUserId).getId(), units.get(0).getId(), categories.get(0).getId(), currentGlucoseValue, note);
                // Send urine color
                sendData(v, users.get(currentUserId).getId(), units.get(1).getId(), categories.get(1).getId(), currentUrineColorValue, note);
                dialog.dismiss();
            }
        });


        listPairedDevices(view);
        return view;
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(intentAction)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };


    public class ConnectedThread extends Thread {
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
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        buffer = new byte[1024];
                        SystemClock.sleep(50); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Sending Data to Arduino..", Toast.LENGTH_SHORT).show();
                mmOutStream.write(bytes);
                Toast.makeText(getActivity().getApplicationContext(),
                        "Data sent.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private static BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };


    private void bluetoothOn(View view){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothStatus.setText("Bluetooth enabled");
            Toast.makeText(getActivity().getApplicationContext(),
                    "Bluetooth turned on",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getActivity().getApplicationContext(),
                    "Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    private void bluetoothOff(View view){
        mBTAdapter.disable(); // turn off
        mBluetoothStatus.setText("Bluetooth disabled");
        Toast.makeText(getActivity().getApplicationContext(),
                "Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    private void listPairedDevices(View view){
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mPairedDevices = mBTAdapter.getBondedDevices();
        mBTArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }
        else
            Toast.makeText(getActivity().getApplicationContext(),
                    "Bluetooth not on", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void sendData(View view, Integer userId, Integer unitId, Integer categoryId, Float value, String note){

                Call<Substance> save_substance = api.saveSubstance(value, unitId, categoryId, userId , note);
                save_substance.enqueue(new Callback<Substance>() {
                    @Override
                    public void onResponse(Call<Substance> call, Response<Substance> response) {
                        Log.d(TAG, "response is: " + response.body().toString());
                        Toast.makeText(getActivity().getApplicationContext(),
                                String.format("Data succesfully sent to server for user: %s", users.get(currentUserId).getName())
                                        , Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Substance> call, Throwable t) {
                        Log.d(TAG, "ERROR SENDING");
                    }
                });
    }

}
