//
//  This copyrighted © code is written for and is part of the book
//  Smartphone-Based Real-Time Digital Signal Processing
//
package com.dsp.firfilter;

public class FIRFilter {
	
	long pointer;
	
	public FIRFilter(int framesize){
		pointer = initialize(framesize);
	}
	
	public void release(){
		finish(pointer);
	}
	
	public short[] process(short[] in){
		return compute(pointer, in);
	}

	//JNI Method Calls	
	public static native short[] compute(long memoryPointer, short[] in);
	public static native long initialize(int fsize);
	public static native void finish(long memoryPointer);
    
    	
	static {
		System.loadLibrary("nativeFIRFilter");
	}
}