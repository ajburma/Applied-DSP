//
//  This copyrighted © code is written for and is part of the book
//  Smartphone-Based Real-Time Digital Signal Processing
//
package com.dsp.firfilter;

import android.media.AudioFormat;
import android.media.MediaRecorder;

public class Settings {
	
	public static final WaveFrame STOP = new WaveFrame(new float[1], -32768);
	private static Monitor main;

	//gui settings
	public static final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;
	public static final int SOURCE = MediaRecorder.AudioSource.DEFAULT;
	public static int Fs = 8000;
	public static int blockSize = 256;
	public static boolean playback = false;
	public static boolean output = false;
	public static boolean changed = false;
	public static int debugLevel = 4;
	public static int secondConstant = Fs/blockSize;
	
	public static CharSequence[] samplingRates = {"8000 Hz"};
	public static CharSequence[] samplingRateValues = {"8000"};
	
	public static void setCallbackInterface(Monitor uiInterface) {
		main = uiInterface;
	}
	
	public static Monitor getCallbackInterface() {
		return main;
	}
	
	public static int setBlockSize(int size){
		if((size < 32) || (size > 2048)){
			return -1;
		}
		size = Utilities.nextPower2(size);
		if(blockSize != size){
			blockSize = size;
			secondConstant = Fs/size;
			changed = true;
			return 1;
		}
		return 0;
	}
	
	public static boolean setSamplingFrequency(int freq){
		if(Fs != freq){
			Fs = freq;
			secondConstant = Fs/blockSize;
			changed = true;
			return true;
		}
		return false;
	}
	
	public static boolean setPlayback(boolean flag){
		if(playback != flag){
			playback = flag;
			changed = true;
			return true;
		}
		return false;
	}
	
	public static boolean setOutput(int stream){
		if((stream == 1) && (output != true)) { //original signal
			output = true;
			changed = playback;
			return playback;
		} else if((stream == 2) && (output != false)) { //filtered signal
			output = false;
			changed = playback;
			return playback;
		}
		return false;
	}
	
	public static String getOutput(){
		if(playback == false){
			return "None";
		} else if (output == true){
			return "Original";
		} else {
			return "Filtered";
		}
	}
	
	public static String getDebug(){
		if(debugLevel == 4){
			return "None";
		} else if (debugLevel == 3){
			return "PCM";
		} else if(debugLevel == 2){
			return "Text";
		} else if(debugLevel == 1){
			return("All");
		}
		return "How could you let this happen?";
	}
	
	public static boolean setDebugLevel(int level){
		if(debugLevel != level){
			debugLevel = level;
			changed = true;
			return true;
		}
		return false;
	}
	
	public static void setRates(CharSequence[] rates){
		samplingRates = rates;
	}
	
	public static void setRateValues(CharSequence[] rateValues){
		samplingRateValues = rateValues;
	}
}
