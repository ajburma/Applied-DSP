//
//  This copyrighted © code is written for and is part of the book
//  Smartphone-Based Real-Time Digital Signal Processing
//
package com.dsp.newtonraphson;

public class Settings {
	
	public static int iterations = 3;
	public static boolean changed = false;
	public static float numeratorFloat = 2.71f;
	public static float denominatorFloat = 3.14f;
	public static short numeratorShort = 6000;
	public static short denominatorShort = 8129;
	private static Monitor main;
	
	public static void setCallbackInterface(Monitor uiInterface) {
		main = uiInterface;
	}
	
	public static Monitor getCallbackInterface() {
		return main;
	}
	
	public static boolean setNumerator(float newValue){
		if(numeratorFloat != newValue){
			numeratorFloat = newValue;
			changed = true;
			return true;
		}
		return false;
	}
	
	public static boolean setDenominator(float newValue){
		if(denominatorFloat != newValue){
			denominatorFloat = newValue;
			changed = true;
			return true;
		}
		return false;
	}
	
	public static boolean setNumeratorShort(short newValue){
		if(numeratorShort != newValue){
			numeratorShort = newValue;
			changed = true;
			return true;
		}
		return false;
	}
	
	public static boolean setDenominatorShort(short newValue){
		if(denominatorShort != newValue){
			denominatorShort = newValue;
			changed = true;
			return true;
		}
		return false;
	}
	
	public static boolean setIterations(int newValue){
		if(iterations != newValue){
			iterations = newValue;
			changed = true;
			return true;
		}
		return false;
	}
}
