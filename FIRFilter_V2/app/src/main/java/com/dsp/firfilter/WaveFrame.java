//
//  This copyrighted © code is written for and is part of the book
//  Smartphone-Based Real-Time Digital Signal Processing
//
package com.dsp.firfilter;

import java.util.Arrays;

public class WaveFrame {
	private float[] fsamples;
	private short[] ssamples, filtered;
	private long frameNum;
	private long elapsed;
	
	public WaveFrame(float[] fsamples, long frameNum){
		this.frameNum = frameNum;
		this.ssamples = null;
		this.fsamples = Arrays.copyOf(fsamples, fsamples.length);
	}
	
	public WaveFrame(short[] ssamples, long frameNum){
		this.frameNum = frameNum;
		this.fsamples = null;
		this.ssamples = Arrays.copyOf(ssamples, ssamples.length);
	}
	
	public float[] getFloats(){
		if(fsamples == null){
			fsamples = new float[Settings.blockSize];
			for(int i=0;i<Settings.blockSize;i++) {
				fsamples[i] = (float)ssamples[i]/32768.0f;
			}
		}
		return fsamples;
	}
	
	public short[] getShorts(){ 
		if(ssamples == null){
			ssamples = new short[Settings.blockSize];
			for(int i=0;i<Settings.blockSize;i++) {
				ssamples[i] = (short)(fsamples[i]*32767);
			}
		}
		return ssamples;
	}
	
	public long getFrameNumber(){
		return frameNum;
	}
	
	public void setFiltered(short[] filtered){
		this.filtered = filtered;
	}
	
	public float[] getFloatFiltered(){
		if(fsamples == null) fsamples = new float[Settings.blockSize];
		for(int i=0;i<Settings.blockSize;i++) {
			fsamples[i] = (filtered[i]/32768.0f);
		}
		return fsamples;
	}
	
	public short[] getFiltered(){
		return filtered;
	}
	
	public void setElapsed(long time){
		elapsed = time;
	}
	
	public long getElapsed(){
		return elapsed;
	}
	
	public void clearSampleData(){
		fsamples = null;
		ssamples = null;
		filtered = null;
	}
}
