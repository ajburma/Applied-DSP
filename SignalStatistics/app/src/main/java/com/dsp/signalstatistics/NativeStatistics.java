package com.dsp.signalstatistics;

public class NativeStatistics {

	public static native void mean(Monitor guiCallback);
	public static native void median(Monitor guiCallback);
	public static native void stdDev(Monitor guiCallback);
	public static native void cov(Monitor guiCallback);
	
}