//
//  This copyrighted © code is written for and is part of the book
//  Smartphone-Based Real-Time Digital Signal Processing
//
package com.dsp.firfilter;

public interface Monitor {
	public int getMode();
	public void done();
	public void notify(String message);
}
