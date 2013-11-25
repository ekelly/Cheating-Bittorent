package org.klomp.snark;

import java.io.DataOutputStream;

public class ThrottledDataOutputStream extends DataOutputStream {
	protected final ThrottledOutputStream out;

	public ThrottledDataOutputStream(ThrottledOutputStream out) {
		super(out);
		this.out = out;
	}

	/**
	 * Set the throttle for this output stream in kb/s
	 * @param throttle
	 */
	public void setThrottle(int throttle) {
		this.out.setThrottle(throttle);
	}
}
