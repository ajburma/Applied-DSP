package com.dsp.signalstatistics;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ScrollView;
import android.view.View;
import android.widget.TextView;

public class GUI extends Activity implements Monitor {

	private TextView logBox;
	private ScrollView logScrollView;
	private List<Integer> lineSize;
	private static Monitor guiCallback;
	
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	//set the application layout
        setContentView(R.layout.recorder);
        
        //hook UI elements
        setupListeners();
        
        //setup the textview 
        lineSize = new ArrayList<Integer>();
        logBox = (TextView)findViewById(R.id.LogView);
        logScrollView = (ScrollView)findViewById(R.id.LogScrollView);
        
        enableButtons(false);
		guiCallback = this;
    }
    
    private void setupListeners() {
    	((Button)findViewById(R.id.buttonStart)).setOnClickListener(buttonClick);
    }

	private void enableButtons(boolean flag) {
		((Button)findViewById(R.id.buttonStart)).setEnabled(!flag);
	}
	
	private View.OnClickListener buttonClick = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.buttonStart: {
					buttonStart();
					break;
				}
			}
		}
	};
	
	private void buttonStart(){
		enableButtons(true);
		appendTextView("Beginning computation.");
		NativeStatistics.mean(GUI.getCallback());
		NativeStatistics.median(GUI.getCallback());
		NativeStatistics.stdDev(GUI.getCallback());
		NativeStatistics.cov(GUI.getCallback());
		enableButtons(false);
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	//otherwise, everything resets when the screen is changed.
	public void onConfigurationChanged(Configuration newConfig) {
		  super.onConfigurationChanged(newConfig);
	}
	
	private void appendTextView(String text){
		text = text + "\n";
		lineSize.add(Integer.valueOf(text.length()));
		logBox.append(text);
		while(lineSize.size() > 250) {
			logBox.getEditableText().delete(0, lineSize.get(0).intValue());
			lineSize.remove(0);
		}
		
		logScrollView.post(new Runnable() {
			public void run() {
				logScrollView.fullScroll(View.FOCUS_DOWN);
			}
		});
	}
	
	public void notify(final String message){
		runOnUiThread(
			new Runnable(){
				 public void run() {
					 appendTextView(message);
				 }
			}
		);
	}

	public static Monitor getCallback() {
		return guiCallback;
	}

	static {
		System.loadLibrary("nativeStatistics");
	}
}
