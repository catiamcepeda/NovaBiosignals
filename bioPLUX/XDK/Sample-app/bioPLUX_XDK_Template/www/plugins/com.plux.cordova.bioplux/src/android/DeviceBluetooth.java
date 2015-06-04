package com.plux.cordova.bioplux;

import java.io.IOException;

import com.plux.cordova.bioplux.BPErrorTypes;
import com.plux.cordova.bioplux.BPException;

//import android.util.Log;

public class DeviceBluetooth extends Device {

	private BluetoothConnection connection;

	protected static byte[] CRC8tab = { (byte) 0x00, (byte) 0x07, (byte) 0x0E,
			(byte) 0x09, (byte) 0x1C, (byte) 0x1B, (byte) 0x12, (byte) 0x15,
			(byte) 0x38, (byte) 0x3F, (byte) 0x36, (byte) 0x31, (byte) 0x24,
			(byte) 0x23, (byte) 0x2A, (byte) 0x2D, (byte) 0x70, (byte) 0x77,
			(byte) 0x7E, (byte) 0x79, (byte) 0x6C, (byte) 0x6B, (byte) 0x62,
			(byte) 0x65, (byte) 0x48, (byte) 0x4F, (byte) 0x46, (byte) 0x41,
			(byte) 0x54, (byte) 0x53, (byte) 0x5A, (byte) 0x5D, (byte) 0xE0,
			(byte) 0xE7, (byte) 0xEE, (byte) 0xE9, (byte) 0xFC, (byte) 0xFB,
			(byte) 0xF2, (byte) 0xF5, (byte) 0xD8, (byte) 0xDF, (byte) 0xD6,
			(byte) 0xD1, (byte) 0xC4, (byte) 0xC3, (byte) 0xCA, (byte) 0xCD,
			(byte) 0x90, (byte) 0x97, (byte) 0x9E, (byte) 0x99, (byte) 0x8C,
			(byte) 0x8B, (byte) 0x82, (byte) 0x85, (byte) 0xA8, (byte) 0xAF,
			(byte) 0xA6, (byte) 0xA1, (byte) 0xB4, (byte) 0xB3, (byte) 0xBA,
			(byte) 0xBD, (byte) 0xC7, (byte) 0xC0, (byte) 0xC9, (byte) 0xCE,
			(byte) 0xDB, (byte) 0xDC, (byte) 0xD5, (byte) 0xD2, (byte) 0xFF,
			(byte) 0xF8, (byte) 0xF1, (byte) 0xF6, (byte) 0xE3, (byte) 0xE4,
			(byte) 0xED, (byte) 0xEA, (byte) 0xB7, (byte) 0xB0, (byte) 0xB9,
			(byte) 0xBE, (byte) 0xAB, (byte) 0xAC, (byte) 0xA5, (byte) 0xA2,
			(byte) 0x8F, (byte) 0x88, (byte) 0x81, (byte) 0x86, (byte) 0x93,
			(byte) 0x94, (byte) 0x9D, (byte) 0x9A, (byte) 0x27, (byte) 0x20,
			(byte) 0x29, (byte) 0x2E, (byte) 0x3B, (byte) 0x3C, (byte) 0x35,
			(byte) 0x32, (byte) 0x1F, (byte) 0x18, (byte) 0x11, (byte) 0x16,
			(byte) 0x03, (byte) 0x04, (byte) 0x0D, (byte) 0x0A, (byte) 0x57,
			(byte) 0x50, (byte) 0x59, (byte) 0x5E, (byte) 0x4B, (byte) 0x4C,
			(byte) 0x45, (byte) 0x42, (byte) 0x6F, (byte) 0x68, (byte) 0x61,
			(byte) 0x66, (byte) 0x73, (byte) 0x74, (byte) 0x7D, (byte) 0x7A,
			(byte) 0x89, (byte) 0x8E, (byte) 0x87, (byte) 0x80, (byte) 0x95,
			(byte) 0x92, (byte) 0x9B, (byte) 0x9C, (byte) 0xB1, (byte) 0xB6,
			(byte) 0xBF, (byte) 0xB8, (byte) 0xAD, (byte) 0xAA, (byte) 0xA3,
			(byte) 0xA4, (byte) 0xF9, (byte) 0xFE, (byte) 0xF7, (byte) 0xF0,
			(byte) 0xE5, (byte) 0xE2, (byte) 0xEB, (byte) 0xEC, (byte) 0xC1,
			(byte) 0xC6, (byte) 0xCF, (byte) 0xC8, (byte) 0xDD, (byte) 0xDA,
			(byte) 0xD3, (byte) 0xD4, (byte) 0x69, (byte) 0x6E, (byte) 0x67,
			(byte) 0x60, (byte) 0x75, (byte) 0x72, (byte) 0x7B, (byte) 0x7C,
			(byte) 0x51, (byte) 0x56, (byte) 0x5F, (byte) 0x58, (byte) 0x4D,
			(byte) 0x4A, (byte) 0x43, (byte) 0x44, (byte) 0x19, (byte) 0x1E,
			(byte) 0x17, (byte) 0x10, (byte) 0x05, (byte) 0x02, (byte) 0x0B,
			(byte) 0x0C, (byte) 0x21, (byte) 0x26, (byte) 0x2F, (byte) 0x28,
			(byte) 0x3D, (byte) 0x3A, (byte) 0x33, (byte) 0x34, (byte) 0x4E,
			(byte) 0x49, (byte) 0x40, (byte) 0x47, (byte) 0x52, (byte) 0x55,
			(byte) 0x5C, (byte) 0x5B, (byte) 0x76, (byte) 0x71, (byte) 0x78,
			(byte) 0x7F, (byte) 0x6A, (byte) 0x6D, (byte) 0x64, (byte) 0x63,
			(byte) 0x3E, (byte) 0x39, (byte) 0x30, (byte) 0x37, (byte) 0x22,
			(byte) 0x25, (byte) 0x2C, (byte) 0x2B, (byte) 0x06, (byte) 0x01,
			(byte) 0x08, (byte) 0x0F, (byte) 0x1A, (byte) 0x1D, (byte) 0x14,
			(byte) 0x13, (byte) 0xAE, (byte) 0xA9, (byte) 0xA0, (byte) 0xA7,
			(byte) 0xB2, (byte) 0xB5, (byte) 0xBC, (byte) 0xBB, (byte) 0x96,
			(byte) 0x91, (byte) 0x98, (byte) 0x9F, (byte) 0x8A, (byte) 0x8D,
			(byte) 0x84, (byte) 0x83, (byte) 0xDE, (byte) 0xD9, (byte) 0xD0,
			(byte) 0xD7, (byte) 0xC2, (byte) 0xC5, (byte) 0xCC, (byte) 0xCB,
			(byte) 0xE6, (byte) 0xE1, (byte) 0xE8, (byte) 0xEF, (byte) 0xFA,
			(byte) 0xFD, (byte) 0xF4, (byte) 0xF3 };
	
	protected boolean isDefaultMode = false;
	int frame_count = 0;
	private boolean extMode = false;
	private byte seq, nbuf;
	private byte buffer[] = new byte[14];
	private short oldch6, oldch7;

	public DeviceBluetooth(String macAddress) throws BPException {
		connection = new BluetoothConnection();
		connection.connect(macAddress);
	}

	public void BeginAcq() throws BPException {
		/**
		 * extMode = false; try { connection.write("Z".getBytes());
		 * frequency=1000; channelNumber = 8; numberBits=12; //_bytesPerFrame =
		 * (int) (2+(Math.ceil(_numberBits*channelNumber/(float)8))); seq = 0;
		 * nbuf = 0; oldch6 = 0; oldch7 = 0; } catch (IOException e) { throw new
		 * BPException(BPErrorTypes.CONTACTING_DEVICE); }
		 **/
		BeginAcq(1000, 0xFF, 12);
	}

	public void BeginAcq(int frequency, int channelMask, int numberBits)
			throws BPException {
		extMode = true;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();

		String frequencyString = "0000" + frequency;
		String channelString = "00"
				+ Integer.toHexString(channelMask).toUpperCase();
		String numberBitsString = "00" + numberBits;

		System.out.println("BEGIN ACQ: " + frequencyString + ". " + channelString + ". "
				+ numberBitsString);

		channelNumber = 0;
		for (int i = 0; i < 8; i++) {
			if ((channelMask & 0x01) == 1) {
				sb2.append(i + 1);
				sb2.append(" ");
				channelNumber++;
			}
			channelMask = (channelMask >> 1);
		}

		sb.append("@START,");
		sb.append(frequencyString.substring(frequencyString.length() - 4,
				frequencyString.length()));
		sb.append(",");
		sb.append(channelString.substring(channelString.length() - 2,
				channelString.length()));
		sb.append(",");
		sb.append(numberBitsString.substring(numberBitsString.length() - 2,
				numberBitsString.length()));
		sb.append(";");

		this.frequency = frequency;
		this.numberBits = (byte) numberBits;
		// _bytesPerFrame = (int)
		// (2+(Math.ceil(_numberBits*channelNumber/(float)8)));

		try {
			connection.write(sb.toString().getBytes());
			System.out.println("Sending start command");
		} catch (IOException e) {
			throw new BPException(BPErrorTypes.CONTACTING_DEVICE);
		}
		seq = 0;
		nbuf = 0;
		oldch6 = 0;
		oldch7 = 0;
	}

	public void EndAcq() throws BPException {
		try {
			connection.write("R".getBytes());
		} catch (IOException e) {
			throw new BPException(BPErrorTypes.CONTACTING_DEVICE);
		}
	}

	public String GetDescription() throws BPException {
		try {
			connection.write("V".getBytes());
			try {
				Thread.sleep(500);
			}
			catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			byte[] aux = connection.read();
			description = aux == null ? "" : new String(aux);
		} catch (IOException e) {
			throw new BPException(BPErrorTypes.CONTACTING_DEVICE);
		}
		return description;
	}

	public void GetFrames(int nframes, Frame[] frames) throws BPException {
		int i = 0;
		boolean flag = true;
		byte nch = 0, siz = 0;
		int nread;
		for (i = 0; i < nframes; i++) {
			// nch=0, siz=0;

			while (flag) {
				if (nbuf > 0) {
					seq = (byte) (buffer[0] & 0x7F);
				}

				nch = channelNumber;
				if (!extMode && seq % 8 != 0) {
					nch = 6;
				}
				// nch = (seq%8 == 0) ? 8 : 6;
				siz = (byte) (1 + nch + 1);
				if (numberBits == 12) {
					siz += (nch + 1) / 2;
				} // if nch is odd, round up

				if (siz > nbuf) {
					try {
						nread = connection.read((byte[]) (buffer), (int) nbuf,
								siz - nbuf);
					} catch (Exception e) {
						// throw new PluxContactingDeviceError();
						throw new BPException(BPErrorTypes.CONTACTING_DEVICE);
					}
					if (nread == -1) {
						int a = 0;
					}
					if (nread == 0) {
						// throw new PluxContactingDeviceError();
						throw new BPException(BPErrorTypes.CONTACTING_DEVICE);
					}

					if (nread != siz - nbuf) {
						nbuf = (byte) (nbuf + nread);
						continue;
					}
					nbuf = siz;
				}

				// if seq is different than expected, reevaluate nch and siz
				// only possible if nbuf is initially == 0
				if (seq != (buffer[0] & 0x7F)) {
					continue;
				}

				if ((buffer[siz - 1] & 0xFF) == (getCrc8(buffer, siz - 1) & 0xFF)) {
					break;
				}
				// an error has occured, synchronize with the next valid frame

				nbuf--;

				// if (nbuf > 0) memmove(buffer, buffer+1, nbuf);
				if (nbuf > 0) {
					for (int j = 0; j < nbuf; j++) {
						buffer[j] = buffer[j + 1];
					}
				}
			}

			frames[i].seq = seq;
			frames[i].dig_in = ((buffer[0] & 0x80) != 0);

			// BYTE *pos = buffer+1;
			int pos = 1;
			if (numberBits == 12) {
				for (byte ch = 0; ch < nch; ch += 2) {
					frames[i].an_in[ch] = (short) ((buffer[pos] & 0x00FF) | (short) ((buffer[pos + 1] & 0x0F) << 8));
					if (ch + 1 < nch) {
						frames[i].an_in[ch + 1] = (short) ((((buffer[pos + 1] & 0xF0) >> 4) & 0x000F) | ((short) (buffer[pos + 2] & 0x00FF)) << 4);
					}
					pos += 3;
				}
			} else { // nbits == 8
				for (byte ch = 0; ch < nch; ch++) {
					frames[i].an_in[ch] = buffer[pos];
					pos++;
				}
			}
			if (!extMode) {
				if (seq % 8 == 0) {
					oldch6 = frames[i].an_in[6];
					oldch7 = frames[i].an_in[7];
				} else {
					frames[i].an_in[6] = oldch6;
					frames[i].an_in[7] = oldch7;
				}
			}
			seq++;
			if (seq == 0x80) {
				seq = 0;
			}

			nbuf = (byte) (nbuf - siz);
			// if (nbuf > 0) memmove(buffer, buffer+siz, nbuf);
			if (nbuf > 0) {
				for (int j = 0; j < nbuf; j++) {
					buffer[j] = buffer[j + siz];
				}
			}
		}
		// return i;

	}

	public void SetDOut(boolean digitalOut) throws BPException {

		try {
			if (digitalOut)
				connection.write(">".getBytes());
			else
				connection.write("<".getBytes());
		} catch (IOException e) {
			throw new BPException(BPErrorTypes.CONTACTING_DEVICE);
		}
	}

	public void TurnOn1() throws BPException {

		try {
			connection.write(">".getBytes());
		} catch (IOException e) {
			throw new BPException(BPErrorTypes.CONTACTING_DEVICE);
		}
	}

	public void TurnOff1() throws BPException {

		try {
			connection.write("<".getBytes());
		} catch (IOException e) {
			throw new BPException(BPErrorTypes.CONTACTING_DEVICE);
		}
	}

	public void TurnOn2() throws BPException {

		try {
			connection.write("B".getBytes());
		} catch (IOException e) {
			throw new BPException(BPErrorTypes.CONTACTING_DEVICE);
		}
	}

	public void TurnOff2() throws BPException {

		try {
			connection.write("A".getBytes());
		} catch (IOException e) {
			throw new BPException(BPErrorTypes.CONTACTING_DEVICE);
		}
	}

	
	@Override
	public void setInterval(int interval) throws BPException {
		
		String int_str = ("000" + interval);
		System.out.println("\n\n" + int_str);
		String i = int_str.substring(int_str.length() - 2,int_str.length());
		System.out.println("Set interval: " + "@SETINTER" + i + ";");
		
		
		try {
			connection.write( ("@SETINTER"+i+";").getBytes() );
		} catch (IOException e) {
			throw new BPException(BPErrorTypes.CONTACTING_DEVICE);
		}
	}

	@Override
	public void setThreshold(int threshold) throws BPException {
		
		String thr_str = ("00000" + threshold);
		System.out.println("\n\n " + thr_str);
		String t = thr_str.substring(thr_str.length() - 3, thr_str.length());
		System.out.println("Set threshold: " + "@THR" + t + ";");
		
		/*try {
			connection.write( ("@THR" + t + ";").getBytes());
		} catch (IOException e) {
			throw new BPException(BPErrorTypes.CONTACTING_DEVICE);
		}*/
	}

	@Override
	public void getData() throws BPException {
		try {
			connection.write("@GETDATA".getBytes());
		} catch (IOException e) {
			throw new BPException(BPErrorTypes.CONTACTING_DEVICE);
		}
	}

	
	private byte getCrc8(byte[] data, int len) {
		byte crc = 0;

		for (int i = 0; i < len; i++)
			crc = CRC8tab[(crc ^ data[i]) & 0xFF];

		return crc;
	}

	protected final class ByteRingBuffer {

		private byte[] _buffer;

		private int _startIndex, _finishIndex;

		public ByteRingBuffer(int bufferSize) {
			_buffer = new byte[bufferSize];
			_startIndex = _finishIndex = 0;
		}

		// !subscriber.freq
		public void put(byte[] bytesToPut) {
			if (bytesToPut.length < (_buffer.length - _finishIndex)) {
				System.arraycopy(bytesToPut, 0, _buffer, _finishIndex,
						bytesToPut.length);
				_finishIndex += bytesToPut.length;
			} else {
				int _aux = (_buffer.length - _finishIndex);
				System.arraycopy(bytesToPut, 0, _buffer, _finishIndex, _aux);
				_finishIndex = 0;
				System.arraycopy(bytesToPut, _aux, _buffer, _finishIndex,
						bytesToPut.length - _aux);
				_finishIndex += bytesToPut.length - _aux;
			}
		}

		public byte[] take(int bytes) {
			byte[] returnBuffer = new byte[bytes];

			if (bytes <= (_buffer.length - _startIndex)) {
				System.arraycopy(_buffer, _startIndex, returnBuffer, 0, bytes);
				_startIndex += bytes;
			} else {
				int aux = (_buffer.length - _startIndex);
				System.arraycopy(_buffer, _startIndex, returnBuffer, 0, aux);
				_startIndex = 0;
				System.arraycopy(_buffer, _startIndex, returnBuffer, aux, bytes
						- aux);
				_startIndex += bytes - aux;
			}

			return returnBuffer;

		}

		public int length() {
			if (_finishIndex < _startIndex)
				return (_buffer.length - _startIndex) + _finishIndex;
			else
				return _finishIndex - _startIndex;
		}

	}

	@Override
	public void Close() throws BPException {
		try {
			connection.disconnect();
		} catch (Exception e) {
			throw new BPException(BPErrorTypes.PORT_COULD_NOT_BE_CLOSED);
		}

	}

	@Override
	public void finalize() {
		try {
			Close();
		} catch (BPException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}
	
	@Override
	public void setVibrationThreshold(int threshold) throws BPException {
		
		try {
			connection.write( ("@SETTHRES," + Integer.toString(threshold) + ";").getBytes());
		} catch (IOException e) {
			throw new BPException(BPErrorTypes.CONTACTING_DEVICE);
		}
	}
}
