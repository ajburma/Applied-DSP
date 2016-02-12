//
//  This copyrighted © code is written for and is part of the book
//  Smartphone-Based Real-Time Digital Signal Processing
//
package com.dsp.newtonraphson;

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

public class GUI extends Activity implements Monitor{
	
	private SharedPreferences preferences;
	private TextView logBox;
	private ScrollView logScrollView;
	private List<Integer> lineSize;
	
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	//set the application layout
        setContentView(R.layout.gui);
        Settings.setCallbackInterface(this);
        
        //hook application preferences interface
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        //hook UI elements
        setupListeners();
        
        //setup the textview 
        lineSize = new ArrayList<Integer>();
        logBox = (TextView)findViewById(R.id.LogView);
        logScrollView = (ScrollView)findViewById(R.id.LogScrollView);
        
        //get saved application settings
        updateSettings();
        settingSummary();
        Settings.changed = false;
        
        enableButtons(false);
    }
    
    private void setupListeners() {
    	((Button)findViewById(R.id.buttonSettings)).setOnClickListener(buttonClick);
    	((Button)findViewById(R.id.buttonStart)).setOnClickListener(buttonClick);
    }

	private void enableButtons(boolean flag) {
		((Button)findViewById(R.id.buttonSettings)).setEnabled(!flag);
		((Button)findViewById(R.id.buttonStart)).setEnabled(!flag);
	}
	
	private View.OnClickListener buttonClick = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.buttonStart: {
					buttonStart();
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
		enableButtons(true);
		appendTextView("Beginning computation.");
		NewtonRaphson.processFloat();
		NewtonRaphson.processShort();
		NewtonRaphson.processLong();
		NewtonRaphson.processSQRT();
		enableButtons(false);
		
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
				settingSummary();
				Settings.changed = false;
			}
			break;
		}
	}

	private void updateSettings(){
		Settings.setNumerator(Float.parseFloat(preferences.getString(getString(R.string.prefNumerator), "2.71")));
		Settings.setDenominator(Float.parseFloat(preferences.getString(getString(R.string.prefDenominator), "3.14")));
		Settings.setIterations(Integer.parseInt(preferences.getString(getString(R.string.prefIterations), "3")));
		Settings.setNumeratorShort((short) Integer.parseInt(preferences.getString(getString(R.string.prefNumeratorShort), "6000")));
		Settings.setDenominatorShort((short) Integer.parseInt(preferences.getString(getString(R.string.prefDenominatorShort), "8192")));
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
		
		logScrollView.post(new Runnable(){
			public void run() {
				logScrollView.fullScroll(View.FOCUS_DOWN);
			}
		});
	}
	
	private void settingSummary(){
		appendTextView("Numerator: " + Settings.numeratorFloat + " | Denominator: "+ Settings.denominatorFloat);
		appendTextView("Numerator: " + Settings.numeratorShort + " | Denominator: "+ Settings.denominatorShort);
		appendTextView("Iterations: " + Settings.iterations);
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
}
