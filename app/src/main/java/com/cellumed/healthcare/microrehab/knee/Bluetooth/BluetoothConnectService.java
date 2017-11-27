package com.cellumed.healthcare.microrehab.knee.Bluetooth;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.content.Context;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.UnsupportedEncodingException;

@TargetApi(23)
public class BluetoothConnectService extends Service implements IMP_CMD {

    private final static String TAG = BluetoothConnectService.class.getSimpleName();



    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private int mConnectionState = STATE_DISCONNECTED;

    byte[] prev_data_byte=new byte[60];
    int prev_data_len=0;

    boolean serviceDiscovered=false;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    BluetoothGatt mBluetoothGatt;
    public String mBluetoothDeviceAddress;

    /* bluino
    public static final UUID SERVICE_UUID=UUID.fromString("0000dfb0-0000-1000-8000-00805f9b34fb");
    public static final UUID WRITE_UUID=UUID.fromString("0000dfb1-0000-1000-8000-00805f9b34fb");   // serial
    public static final UUID NOTIFY_UUID=UUID.fromString("0000dfb1-0000-1000-8000-00805f9b34fb");   // serial
    public static final UUID CommandUUID=UUID.fromString("0000dfb2-0000-1000-8000-00805f9b34fb");
*/

    //기존모듈
/*
    public static final UUID SERVICE_UUID=UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID WRITE_UUID=UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");   // android입장에서write.
    public static final UUID NOTIFY_UUID=UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");   // serial
    //public static final UUID WRITE_UUID=UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");   // serial
    //public static final UUID CommandUUID=UUID.fromString("0000dfb2-0000-1000-8000-00805f9b34fb");
*/

// 신규모듈

    public static final UUID SERVICE_UUID=UUID.fromString("49535343-fe7d-4ae5-8fa9-9fafd205e455");
    public static final UUID WRITE_UUID=UUID.fromString("49535343-1e4d-4bd9-ba61-23c647249616");   // android입장에서write.
    public static final UUID NOTIFY_UUID=UUID.fromString("49535343-1e4d-4bd9-ba61-23c647249616");   // serial
    //public static final UUID WRITE_UUID=UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");   // serial
    //public static final UUID CommandUUID=UUID.fromString("0000dfb2-0000-1000-8000-00805f9b34fb");

    //To tell the onCharacteristicWrite call back function that this is a new characteristic,
    //not the Write Characteristic to the device successfully.
    private static final int WRITE_NEW_CHARACTERISTIC = -1;
    //define the limited length of the characteristic.
    private static final int MAX_CHARACTERISTIC_LENGTH = 17;
    //Show that Characteristic is writing or not.
    private boolean mIsWritingCharacteristic=false;


    //class to store the Characteristic and content string push into the ring buffer.
    private class BluetoothGattCharacteristicHelper{
        BluetoothGattCharacteristic mCharacteristic;
        String mCharacteristicValue;
        BluetoothGattCharacteristicHelper(BluetoothGattCharacteristic characteristic, String characteristicValue){
            mCharacteristic=characteristic;
            mCharacteristicValue=characteristicValue;
        }
    }
    //ring buffer
    private RingBuffer<BluetoothGattCharacteristicHelper> mCharacteristicRingBuffer = new RingBuffer<BluetoothGattCharacteristicHelper>(8);


    public final static String ACTION_CONNECTED =
            "com.cellumed.healthcare.microrehab.ems_ui.ACTION_CONNECTED";
    public final static String ACTION_DISCONNECTED =
            "com.cellumed.healthcare.microrehab.ems_ui.ACTION_DISCONNECTED";
    public final static String ACTION_WRONG_ID =
            "com.cellumed.healthcare.microrehab.ems_ui.ACTION_WRONG_ID";
    public final static String ACTION_DATA_AVAILABLE =
            "com.cellumed.healthcare.microrehab.ems_ui.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.cellumed.healthcare.microrehab.ems_ui.EXTRA_DATA";

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String userId ="C1"; // ble k-100 default id no. 0xc1
    private static boolean isInit = false;

    private static BluetoothGattCharacteristic mSCharacteristic, mModelNumberCharacteristic, mSerialPortCharacteristic, mCommandCharacteristic;

    private byte[] last_unacked_bytes;

    public class LocalBinder extends Binder {
        BluetoothConnectService getService() {
            return BluetoothConnectService.this;
        }
    }

    // init Bluetooth adapter
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        Log.d("BluetoothLeService",  "initialize"+mBluetoothManager);
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBinder;
    }

    /**
     * 주소로 연결하기
     *
     * @param address mac address
     */
    public void connect(String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return;
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

        synchronized(this)
        {
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        }
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;


        PreferenceUtil.putLastRequestDeviceAddress(address);
    }


    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {

        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }


    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
           if (newState == BluetoothProfile.STATE_CONNECTED) {

                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
               serviceDiscovered=false;

                if(mBluetoothGatt.discoverServices())
                {
                    Log.i(TAG, "Attempting to start service discovery:");

                }
                else{
                    Log.i(TAG, "Attempting to start service discovery:not success");

                }

               Handler handler = new Handler(Looper.getMainLooper());
               handler.postDelayed(new Runnable() {
                   public void run() {
                       String act;
                       act = ACTION_CONNECTED;
                       mConnectionState = STATE_CONNECTED;
                       broadcastUpdate(act);
                   }
               }, 500);





            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());
            //gatt.readCharacteristic(services.get(1).getCharacteristics().get(0));

            // notify uuid enable
            if (status == BluetoothGatt.GATT_SUCCESS) {
               BluetoothGattCharacteristic characteristic = mBluetoothGatt.getService(SERVICE_UUID).getCharacteristic(NOTIFY_UUID);
                for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                }
                mBluetoothGatt.setCharacteristicNotification(characteristic, true);

            } else {
                Log.w(TAG, "onServicesDiscoverd receive : " + status);
            }
            //setCharacteristicNotification
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            //this block should be synchronized to prevent the function overloading
            synchronized(this)
            {
                //CharacteristicWrite success
                if(status == BluetoothGatt.GATT_SUCCESS)
                {
                    System.out.println("onCharacteristicWrite success:"+ new String(characteristic.getValue()));
                    if(mCharacteristicRingBuffer.isEmpty())
                    {
                        mIsWritingCharacteristic = false;
                    }
                    else
                    {
                        BluetoothGattCharacteristicHelper bluetoothGattCharacteristicHelper = mCharacteristicRingBuffer.next();
                        if(bluetoothGattCharacteristicHelper.mCharacteristicValue.length() > MAX_CHARACTERISTIC_LENGTH)
                        {
                            try {
                                bluetoothGattCharacteristicHelper.mCharacteristic.setValue(bluetoothGattCharacteristicHelper.mCharacteristicValue.substring(0, MAX_CHARACTERISTIC_LENGTH).getBytes("ISO-8859-1"));

                            } catch (UnsupportedEncodingException e) {
                                // this should never happen because "US-ASCII" is hard-coded.
                                throw new IllegalStateException(e);
                            }


                            if(mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicHelper.mCharacteristic))
                            {
                                System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":success");
                            }else{
                                System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":failure");
                            }
                            bluetoothGattCharacteristicHelper.mCharacteristicValue = bluetoothGattCharacteristicHelper.mCharacteristicValue.substring(MAX_CHARACTERISTIC_LENGTH);
                        }
                        else
                        {
                            try {
                                bluetoothGattCharacteristicHelper.mCharacteristic.setValue(bluetoothGattCharacteristicHelper.mCharacteristicValue.getBytes("ISO-8859-1"));
                            } catch (UnsupportedEncodingException e) {
                                // this should never happen because "US-ASCII" is hard-coded.
                                throw new IllegalStateException(e);
                            }

                            if(mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicHelper.mCharacteristic))
                            {
                                System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":success");
                            }else{
                                System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":failure");
                            }
                            bluetoothGattCharacteristicHelper.mCharacteristicValue = "";

//	            			System.out.print("before pop:");
//	            			System.out.println(mCharacteristicRingBuffer.size());
                            mCharacteristicRingBuffer.pop();
//	            			System.out.print("after pop:");
//	            			System.out.println(mCharacteristicRingBuffer.size());
                        }
                    }
                }
                //WRITE a NEW CHARACTERISTIC
                else if(status == WRITE_NEW_CHARACTERISTIC)
                {
                    if((!mCharacteristicRingBuffer.isEmpty()) && mIsWritingCharacteristic==false)
                    {
                        BluetoothGattCharacteristicHelper bluetoothGattCharacteristicHelper = mCharacteristicRingBuffer.next();
                        if(bluetoothGattCharacteristicHelper.mCharacteristicValue.length() > MAX_CHARACTERISTIC_LENGTH)
                        {

                            try {
                                bluetoothGattCharacteristicHelper.mCharacteristic.setValue(bluetoothGattCharacteristicHelper.mCharacteristicValue.substring(0, MAX_CHARACTERISTIC_LENGTH).getBytes("ISO-8859-1"));
                            } catch (UnsupportedEncodingException e) {
                                // this should never happen because "US-ASCII" is hard-coded.
                                throw new IllegalStateException(e);
                            }

                            if(mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicHelper.mCharacteristic))
                            {
                                System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":success");
                            }else{
                                System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":failure");
                            }
                            bluetoothGattCharacteristicHelper.mCharacteristicValue = bluetoothGattCharacteristicHelper.mCharacteristicValue.substring(MAX_CHARACTERISTIC_LENGTH);
                        }
                        else
                        {
                            try {
                                bluetoothGattCharacteristicHelper.mCharacteristic.setValue(bluetoothGattCharacteristicHelper.mCharacteristicValue.getBytes("ISO-8859-1"));
                            } catch (UnsupportedEncodingException e) {
                                // this should never happen because "US-ASCII" is hard-coded.
                                throw new IllegalStateException(e);
                            }


                            if(mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicHelper.mCharacteristic))
                            {
                                System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":success");
//	            	        	System.out.println((byte)bluetoothGattCharacteristicHelper.mCharacteristic.getValue()[0]);
//	            	        	System.out.println((byte)bluetoothGattCharacteristicHelper.mCharacteristic.getValue()[1]);
//	            	        	System.out.println((byte)bluetoothGattCharacteristicHelper.mCharacteristic.getValue()[2]);
//	            	        	System.out.println((byte)bluetoothGattCharacteristicHelper.mCharacteristic.getValue()[3]);
//	            	        	System.out.println((byte)bluetoothGattCharacteristicHelper.mCharacteristic.getValue()[4]);
//	            	        	System.out.println((byte)bluetoothGattCharacteristicHelper.mCharacteristic.getValue()[5]);

                            }else{
                                System.out.println("writeCharacteristic init "+new String(bluetoothGattCharacteristicHelper.mCharacteristic.getValue())+ ":failure");
                            }
                            bluetoothGattCharacteristicHelper.mCharacteristicValue = "";

//		            			System.out.print("before pop:");
//		            			System.out.println(mCharacteristicRingBuffer.size());
                            mCharacteristicRingBuffer.pop();
//		            			System.out.print("after pop:");
//		            			System.out.println(mCharacteristicRingBuffer.size());
                        }
                    }

                    mIsWritingCharacteristic = true;

                    //clear the buffer to prevent the lock of the mIsWritingCharacteristic
                    if(mCharacteristicRingBuffer.isFull())
                    {
                        mCharacteristicRingBuffer.clear();
                        mIsWritingCharacteristic = false;
                    }
                }
                else
                //CharacteristicWrite fail
                {
                    mCharacteristicRingBuffer.clear();
                    System.out.println("onCharacteristicWrite fail:"+ new String(characteristic.getValue()));
                    System.out.println(status);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i("OnCharacteristicRead", characteristic.toString());
           // if (characteristic != null)
               // broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic.toString());
            //gatt.disconnect();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
           // buno ble. 시리얼uuid를 통한 noti
            // broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic.toString());
            Log.i("OnCharacteristicChanged", characteristic.toString());
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, data);
            }
           // Log.e("TAG", "CHANGED");
        }
    };



            //temp 열고 리턴
          //  broadcastUpdate(ACTION_CONNECTED);  /// act_device_connect에서 act_home 열때만 사용됨
            //  broadcastUpdate(ACTION_DISCONNECTED);
            //  mConnectionState = STATE_DISCONNECTED;


         //   mConnectionState = STATE_CONNECTED;

    //        broadcastUpdate(ACTION_CONNECTED);  /// act_device_connect에서 act_home 열때만 사용됨.


/*
                Log.d(TAG," inp = "+ bytes2String(buffer, bytes));

                // update version if incoming packet is STATUS_RSP
                if (buffer[2] == 0x11) {

                    // STATUS = Ready (0x0B)

                    BudUtil.getInstance().FWVersion = buffer[4] + "." + buffer[5];
                    BudUtil.getInstance().HWVersion = buffer[6] + "." + buffer[7];

                    Log.d("recv STATUS_RSP","FW "+BudUtil.getInstance().FWVersion);
                    Log.d("recv STATUS_RSP","HW "+BudUtil.getInstance().HWVersion);
                }

                broadcastUpdate(ACTION_DATA_AVAILABLE, bytes2String(buffer, bytes));
*/

/*
                try {
                    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                    mmSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                    mmSocket.connect();
                    mmInStream = mmSocket.getInputStream();
                    mmOutStream = mmSocket.getOutputStream();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    mConnectionState = STATE_DISCONNECTED;
                    broadcastUpdate(ACTION_DISCONNECTED);

                    break;
                }
*/




    private String bytes2String(byte[] b, int count) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            String myInt = Integer.toHexString((int) (b[i] & 0xFF));
            result.add(myInt);
        }
        return TextUtils.join(" ", result);
    }


    private void broadcastUpdate(final String action) {
        Log.e("TAG", "Send broadcast");
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final byte[] b_data) {
        Log.e("BTService","rcv=" + bytes2String(b_data,b_data.length));
      //  String cmd =data.split(" ")[2] ;
        final Intent intent = new Intent(action);
        String out_str;
        byte[] com_data=null;    // complete data
        //jun@ reassemble if packet is not completed

        int i,j;
        int first_pos=0;

        if((b_data[0] & 0xFF) != 0x21 ) {
            for (first_pos = 1; first_pos < b_data.length; first_pos++)
                if (((b_data[first_pos - 1] & 0xff) == 0x75) && (b_data[first_pos] & 0xFF) == 0x21)
                    break;

    //        Log.e("BTService","not 21 first=" + Integer.toString(first_pos)+ "prev_len="+ Integer.toString(prev_data_len));
            if (prev_data_len != 0) // 시작하기 전까지 복사
            {
                j = prev_data_len;
                for (i = 0; i < first_pos; i++) prev_data_byte[j + i] = b_data[i];
                prev_data_len += first_pos;
            }

            // drop

            if (prev_data_len < 20 && first_pos == b_data.length) {
                // 20보다 작지만 끝까지 다 복사한 경우. 아직 시작이 발견안됐으니 다음 패킷 받을때까지 넘김
            } else if (prev_data_len == 20) {
                com_data = new byte[20];
                System.arraycopy(prev_data_byte, 0, com_data, 0, 20);
                System.arraycopy(b_data, first_pos, prev_data_byte, 0, b_data.length - first_pos);
                prev_data_len=b_data.length - first_pos;
               // Log.e("BTService","merged=" + bytes2String(com_data, com_data.length));
             //   Log.e("BTService","new prev=" + bytes2String(prev_data_byte, prev_data_len));


            } else {
                // 이전거 버림
                prev_data_len = 0;
            }
        }
        else if(b_data.length != 20) {  // 21로 시작됮미ㅏㄴ 패킷 길이 아닌경우
            if (prev_data_len == 0) {
                System.arraycopy(b_data, 0, prev_data_byte, 0, b_data.length);
                prev_data_len=b_data.length;
            }
        }
        else {  // 21시작 20byte
            com_data = new byte[20];
            System.arraycopy(b_data, 0, com_data, 0, b_data.length);
        }

        if(com_data!=null && com_data.length==20) {
            out_str = bytes2String(com_data, com_data.length);
            Log.e("BTService", "comp=" + out_str);
            if(checksumOK(out_str)==true)
            {

                intent.putExtra(EXTRA_DATA, out_str);
                sendBroadcast(intent);

            }
            else  Log.e("BTService", "crc error");

        }
        else
        {
            Log.e("BTService","Pkt dropped. s= "+bytes2String(b_data,b_data.length));
            return;
        }

    }

    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len - 1; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public boolean isConnected() {
        return mConnectionState == STATE_CONNECTED;
    }

    public boolean isDisconnected() {
        return mConnectionState == STATE_DISCONNECTED;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }


    /**
     * Write information to the device on a given {@code BluetoothGattCharacteristic}. The content string and characteristic is
     * only pushed into a ring buffer. All the transmission is based on the {@code onCharacteristicWrite} call back function,
     * which is called directly in this function
     *
     * @param characteristic The characteristic to write to.
     */
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        //The character size of TI CC2540 is limited to 17 bytes, otherwise characteristic can not be sent properly,
        //so String should be cut to comply this restriction. And something should be done here:
        String writeCharacteristicString;
        try {
            writeCharacteristicString = new String(characteristic.getValue(),"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            // this should never happen because "US-ASCII" is hard-coded.
            throw new IllegalStateException(e);
        }
        System.out.println("allwriteCharacteristicString:"+writeCharacteristicString);

        //As the communication is asynchronous content string and characteristic should be pushed into an ring buffer for further transmission
        mCharacteristicRingBuffer.push(new BluetoothGattCharacteristicHelper(characteristic,writeCharacteristicString) );
        System.out.println("mCharacteristicRingBufferlength:"+mCharacteristicRingBuffer.size());


        //The progress of onCharacteristicWrite and writeCharacteristic is almost the same. So callback function is called directly here
        //for details see the onCharacteristicWrite function
        mGattCallback.onCharacteristicWrite(mBluetoothGatt, characteristic, WRITE_NEW_CHARACTERISTIC);

    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }


    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    //-----------

    private boolean checksumOK(String data) {
        String[] split_data=data.split(" ");

        int length = (split_data.length);
        int checkSum = 0;

        if(length !=20 ) return false;

        // except stx, etx, checksum
        for (int i = 1; i < length -2; i ++) {
            //tmp = Integer.parseInt(data.substring(i, i + 2), 16);
            checkSum += Integer.parseInt(split_data[i], 16);
        }

        if (checkSum > 255)
            checkSum = checkSum & 0x00ff;

        return (checkSum == Integer.parseInt(split_data[length-2],16));
     /*
        if(data.length()!=20) return false; // data length is always 40. hex

        int checkSum = 0;
        String hex;

        byte b[]=data.getBytes(StandardCharsets.US_ASCII);
        byte b2[]=data.getBytes();

        String myInt;
        // except stx, etx, checksum
        for (int i = 1; i < 18; i ++) {
            //tmp = Integer.parseInt(data.substring(i, i + 2), 16)
            checkSum += (b[i] & 0xff);
           myInt = Integer.toHexString((int) (b[i] & 0xFF));

        }

        if (checkSum > 255)
            checkSum = checkSum & 0x00ff;


        return (checkSum == b[18]);
        */
    }

    private String makeData(String cmd, String data) {
        String header = "21";
        String footer = "75";

        int length = 20;    // length is always 20
        int checkSum = 0;

        String outdata="C100"+cmd + data + "0000000000000000000000000000".substring(0,28-data.length());
        for (int i = 0; i < outdata.length() ; i += 2) {
            //tmp = Integer.parseInt(data.substring(i, i + 2), 16);
            checkSum += Integer.parseInt(outdata.substring(i, i + 2), 16);

        }

        if (checkSum > 255)
            checkSum = checkSum & 0x00ff;
        return header + outdata + String.format("%02X", checkSum) + footer;
    }

    private String byteToString(byte a) {
        char[] hexChars = new char[2];
        int v = a & 0xFF;
        hexChars[0] = hexArray[v >>> 4];
        hexChars[1] = hexArray[v & 0x0F];

        return new String(hexChars);
    }

    private String byteArrayToString(byte[] a) {
        char[] hexChars = new char[a.length * 3];
        for (int j = 0; j < a.length; j++) {
            int v = a[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = new String(" ").charAt(0);
        }

        return new String(hexChars);
    }

    private byte[] intToByteArray(final int integer) {
        ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE / 8);
        buff.putInt(integer);
        buff.order(ByteOrder.BIG_ENDIAN);
        return buff.array();
    }

    public boolean send(String cmd,String data) {
        return send_str(makeData(cmd, data));
    }

    public boolean send_str(String s) {
        try {
            if (mConnectionState == STATE_CONNECTED) {
                BluetoothGattService service = mBluetoothGatt.getService(SERVICE_UUID);
                if (service == null) {
                    Log.e("Tag","Connection State error");
                    return false;
                }
                else {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(WRITE_UUID);

                    if (characteristic == null) return false;


                    byte b[] = hexStringToByteArray(s);
                    characteristic.setValue(b);    //jun@. tx values should be byte array. not hex
                    //characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    boolean status = mBluetoothGatt.writeCharacteristic(characteristic);

                    Log.e("TAG", "SEND DATA s="+s+" r="+ status);
                    return status;
                }
            }
        } catch(Exception e)
        {
            // 중지되거나 해서 정상적으로 thread 생성을 안 한 경우 thread null exception.
            // 재연결로 이동함
            mConnectionState = STATE_DISCONNECTED;
            broadcastUpdate(ACTION_DISCONNECTED);
        }

        return false;
    }



}
