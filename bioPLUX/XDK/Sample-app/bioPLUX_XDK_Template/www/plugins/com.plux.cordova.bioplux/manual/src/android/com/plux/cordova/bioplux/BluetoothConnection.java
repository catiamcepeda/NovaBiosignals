package org.apache.cordova.bioplux;
/**
 * @author Gon�alo Martins
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

public final class BluetoothConnection implements IConnection  {
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothDevice _device;
	private BluetoothSocket _socket;
	private BluetoothServerSocket _socket_server;
	private InputStream _inStream;
    private OutputStream _outStream;
    public int NOT_CONNECTED = 0;
    public int CONNECTED = 1;
    private int state = NOT_CONNECTED;
    private static final String MY_UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"; // "00001101-0000-1000-8000-00805F9B34FB";
    private static final String TAG_BIOPLUX = "BioPlux";
    
	public BluetoothConnection() throws BPException{
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	       if (!bluetoothAdapter.isEnabled()) {
	    	   throw new BPException(BPErrorTypes.BT_ADAPTER_NOT_FOUND);
	       }
	}
	public BluetoothConnection(Activity dataServerActivity) {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	       if (bluetoothAdapter == null) {
	    	   // EXCEPTION "Bluetooth is not supported"
	           return;
	       }else{
	    	   if(!bluetoothAdapter.isEnabled()){
	    		   dataServerActivity.startActivityForResult(new Intent(bluetoothAdapter.ACTION_REQUEST_ENABLE), 0);
	    	   }
	       }
	}
	
	public void connect(String macAdress) throws BPException {
		
		System.out.println("trying to CONNECT to: " + macAdress);
		
        bluetoothAdapter.cancelDiscovery();
        
//        _device.
        
		if(!BluetoothAdapter.checkBluetoothAddress(macAdress)){
			_device = null;
		}
        	
        _device = bluetoothAdapter.getRemoteDevice(macAdress.toUpperCase());
        
        System.out.println("device is : " + _device.getName() + ". Bond state = " + _device.getBondState());
       
        
        try {       	
       		/*
    		Method m = _device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class }); //new Class[] { UUID.class } );
    		_socket = (BluetoothSocket)m.invoke(_device, UUID.fromString(MY_UUID_STRING));
        	*/
    		
        	_socket = _device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(MY_UUID_STRING));
    	
            int bondState = _device.getBondState();
            System.out.println("Device "+macAdress+" Bond State : "+ bondState);
    	
            _socket.connect();
        	_inStream = _socket.getInputStream();
        	_outStream = _socket.getOutputStream();
        } catch (Exception e) {
        	Log.e(TAG_BIOPLUX, macAdress + " " + e.getMessage());
        	state = NOT_CONNECTED;
        	if (_socket != null) {
        		try {
					_socket.close();
					//_socket_server.close();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
        	throw new BPException(BPErrorTypes.BT_DEVICE_NOT_FOUND);
	}
}
public void old_connect(String macAdress) throws BPException {
		
        bluetoothAdapter.cancelDiscovery();
        
		if(!BluetoothAdapter.checkBluetoothAddress(macAdress)){
			_device=null;
		}
        	
        _device = bluetoothAdapter.getRemoteDevice(macAdress.toUpperCase());      
    
        try {           
            _socket = _device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(MY_UUID_STRING));
            
            int bondState = _device.getBondState();
            System.out.println("Device "+macAdress+" Bond State : "+ bondState);

        	_socket.connect();
        	
        	_inStream = _socket.getInputStream();
        	_outStream = _socket.getOutputStream();
        	
        } catch (Exception e) {
        	Log.e(TAG_BIOPLUX,macAdress+" "+e.getMessage());
        	state = NOT_CONNECTED;
        	if (_socket!=null) {
        		try {
					_socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
        	}
        	throw new BPException(BPErrorTypes.BT_DEVICE_NOT_FOUND);
    	}
	}
	
	public void disconnect() throws IOException {
		_socket.close();
	}

	public void write(byte[] message) throws IOException{
	    if(_outStream!=null)
            _outStream.write(message);
		  else
			state = NOT_CONNECTED;	
	}

	public int getState() {
		return 0;
	}

	public int read(byte[] buffer,int offset,int toread) throws IOException {
//		try{
				return _inStream.read(buffer,offset,toread);
			//}
//		}
//		catch (Exception e) {
			//Log.d("Exception", e.getMessage());
//			return 0;
//		}

	}
	public byte[] read() {
		try{
			//if(state!=NOT_CONNECTED){
				byte[] buffer = new byte[1000];
				int offset=0;
				int available;
				while ((available=_inStream.available())>0) {
					int bytes = _inStream.read(buffer,offset,available);
					offset+=bytes;
				}
				byte [] returnBa = new byte[offset];
				//System.arraycopy(src, srcPos, dest, destPos, length);
				System.arraycopy(buffer, 0, returnBa, 0, offset);
				return returnBa;
			//}
		}
		catch (Exception e) {
			Log.d("BioPlux", e.getMessage());
		}
		return null;
	}
}
