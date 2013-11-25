package org.klomp.snark;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ThrottledOutputStream extends BufferedOutputStream {
	final OutputStream out;
	long maxBytesPerSecond = Long.MAX_VALUE;
	
	// This is a per second counter
	int bytesSentSoFar = 0;
	
	/**
	 * Constructor for a throttled output stream
	 * @param out
	 */
	public ThrottledOutputStream(OutputStream out) {
		super(out);
		this.out = out;
	}

	/**
	 * Constructor for a throttled output stream
	 * @param out
	 * @param throttle Throttle limit in kb/s
	 */
	public ThrottledOutputStream(OutputStream out, int throttle) {
		this(out);
		this.setThrottle(throttle);
	}
	
	/**
	 * Adjust the throttling
	 * @param newThrottle new throttle bandwidth, in kb/s
	 */
	public void setThrottle(int newThrottle) {
		this.maxBytesPerSecond = (newThrottle * 1024);
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
		bytesSentSoFar++;
		throttle();
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (len + bytesSentSoFar > maxBytesPerSecond) {
			// TODO - @eric - not sure if this is always guaranteed to be an int...
			int bytesToSend = (int) (maxBytesPerSecond - bytesSentSoFar);
			out.write(b, off, bytesToSend);
			bytesSentSoFar += bytesToSend;
			throttle();
			
			// Finish writing the remainder using recursion 
			write(b, off + bytesToSend, len - bytesToSend);
		} else {
			out.write(b, off, len);
			bytesSentSoFar += len;
			throttle();
		}
	}
	
	/**
	 * Slow down the send speed of this connection by sleeping for a second, 
	 * if we've sent all the data we want to send in this second
	 */
	private void throttle() {
		if (bytesSentSoFar >= maxBytesPerSecond) {
			try {
				// TODO Timer?
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			bytesSentSoFar = 0;
		}
	}
}
