package org.apache.cordova.bioplux;

import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.bioplux.Device.Frame;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.annotation.TargetApi;


/**
 * bioPLUX Plugin for Cordova
 *
 * @version 	0.0.1
 * @author  	GTelo
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class BioPluxPlugin extends CordovaPlugin
{
	private static final String LOG_TAG					= "bioPLUXPlugin";

	private static final String ACTION_BEGINACQ			= "beginAcq";
	private static final String ACTION_ENDACQ			= "endAcq";
	private static final String ACTION_GETDATA		= "getData";

	/**
	* Bioplux BT service
	**/
	public BioPluxSimpleService mDevice;
	
	private CallbackContext dataCallback;



	/**
	 * Initialize the Plugin, Cordova handles this.
	 *
	 * @param cordova	Used to get register Handler with the Context accessible from this interface
	 * @param view		Passed straight to super's initialization.
	 */
	public void initialize(CordovaInterface cordova, CordovaWebView view)
	{
		super.initialize(cordova, view);
	
		mDevice = new BioPluxSimpleService(mHandler);
		
	}
	
	/**
	 * Executes the given action.
	 *
	 * @param action		The action to execute.
	 * @param args			Potential arguments.
	 * @param callbackCtx	Babby call home.
	 */
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackCtx)
	{
		if (ACTION_BEGINACQ.equals(action)){
			beginAcq(args, callbackCtx);
		}
		else if (ACTION_ENDACQ.equals(action)){
			endAcq(args, callbackCtx);
		}
		else if(ACTION_GETDATA.equals(action))
		{
			getData(args, callbackCtx);
		}
		else
		{
			Log.e(LOG_TAG, "Invalid Action[" + action + "]");
			callbackCtx.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
		}

		return true;
	}

	/**
	 * Begin an acquisition.
	 *
	 * @param args			Arguments given.
	 * @param callbackCtx	Where to send results.
	 */
	public void beginAcq(JSONArray args, CallbackContext callbackCtx)
	{
		try{
			String macAddress = args.getString(0);		
			int fs = args.getInt(1);		
			int channels = args.getInt(2); 	
			int nbits = args.getInt(3);		
			int nframes = args.getInt(4);
			callbackCtx.sendPluginResult(new PluginResult(PluginResult.Status.OK, mDevice.beginAcq(macAddress, fs, channels, nbits, nframes) ));
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Error on beginAcq: " + e.getMessage());
		}
	}
	
	
	/**
	 * Stop acquisition and connection.
	 */
	private void endAcq(JSONArray args, CallbackContext callbackCtx)
	{
		try
		{
			callbackCtx.sendPluginResult(new PluginResult(PluginResult.Status.OK, mDevice.endAcq() ));
		}
		catch(Exception e)
		{
			Log.e(LOG_TAG, "Error on endAcq: " + e.getMessage());
		}
	}

	
	private void getData(JSONArray args, CallbackContext callbackCtx)
	{
		dataCallback = callbackCtx;

        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackCtx.sendPluginResult(result);
	}
	
	ArrayList <Integer> dataArray;
    /**
     * Callback routine that operates asynchronously and handles inputs received
     */
    private final Handler mHandlerD1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_FREQ:
                    String f = (String) msg.obj;
                    break;
                case Constants.MESSAGE_READ:
                    dataArray = (ArrayList <Integer>) msg.obj;     

                    JSONArray data = new JSONArray(dataArray);

                    if( dataCallback != null){
                    	PluginResult result = new PluginResult(PluginResult.Status.OK, data);
                        result.setKeepCallback(true);
                        dataCallback.sendPluginResult(result);
                    }

                    break;
                case Constants.MESSAGE_CONNECTED    :
                    // save address of the bioPLUX device
                    /*if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + address, Toast.LENGTH_SHORT).show();
                    }*/
                    break;
            }
        }
    };
    
}
