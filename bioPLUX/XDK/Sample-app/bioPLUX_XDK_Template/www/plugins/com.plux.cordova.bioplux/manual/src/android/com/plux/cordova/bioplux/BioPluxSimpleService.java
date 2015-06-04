package org.apache.cordova.bioplux;

import org.apache.cordova.bioplux.Device.Frame;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

import java.util.List;
import android.webkit.WebView;
import android.os.Handler;
import android.util.Log;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.apache.cordova.CordovaWebView;


/**
 * Creates a connection with a bioplux device and receives frames sent from
 * device
 * 
 * 
 */
public class BioPluxSimpleService {

	private static final String TAG = "BPService";


	private int numberOfFrames;

	private Device mBPDevice;
	private DataThread mDataThread;
	
	private Handler mHandler;
	
	public Frame[] frames;

	private int fs;
	private int nCh;
	private double samplingFrames;
	private double samplingCounter = 0;
	
    private int frameSeq =-1;
    int i =-1;
	//Number of samples
    int c = 0;
    Timestamp t1;
    Timestamp t2;
    boolean time = true;
    int n = 0, x = 0;
    int len = nCh * numberOfFrames;
	boolean flag = false;

	/*
	Constructor for the BioPluxSimpleService class.
	*/
	public BioPluxSimpleService(Handler handler){
		mHandler = handler;
	}
	

    /**
     * Begin Acquisition
     * @param macAddress
     * @param sampfreq - sampling frequency
     * @param channels - number of channels
     * @param bits - number of bits
     * @return
     */
    public boolean beginAcq(String macAddress, int sampfreq, int channels, int bits, int nframes){
    	numberOfFrames = nframes;
    	fs = sampfreq;
    	nCh = channels;
        frames = new Device.Frame[numberOfFrames];
        for (int i = 0; i < frames.length; i++){
            frames[i] = new Frame();
        }

        if (connectDevice(macAddress, fs, channels, bits)){
            // Start the thread to manage the connection and perform transmissions
            flag = true;
            mDataThread = new DataThread();
            mDataThread.start();
            return true;
        } return false;
    }


    /**
     * Get frames from the bioplux device
     */
    public void getFrames(int numberOfFrames) {
        try {
            mBPDevice.GetFrames(numberOfFrames, frames);
        } catch (BPException e) {
            Log.e(TAG, "Exception getting frames", e);
            try {
                mBPDevice.Close();
            } catch (BPException e1) {
                e1.printStackTrace();
            }
        }
    }			

	/**
     * Establish connection with bioPLUX
     * @param address - macAddress
     * @param fs - Sampling Frequency
     * @param channels - number of channels
     * @param bits - number of bits
     * @return
     */
    private boolean connectDevice(String address, int fs, int channels, int bits) {

        try {
            mBPDevice = Device.Create(address);
            int ch = (int)(Math.pow(2, channels) - 1);
            mBPDevice.BeginAcq(fs, ch, bits);
        }catch (BPException e){
            try {
                mBPDevice.Close();
            }catch (BPException e1) {
                Log.e(TAG, "bioplux close connection exception", e1);
                return false;
            }

            Log.e(TAG, "Bioplux connection exception", e);
            return false;
        }
        return true;
    }

    /**
     * Stops the service properly whilst being destroyed
     */
    public boolean endAcq(){
        try {
            flag = false;
            mDataThread.interrupt();
            mDataThread = null;
            mHandler.removeMessages(Constants.MESSAGE_READ);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mBPDevice.EndAcq();
            mBPDevice.Close();
            Log.i(TAG,"Stop Acquisition");
            return true;
        } catch (BPException e) {
            Log.e(TAG, "Exception ending ACQ", e);
        }
        return false;
    }
    
    
    /**
     * This thread runs during a connection with a bioPLUX.
     * It handles all incoming transmissions.
     */
    private class DataThread extends Thread {

        public DataThread() {
            Log.d(TAG, "create DataThread");
        }

        public void run() {
            Log.i(TAG, "BEGIN DataThread");

            // Keep listening while connected
            while (flag) {
                if(time) {
                    Date date = new Date();
                    t1 = new Timestamp(date.getTime());
                }
                getFrames(numberOfFrames);
                ArrayList <Integer> framesArray = new ArrayList <Integer>(len);
                for (Frame frame : frames) {
                    //timeStamp - check performance

                    //
                    i++;
                    frameSeq = frameSeq + 1 < 128 ? frameSeq + 1 : 0;

                    if (frameSeq != frame.seq || i == 0) {

                        Log.e(TAG, "frameSeq " + frameSeq + " frame.seq " + frame.seq);

                        frameSeq = frame.seq;
                    }

                    if (samplingCounter++ >= samplingFrames) {
                        c++;
                        
                        for(int i = 0; i < nCh; i++){
                        	framesArray.add((int)frame.an_in[i]);
                        }

                        // retains the decimals
                        samplingCounter -= samplingFrames;

                        if(c == fs){
                            time = false;
                            //Bluetooth transmission fix -  API above 19
                            try {
                                mBPDevice.SetDOut(true);
                            } catch (BPException e) {
                                e.printStackTrace();
                            }
                        }

                        if(time == false) {
                            Double f;
                            double deltat;
                            if(n==0) {
                                n = 1;
                                f = 0.0;
                                deltat = 0.0;
                            }
                            else {
                                deltat = (double) (t1.getTime() - t2.getTime());
                                f = fs * (1000.0 / deltat);
                            }
                            x += c;
                            c = 0;
                            time = true;
                            t2 = new Timestamp(t1.getTime());
                            DecimalFormat df = new DecimalFormat("00.00");
                            /*mHandler.obtainMessage(Constants.MESSAGE_FREQ,df.format(f))
                                    .sendToTarget();*/

                            System.out.println(String.format("%d" + " " + "%.4f",x,f));
                        }
                    }
                }
                
             // Send the obtained frames to the UI Activity
                mHandler.obtainMessage(Constants.MESSAGE_READ,framesArray)
                        .sendToTarget();
                
            }
        }
    }

}