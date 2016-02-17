//
//  This copyrighted © code is written for and is part of the book
//  Smartphone-Based Real-Time Digital Signal Processing
//
package com.dsp.firfilter;

import java.util.concurrent.BlockingQueue;

public class Filter implements Runnable{
	
	private BlockingQueue<WaveFrame> input;
	private BlockingQueue<WaveFrame> output;
	private FIRFilter filter;
	private Thread filterThread;
	
	public Filter(BlockingQueue<WaveFrame> input, BlockingQueue<WaveFrame> output) {
		this.input = input;
		this.output = output;
		filter = new FIRFilter(Settings.blockSize);
        filterThread = new Thread(this);
        filterThread.start();
	}

	public void run() {
		try {
			loop:while(true) {
				WaveFrame currentFrame = null;
				currentFrame = input.take();
				if(currentFrame == Settings.STOP){
					filter.release();
					output.put(currentFrame);
					break loop;
				}
				
				long then = System.nanoTime();
				currentFrame.setFiltered(filter.process(currentFrame.getShorts()));
				currentFrame.setElapsed(System.nanoTime()-then);
				output.put(currentFrame);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}
}