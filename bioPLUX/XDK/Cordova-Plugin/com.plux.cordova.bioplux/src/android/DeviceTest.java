package com.plux.cordova.bioplux;

import java.io.IOException;

import android.os.SystemClock;
import android.util.Log;


public class DeviceTest extends Device {
	
	// Specific teste
	protected double period;
	protected long tick0;
	protected long nsamples=0;
	protected byte chans[] = new byte[8];
	protected boolean lastdout;
	
	@Override
	public void BeginAcq() throws BPException {
		System.out.println("TEST: fs = 40");
		BeginAcq(40, 0xFF,12);
		extMode = false;
	}

	@Override
	public void BeginAcq(int freq, int chmask, int nbits) throws BPException {
		System.out.println("TEST: fs = " + freq);
		extMode = true;
		channelNumber=0;
		tick0 = SystemClock.uptimeMillis();
		for (int i = 0; i < 8; i++) {
			if((chmask & 0x01)==1){
				chans[channelNumber++] = (byte) i;
			}
			chmask=(chmask>>1);
		}
		frequency=freq;
		period = 1000./(double)(freq);
		
		numberBits=(byte)nbits;
	
		seq = 0;
        nbuf = 0;
        oldch6 = 0;
        oldch7 = 0;
	}

	@Override
	public void Close() throws BPException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void EndAcq() throws BPException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String GetDescription() throws BPException {
		return "test device";
	}

	@Override
	public void GetFrames(int nframes, Frame[] frames) throws BPException {
		
		for(int i=0; i < nframes; i++) {
			frames[i].seq = seq;
			frames[i].dig_in = lastdout;
			
			short t100  = (short)(nsamples % 100);
			short t1000 = (short)(nsamples % 1000);
			
			byte nch = channelNumber;
			if (!extMode && seq%8 != 0)   nch = 6;

			for(int j=0; j < nch; j++) {
				short val=0;
				switch (chans[j]) {
				case 0:
					val = (short) (0x800 + (short)(0x7FF * Math.sin(2*3.1415927*t100/100.0)));
					break;
				case 1:
					val = (short) (t100 * 0xFFF / 99.0);
					break;
				case 2:
					val = (short) (0xFFF - t100 * 0xFFF / 99.0);
					break;
				case 3:
					val = (short) (t100 < 50 ? 0xFFF : 0);
					break;
				case 4:
					val = ((t100 & 0x01)>0) ? (short)0xFFF : 0;
					break;
				case 5:
					val = (short)( Math.random() * 0xFFF );
					break;
				case 6:
					val = (short) (0x800 + (short)(0x7FF * Math.sin(2*3.1415927*t1000/1000)));
					break;
				case 7:
					val = (short) (t1000 * 0xFFF / 999);
					break;
				}
				if (numberBits == 8)   val >>= 4;
				frames[i].an_in[j] = val;
			}
			
			if (!extMode) {
				if (seq % 8 == 0) {
					oldch6 = frames[i].an_in[6];
					oldch7 = frames[i].an_in[7];
				}
				else {
					frames[i].an_in[6] = oldch6;
					frames[i].an_in[7] = oldch7;
				}
			}
			seq++;
			nsamples++;
			if (nsamples==Long.MAX_VALUE) {
				nsamples=0;
				tick0 = SystemClock.uptimeMillis();
			}
			if (seq == (byte)0x80) seq = 0;
			//Log.i("Test2",Byte.toString(seq)+ " "+ frames[i].an_in[0]);
		}
		// add delay to respect frequency
		long dt = tick0 + (long) (nsamples*period) - SystemClock.uptimeMillis();
		if (dt > 0)
			try {
				Thread.sleep(dt);
			} catch (InterruptedException e) {
				// Thread Interrupted : we quit...
			}
	}

	@Override
	public void SetDOut(boolean dout) throws BPException {
		lastdout = dout;
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
