//
//  This copyrighted © code is written for and is part of the book
//  Smartphone-Based Real-Time Digital Signal Processing
//
package com.dsp.newtonraphson;

public class NewtonRaphson {

	static {
		System.loadLibrary("nativeNewtonRaphson");
	}
	
	public static void processFloat(){
		computeFloat(Settings.getCallbackInterface(), Settings.numeratorFloat, Settings.denominatorFloat, Settings.iterations);
	}
	
	public static void processShort(){
		computeFixed(Settings.getCallbackInterface(), Settings.numeratorShort, Settings.denominatorShort, Settings.iterations);
	}
	public static void processLong(){
		computeFixedLong(Settings.getCallbackInterface(), Settings.numeratorShort, Settings.denominatorShort, Settings.iterations);
	}
	public static void processSQRT(){
		computeSQRT(Settings.getCallbackInterface(), Settings.numeratorFloat, Settings.denominatorFloat, Settings.iterations);
	}

	//JNI Method Calls	
	private static native void computeFloat(Monitor main, float numerator, float denominator, int iterations);
	private static native void computeFixed(Monitor main, short numerator, short denominator, int iterations);
	private static native void computeSQRT(Monitor main, float numerator, float denominator, int iterations);
	private static native void computeFixedLong(Monitor main, short numerator, short denominator, int iterations);
	
}
