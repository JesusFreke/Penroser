package net.rbgrn.android.glwallpaperservice;

import javax.microedition.khronos.opengles.GL;

interface GLWrapper {
	/**
	 * Wraps a gl interface in another gl interface.
	 *
	 * @param gl
	 * a GL interface that is to be wrapped.
	 * @return either the input argument or another GL object that wraps the input argument.
	 */
	GL wrap(GL gl);
}
