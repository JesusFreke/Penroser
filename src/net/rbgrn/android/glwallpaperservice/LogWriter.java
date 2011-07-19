package net.rbgrn.android.glwallpaperservice;


import android.util.Log;

import java.io.Writer;

class LogWriter extends Writer {
	private StringBuilder mBuilder = new StringBuilder();

	@Override
	public void close() {
		flushBuilder();
	}

	@Override
	public void flush() {
		flushBuilder();
	}

	@Override
	public void write(char[] buf, int offset, int count) {
		for (int i = 0; i < count; i++) {
			char c = buf[offset + i];
			if (c == '\n') {
				flushBuilder();
			} else {
				mBuilder.append(c);
			}
		}
	}

	private void flushBuilder() {
		if (mBuilder.length() > 0) {
			Log.v("GLSurfaceView", mBuilder.toString());
			mBuilder.delete(0, mBuilder.length());
		}
	}
}
