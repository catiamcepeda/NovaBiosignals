package org.apache.cordova.bioplux;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.os.Environment;

public class DeviceFile extends Device {
	private static final String PLUXDATA_DIRECTORY ="PluxData";
	String mFileName="";
	File mFile;
	BufferedReader mReader;
	String mHeader=""; 
	public DeviceFile(String port) throws BPException {
		mFileName=port;
		File file;
		if (getStorageState()) {
			file = Environment.getExternalStorageDirectory();
		} else {
			file = Environment.getDataDirectory();
		}
		String root = file.getAbsolutePath();
		File dataDir = new File(root,PLUXDATA_DIRECTORY);
		mFile = new File(dataDir.getAbsolutePath(),mFileName);
		try {
			mReader = new BufferedReader(new FileReader(mFile));
	
			// Read the header
			for (int i=0;i<8;i++) {
				String line = mReader.readLine(); 
				if (i>1&&i<7) { // Skip First and last line of Header
					mHeader+=line;
				}
			}
		} catch (FileNotFoundException e) {
			throw  new BPException(BPErrorTypes.BT_DEVICE_NOT_FOUND);
		} catch (IOException e) {
			throw  new BPException(BPErrorTypes.PORT_COULD_NOT_BE_OPENED);
		}
		
	}
	
	
	
	/**
	   * Get the external storage state
	   * @return
	   */
	private boolean getStorageState() {
		boolean bExternalStorageAvailable;
		boolean bExternalStorageWriteable;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		      // We can read and write the media
			bExternalStorageAvailable = bExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		      // We can only read the media
			bExternalStorageAvailable = true;
		    bExternalStorageWriteable = false;
		} else {	
		      // Something else is wrong. It may be one of many other states, but all we need
		      //  to know is we can neither read nor write
		    bExternalStorageAvailable = bExternalStorageWriteable = false;
		}
		return (bExternalStorageAvailable&&bExternalStorageWriteable);
	  }
	
	@Override
	public String GetDescription() throws BPException {
		return mHeader;
	}

	@Override
	public void BeginAcq() throws BPException {
		frequency=1000;
		channelNumber = 8;
		numberBits=12;
	}

	@Override
	public void BeginAcq(int freq, int chmask, int nbits) throws BPException {
		// TODO : confirm all comparing with the header
		frequency=freq;
		channelNumber = 8;
		numberBits=12;

	}

	@Override
	public void GetFrames(int nframes, Frame[] frames) throws BPException {
		// TODO : change in function of numbe of channels available
		try {
			for (int i=0;i<nframes;i++) {
				String line = mReader.readLine();
				String values[] = line.split("\\t");
				frames[i].seq=(byte) Integer.parseInt(values[0]);
				frames[i].dig_in =(Integer.parseInt(values[1])==1);
				for (int c=0;c<8;c++) {
					frames[i].an_in[c] = (short)Integer.parseInt(values[3+c]);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new BPException(BPErrorTypes.CONTACTING_DEVICE);
		} catch (NullPointerException e) { // when line is null
			throw new BPException(BPErrorTypes.CONTACTING_DEVICE);
		}

	}
	
	@Override
	public void SetDOut(boolean dout) throws BPException {
		// TODO Auto-generated method stub

	}

	@Override
	public void EndAcq() throws BPException {
		// TODO Auto-generated method stub

	}

	@Override
	public void Close() throws BPException {
		try {
			mReader.close();
		} catch(Exception e){
			throw new BPException(BPErrorTypes.PORT_COULD_NOT_BE_CLOSED);
		}
	}

	
	@Override
	public void setInterval(int interval) throws BPException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void setThreshold(int threshold) throws BPException {
		// TODO Auto-generated method stub
	}



	@Override
	public void getData() throws BPException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void TurnOn1() throws BPException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void TurnOff1() throws BPException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void TurnOn2() throws BPException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void TurnOff2() throws BPException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void setVibrationThreshold(int thresold) throws BPException {
		// TODO Auto-generated method stub
		
	}
}
