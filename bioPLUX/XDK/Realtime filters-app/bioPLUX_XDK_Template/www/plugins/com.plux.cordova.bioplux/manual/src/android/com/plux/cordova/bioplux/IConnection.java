package org.apache.cordova.bioplux;

import java.io.IOException;

/**
 *
 * IConnection.java
 * Purpose: Be an interface to bluetooth connections.
 *
 * @author Plux
 * @version ?
 */

public interface IConnection {
	void connect (String address) throws Exception;
	void write(byte[] message) throws IOException;
	byte[] read();
	public int read(byte[] buffer,int offset,int toread) throws IOException;
	int getState();
	void disconnect() throws IOException;
}