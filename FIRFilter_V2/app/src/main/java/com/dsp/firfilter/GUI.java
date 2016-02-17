//
//  This copyrighted © code is written for and is part of the book
//  Smartphone-Based Real-Time Digital Signal Processing
//
package com.dsp.firfilter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;

public class GUI extends Activity implements Monitor{
	
	private SharedPreferences preferences;
	private WaveRecorder waveRecorder;
	private WaveReader waveReader;
	private TextView logBox;
	private List<Integer> lineSize;
	
	private String[] fileList;
	private String dialogFile;	
	
	private int mode;
	private AtomicBoolean processing;
	
	private BlockingQueue<WaveFrame> inputFrames;
	private BlockingQueue<WaveFrame> processedFrames;
	
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	//set the application layout
        setContentView(R.layout.gui);
        
        Settings.setCallbackInterface(this);
        
        //hook application preferences interface
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        updateSettings();
        
        //hook UI elements
        setupListeners();
        
        //setup the textview 
        lineSize = new ArrayList<Integer>();
        logBox = (TextView)findViewById(R.id.LogView);
        
        //create directory to save data into
        Utilities.prepareDirectory(getString(R.string.appDirectory));
        
        //Check for supported sampling rates
    	new Thread(WaveRecorder.checkSamplingRate).start();
        
        //prepare to process data
        processing = new AtomicBoolean();
        
        //create queues for data to be stored in during processing
        inputFrames = new ArrayBlockingQueue<WaveFrame>(1);
        processedFrames = new ArrayBlockingQueue<WaveFrame>(1);
        
        enableButtons(false);
        
        appendTextView("Sampling: " + Settings.Fs + "Hz | Frame: "+ Settings.blockSize + " samples\n");
        appendTextView("Playback: " + Settings.getOutput() + " | Debug: " + Settings.getDebug() + "\n");
    }
    
    private void setupListeners() {
    	((Button)findViewById(R.id.buttonSettings)).setOnClickListener(buttonClick);
    	((Button)findViewById(R.id.buttonStart)).setOnClickListener(buttonClick);
    	((Button)findViewById(R.id.buttonStop)).setOnClickListener(buttonClick);
    	((Button)findViewById(R.id.buttonPCM)).setOnClickListener(buttonClick);
    }

	private void enableButtons(boolean flag) {
		((Button)findViewById(R.id.buttonSettings)).setEnabled(!flag);
		((Button)findViewById(R.id.buttonStart)).setEnabled(!flag);
		((Button)findViewById(R.id.buttonStop)).setEnabled(flag);
		((Button)findViewById(R.id.buttonPCM)).setEnabled(!flag);
	}
    
	private View.OnClickListener buttonClick = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.buttonStart: {
					buttonStart();
					break;
				}
				case R.id.buttonStop: {
					buttonStop();
					break;
				}
				case R.id.buttonPCM: {
					buttonPCM();
					break;
				}
				case R.id.buttonSettings: {
					buttonSettings();
					break;
				}
			}
		}
	};
	
	private void buttonStart(){
		
		//setup data processing
		inputFrames.clear();
    	processedFrames.clear();
    	new Filter(inputFrames, processedFrames);
		
		if(dialogFile == null){
	        //setup audio recording component
	    	waveRecorder = new WaveRecorder(inputFrames);
	    	mode = 2;
	    	appendTextView("Recording and Filtering.\n");
			new WaveSaver(getString(R.string.appDirectory) + File.separator + System.currentTimeMillis(), processedFrames);
		} else {
			mode = 1;
			appendTextView("Reading from file: " + dialogFile + "\n");
			waveReader = new WaveReader("Filtering" + File.separator + dialogFile, inputFrames);
			new WaveSaver((getString(R.string.appDirectory) + File.separator + dialogFile.replace(".wav", "")).replace(".pcm", ""), processedFrames);
		}
		processing.set(true);
		enableButtons(true);
	}
	
	private void buttonStop(){
		switch(mode) {
			case 1: {
				waveReader.stop();
				waveReader = null;
				break;
			}
			case 2: {
				waveRecorder.stopRecording();
				waveRecorder = null;
				break;
			}
			default: {
				break;
			}
		}
	}
	
	private void buttonPCM(){
		getDirectoryContents();
		createFilePrompt().show();
	}
	
	private void buttonSettings(){
		Intent intent = new Intent(this, PreferencesUI.class);
		startActivityForResult(intent, 42);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case 42:
			if(Settings.changed){
				appendTextView("Sampling: " + Settings.Fs + "Hz | Frame: "+ Settings.blockSize + " samples\n");
		        appendTextView("Playback: " + Settings.getOutput() + " | Debug: " + Settings.getDebug() + "\n");
		        Settings.changed = false;
			}
			break;
		}
	}

	private void updateSettings(){
		Settings.setPlayback(preferences.getBoolean(getString(R.string.prefPlayback), false));
		Settings.setOutput(Integer.parseInt(preferences.getString(getString(R.string.prefOutputStream), "2")));
		Settings.setSamplingFrequency(Integer.parseInt(preferences.getString(getString(R.string.prefSamplingFreq), "8000")));
		Settings.setBlockSize(Integer.parseInt(preferences.getString(getString(R.string.prefBlockSize), "256")));
		Settings.setDebugLevel(Integer.parseInt(preferences.getString(getString(R.string.prefDebug), "4")));
	}
	
	//otherwise, everything resets when the screen is changed.
	public void onConfigurationChanged(Configuration newConfig) {
		  super.onConfigurationChanged(newConfig);
	}
	
	//File selection dialog methods.
	private void getDirectoryContents() {
	    File directory = Utilities.prepareDirectory(getString(R.string.appDirectory));
	    if(directory.exists()) {
	        FilenameFilter filter = new FilenameFilter() {
	            public boolean accept(File dir, String filename) {
	                return filename.contains(".pcm") || filename.contains(".wav");
	            }
	        };
	        fileList = directory.list(filter);
	    } else {
	        fileList = new String[0];
	    }
	}
	
	private Dialog createFilePrompt() {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose file");
        builder.setItems(fileList, 
	        new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                dialogFile = fileList[which];
	                buttonStart();
	            }
	        }
        );
	    return builder.create();
	}
	
	private void appendTextView(String text){
		lineSize.add(Integer.valueOf(text.length()));
		logBox.append(text);
		while(logBox.getLineCount() > 28) {
			logBox.getEditableText().delete(0, lineSize.get(0).intValue());
			lineSize.remove(0);
		}
	}
	
	public int getMode(){
		return mode;
	}
	
	public void done(){
		dialogFile = null;
		if(processing.getAndSet(false)){
			runOnUiThread(
				new Runnable(){
					 public void run() {
						 
						 if(mode == 1) {
							 appendTextView("File read completed.\n");
						 } else if (mode == 2) {
							 appendTextView("Recording finished.\n");
						 }
						 enableButtons(false);
					 }
				}
			);
		}
	}
	
	public synchronized void notify(final String message){
		runOnUiThread(
			new Runnable(){
				 public void run() {
					 appendTextView(message + "\n");
				 }
			}
		);
	}
}
