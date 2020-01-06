package com.jmctvs.dieukhienxev2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class TabFragment2 extends Fragment {
    private View frag_view;

    private static TextView btStatusText;
    private Button btOnBtn;
    private Button btOffBtn;
    private Button btListBtn;
    private ListView btList;

    private BluetoothAdapter mBTAdapter;
    private ArrayAdapter<String> mBTArrayAdapter;
    private Set<BluetoothDevice> mPairedDevices;

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private boolean btListIsShow = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("test", "Tab 2");
        frag_view = inflater.inflate(R.layout.tab_fragment_2, container, false);
        return frag_view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btOnBtn =  frag_view.findViewById(R.id.btOnBtn);
        btOffBtn =  frag_view.findViewById(R.id.btOffBtn);
        btListBtn =  frag_view.findViewById(R.id.btListBtn);
        btStatusText =  frag_view.findViewById(R.id.btStatusText);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mBTArrayAdapter = new ArrayAdapter<String>(frag_view.getContext(), android.R.layout.simple_list_item_1);

        btList = frag_view.findViewById(R.id.btList);
        btList.setAdapter(mBTArrayAdapter);
        btList.setOnItemClickListener(mDeviceClickListener);

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            btStatusText.setText("Không tìm thấy thiết bị");
            Toast.makeText(getActivity(),"Không tìm thấy thiết bị",Toast.LENGTH_SHORT).show();
        }
        else{
            btOnBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BluetoothOn(v);
                }
            });

            btOffBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BluetoothOff(v);
                }
            });

            btListBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPairedDevices(v);
                }
            });
        }

    }

    public static void setBTStatusText(String mess){
        btStatusText.setText(mess);
    }

    private void BluetoothOn(View view){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, MainActivity.REQUEST_ENABLE_BT);
            btStatusText.setText("Bật");
            //btStatusText.setTextColor(Color.parseColor("#4cd137"));
            Toast.makeText(getActivity(),"Bật bluetooth",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getActivity(),"Bluetooth đã được bật", Toast.LENGTH_SHORT).show();
        }
    }

    private void BluetoothOff(View view){
        mBTArrayAdapter.clear();
        btList.setAdapter(mBTArrayAdapter);
        mBTAdapter.disable();
        btStatusText.setText("Tắt");
        //btStatusText.setTextColor(Color.parseColor("#eb2f06"));
        Toast.makeText(getActivity(),"Tắt bluetooth", Toast.LENGTH_SHORT).show();
        btListIsShow = false;
    }

    private void showPairedDevices(View view){
        if(btListIsShow == true) return;
        if(mBTAdapter.isEnabled()){
            mPairedDevices = mBTAdapter.getBondedDevices();
            btListIsShow = true;
            for(BluetoothDevice device: mPairedDevices){
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
            Toast.makeText(getActivity(), "Hiện các thiết bị ghép nối", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getActivity(), "Bluetooth chưa được bật", Toast.LENGTH_SHORT).show();
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getActivity().getBaseContext(), "Bluetooth chưa được bật", Toast.LENGTH_SHORT).show();
                return;
            }
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);
            //
            btStatusText.setText("Đang kết nối với " + name);
            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        MainActivity.mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getActivity().getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        MainActivity.mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            MainActivity.mBTSocket.close();
                            TabFragment3.mHandler.obtainMessage(MainActivity.CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getActivity().getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(fail == false) {
                        MainActivity.mConnectedThread = new ConnectedThread(MainActivity.mBTSocket);
                        MainActivity.mConnectedThread.start();

                        TabFragment3.mHandler.obtainMessage(MainActivity.CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }
}