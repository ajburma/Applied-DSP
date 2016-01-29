//
//  This copyrighted © code is written for and is part of the book
//  Smartphone-Based Real-Time Digital Signal Processing
//
package com.dsp.realtime;

public class Filters {

	static {
		System.loadLibrary("realTimeNative");
	}

	//JNI Method Calls	
	public static native float[] compute(short[] in);
	public static native void initialize(int fsize, int delay);
	public static native void finish();
}
