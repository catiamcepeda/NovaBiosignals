package org.apache.cordova.bioplux;

import java.util.regex.Pattern;

import org.apache.cordova.bioplux.BPException;


public abstract class Device {
		protected static byte seq,  nbuf;
		protected static byte buffer[] = new byte[14];
		protected static short oldch6,  oldch7;
		protected boolean extMode = false;
		protected byte numberBits;
		protected byte channelNumber;
		protected int frequency;
		protected String description;
	 	
		public static class Frame {
	      public Frame() {
	         an_in = new short[8];    	  
	      }
	      public byte     seq;
	      public boolean  dig_in;
	      public short[]  an_in;
	   };
	   
	   public static Device Create(String port) throws BPException{
			if ("test".equalsIgnoreCase(port)) {
				return new DeviceTest();
			}
			if (Pattern.matches("^([0-9a-fA-F][0-9a-fA-F]:){5}([0-9a-fA-F][0-9a-fA-F])$", port)) {
				return new DeviceBluetooth(port);	
			}
			return new DeviceFile(port);
	   }
	   
	   public abstract String GetDescription()  throws BPException;
	   public abstract void   BeginAcq()  throws BPException;
	   public abstract void   BeginAcq(int freq, int chmask, int nbits)  throws BPException;
	   public abstract void   GetFrames(int nframes, Frame[] frames)  throws BPException;
	   public abstract void   SetDOut(boolean dout)  throws BPException;
	   
	   /** RGOMES **/
	   public abstract void   TurnOn1()  throws BPException;
	   public abstract void   TurnOff1()  throws BPException;
	   public abstract void   TurnOn2()  throws BPException;
	   public abstract void   TurnOff2()  throws BPException;
	   public abstract void   setInterval(int interval)  throws BPException;
	   public abstract void   setThreshold(int threshold)  throws BPException;
	   public abstract void   getData()  throws BPException;
	   /** RGOMES **/
	   
	   /**
	    * FUNCTION TO SET THRESHOLD FOR BIOPLUX ACCESS VIBRATION
	    */
	   public abstract void   setVibrationThreshold(int thresold) throws BPException;
	   
	   public abstract void   EndAcq()  throws BPException;
	   public abstract void   Close()  throws BPException;

}
