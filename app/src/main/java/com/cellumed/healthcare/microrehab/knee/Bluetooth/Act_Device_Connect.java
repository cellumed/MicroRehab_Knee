package com.cellumed.healthcare.microrehab.knee.Bluetooth;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cellumed.healthcare.microrehab.knee.R;
import com.cellumed.healthcare.microrehab.knee.Home.Act_Home;
import com.cellumed.healthcare.microrehab.knee.Home.BackPressCloseHandler;
import com.cellumed.healthcare.microrehab.knee.Util.BudUtil;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


@TargetApi(21)
public class Act_Device_Connect extends BTConnectActivity implements IMP_CMD {


    private Context mContext;

    private int mInitStatus = 0;  // init. for send status req. 0: none. 1: sent status req. 2: recv status rsp.
    private int mInitReties = 0;
    private TimerTask mTask;
    private Timer mRetryTimer;

    private BluetoothLeScanner mBleScanner;

    private LeDeviceListAdapter mDeviceListAdapter;
    public BluetoothDevice connectedDevice;
    private BluetoothDevice temp;
    private BluetoothGatt mGatt;

    //    private DeviceListAdapter mDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;

    private int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
 //   private static final long SCAN_PERIOD = 10000;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 2;
    private static final int PERMISSION_REQUEST_GET_ACCOUNT = 3;

    private ProgressDialog dialog;
    private BackPressCloseHandler backPressCloseHandler;

    Handler SearchTimeoutHandler = new Handler();
	Handler ConnectTimeoutHandler = new Handler();

    ImageButton startbutton;
    ImageButton connectbutton;
    ListView deviceList;
    View updated;





    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                final String deviceName = device.getName();
                if (deviceName != null && deviceName.length() > 0) {
                    // filtering by bluetooth name.
                    if (device.getName().contains("CRD-K100")) {
                        Log.e("BLE","broadcast on recv");
                        dialog.dismiss();
                        SearchTimeoutHandler.removeCallbacksAndMessages(null);
                        mDeviceListAdapter.addDevice(device);
                        mDeviceListAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;




        setContentView(R.layout.act__device__connect);
        setTitle("");

        // 1. set actionbar ----------------------------------------
        /*assert getSupportActionBar() != null;
        getSupportActionBar().setBackgroundDrawable(getResources().getResourceName(R.drawable.top_bar));
        */
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        View mCustomView = LayoutInflater.from(this).inflate(R.layout.layout_actionbar, null);
        actionBar.setCustomView(mCustomView);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        ((ImageButton) findViewById(R.id.custom_back_btn)).setBackground(null);
        ((ImageButton) findViewById(R.id.custom_back_btn)).setEnabled(false);
        Toolbar parent = (Toolbar) mCustomView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(mCustomView, params);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) mBluetoothAdapter.enable();

        mBleScanner = mBluetoothAdapter.getBluetoothLeScanner();

        //startService();

        backPressCloseHandler = new BackPressCloseHandler(this);

        startbutton = (ImageButton) findViewById(R.id.search_device);
        connectbutton = (ImageButton) findViewById(R.id.connect_device);
        deviceList = (ListView) findViewById(R.id.device_list);
        deviceList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        deviceList.setOnItemClickListener(tempDevice);
        startbutton.setOnClickListener(startAction);
        connectbutton.setOnClickListener(connectDevice);

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.GET_ACCOUNTS};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        mHandler = new Handler();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    Button.OnClickListener startAction = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mScanning == false) {

                dialog = new ProgressDialog(mContext, R.style.AppCompatAlertDialogStyle);
                dialog.setTitle(mContext.getResources().getString(R.string.ScanTitle));
                dialog.setMessage(mContext.getResources().getString(R.string.Scanning));
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.show();

                mScanning = true;
                mDeviceListAdapter.clear();
                mDeviceListAdapter.notifyDataSetChanged();
                scanDevice(true);

                SearchTimeoutHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                            mScanning = false;
                            scanDevice(false);

                            Toast.makeText(mContext, mContext.getResources().getString(R.string.CannotScan), Toast.LENGTH_SHORT).show();

                        }
                    }
                }, 20000);

            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();

                    SearchTimeoutHandler.removeCallbacksAndMessages(null);
                }
                mScanning = false;
                scanDevice(false);
            }
        }
    };

    ListView.OnItemClickListener tempDevice = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final BluetoothDevice device = mDeviceListAdapter.getDevice(position);
            if (device == null) {
                return;
            }

            if (updated != null)
                updated.setBackgroundColor(Color.TRANSPARENT);

            updated = view;
            deviceAddress = device.getAddress();
            Log.e("TAG", device.getAddress());
            view.setBackgroundColor(Color.GRAY);
        }
    };

    Button.OnClickListener connectDevice = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (deviceAddress == null) {
                Log.e("TAG", "Null address");
                return;
            }

            if (mScanning) {
                mScanning = false;
                scanDevice(false);
            }


            dialog = new ProgressDialog(mContext, R.style.AppCompatAlertDialogStyle);
            dialog.setTitle(mContext.getResources().getString(R.string.ConnectTitle));
            dialog.setMessage(mContext.getResources().getString(R.string.Connecting));
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
			
			ConnectTimeoutHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        mDeviceListAdapter.clear();
                        mDeviceListAdapter.notifyDataSetChanged();
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.ConnectFailed), Toast.LENGTH_SHORT).show();

                    }
                }
            }, 20000);

          //  mBluetoothAdapter.cancelDiscovery();
            mHandler.removeCallbacksAndMessages(null);

            mBluetoothConnectService.connect(deviceAddress);

            Log.e("TAG", "connect start " + deviceAddress);

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        mDeviceListAdapter = new LeDeviceListAdapter();
        deviceList.setAdapter(mDeviceListAdapter);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                finish();
                return;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        Log.d("TAG", "Act Device Connect. onPause");
        super.onPause();
        this.unregisterReceiver(mReceiver);
        if (mBluetoothConnectService != null && mBluetoothConnectService.isDisconnected())
            scanDevice(false);    // connecting이나 Conneted일때는 이미 정지되어있음
        mDeviceListAdapter.clear();
		
		        // force to cancel if scan handler working
        mScanning = false;
        mHandler.removeCallbacksAndMessages(null);
		if (dialog!=null && dialog.isShowing()) dialog.dismiss();
    }

    @Override
    protected void connectedDevice() {

        Log.d("ACB","connected initSt=" + mInitStatus + "svc=" + mBluetoothConnectService.toString());
        // 처음 연결되었을때는 초기화 시퀀스를 보낸다.
        if (mInitStatus == 0) {

            mInitReties = 0;
            if (mBluetoothConnectService != null) {
                Log.d("ACB","send REQ VER");
                mBluetoothConnectService.send(CMD_REQ_VER, "");
            }
            mInitStatus = 1;

            mTask = new TimerTask() {

                @Override
                public void run() {

                    Log.d("TAG", "Retry. sending status request again");
                    mInitReties++;
                    if (mInitReties > 3) {
                        Toast to=Toast.makeText(mContext, mContext.getResources().getString(R.string.ConnectFailed), Toast.LENGTH_SHORT);
                      //  setGlobalFont(to.getView());
                        to.show();
                    } else {
                        if (mBluetoothConnectService != null) {
                            mBluetoothConnectService.send(CMD_REQ_VER, "");


                            if (mRetryTimer != null) mRetryTimer.schedule(mTask, 1000);
                        } else {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.DisconnectWhileConnect), Toast.LENGTH_SHORT).show();
                        }
                    }
                } // end run
            };

            mRetryTimer = new Timer();
            mRetryTimer.schedule(mTask, 1000);


        }

    }

    @Override
    protected void dataAvailableCheck(String data) {

        Log.d("TAG", "DATA recved act activity");
        String[] sp=data.split(" ");

        if(sp.length!=20 || sp[0].equals("21")!=true || sp[19].equals("75")!=true ) {
            Log.e("BLE","Pkt dropped. s= "+data);
            return;
        }

        String cmd = data.split(" ")[3];
        if (cmd.length() == 1) cmd = "0" + cmd;

        // REQ_VER 응답
        if (cmd.equals(CMD_REQ_VER) && mInitStatus == 1) {
            Log.d("TAG", "Recv STATUS_RSP");

            if (mRetryTimer != null) mRetryTimer.cancel();

            String fw1=data.split(" ")[5];
            String fw2=data.split(" ")[4];
            String hw1=data.split(" ")[7];
            String hw2=data.split(" ")[6];

            if(fw1.length()==1) fw1 = "0" + fw1;
            if(fw2.length()==1) fw2 = "0" + fw2;
            if(hw1.length()==1) hw1 = "0" + hw1;
            if(hw2.length()==1) hw2 = "0" + hw2;

            Log.e("TAG", "dev conn. ver updated" + fw1 + "." + fw2 + ","
                    + hw1 + "."+ hw2 );

            BudUtil.getInstance().FWVersion = fw2 + "." + fw1;
            BudUtil.getInstance().HWVersion = hw2 + "."+ hw1;


            mInitStatus = 2;
            Intent intent = new Intent(this, Act_Home.class);
            startActivity(intent);

            ConnectTimeoutHandler.removeCallbacksAndMessages(null);
            // 장치연결 dialog
            dialog.dismiss();
            finish();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_COARSE_LOCATION) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (requestCode == PERMISSION_REQUEST_EXTERNAL_STORAGE) {

        }
    }

    private void scanDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(() -> {
                mScanning = false;

                // 블투끄고 들어와서 블투 켜진 경우
                if(mBluetoothAdapter==null)
                {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!mBluetoothAdapter.isEnabled()) mBluetoothAdapter.enable();
                }
                if( mBleScanner == null)
                {
                    mBleScanner =mBluetoothAdapter.getBluetoothLeScanner();
                }

                // BT -> BLE
                //mBluetoothAdapter.startDiscovery();
                //todo. check null state
                if(mLeScanCallback == null)
                {
                    Log.e("BLE","mLeScanCallback is null");
                }
                else mBleScanner.startScan(mLeScanCallback);

                invalidateOptionsMenu();
            }, 500);    // after 1000

           // mBluetoothAdapter.startDiscovery();
        } else {
            if(mBleScanner!=null) mBleScanner.stopScan(mLeScanCallback);
            mHandler.removeCallbacksAndMessages(null);
        }
        invalidateOptionsMenu();

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Act_Device_Connect Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class LeDeviceListAdapter extends BaseAdapter {

        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflater;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflater = Act_Device_Connect.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return mLeDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listitem_device, null);
              //  setGlobalFont(convertView);
                viewHolder = new ViewHolder();
                viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            BluetoothDevice device = mLeDevices.get(position);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText("Unknown");

            return convertView;
        }

    }

    private ScanCallback mLeScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   // Log.e("BLE","onScanResult");

                    final String deviceName = result.getDevice().getName();
                    Log.e("BLE","onScanResult. dev = " + result.getDevice().getName());
                    if (deviceName != null && deviceName.length() > 0) {
                        // filtering by bluetooth name.
                        if (result.getDevice().getName().contains("CRD-K100")) {
                            //Log.e("BLE","broadcast on recv" + result.getDevice().getName());
                            dialog.dismiss();
                            SearchTimeoutHandler.removeCallbacksAndMessages(null);
                            mDeviceListAdapter.addDevice(result.getDevice());
                            mDeviceListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.d("BLE", "BLE// onBatchScanResults");
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }

    };


    static class ViewHolder {
        TextView deviceName;
    }

}
