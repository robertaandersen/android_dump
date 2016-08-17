package com.example.brewcenter;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SetTemperature extends ActionBarActivity {
	final Handler handler = new Handler(); 
	Thread workerThread; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
		//Sækir skilaboð frá intent
		Intent intent = getIntent(); 
		String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);						
		
		
		//Býr til nýtt textView
		final TextView textView = new TextView(this);
		textView.setTextSize(40);
		textView.setText(message);
				
		//Setur nýja textviewið sem layout
		setContentView(textView);
		workerThread = new Thread(new Runnable(){
			@Override
			public void run() {
				int count = 0; 
				int tempDate = 1000;
				int stopTime = 0; 				
				while(!Thread.currentThread().isInterrupted() && count <= 1000){										
					Date d = new Date();
					int startTime = (int) d.getTime();  
					if(tempDate >= 1000){
						double i = Math.random() * 100;					
						updateUI(i+"", handler, textView);
						count++;
						stopTime = (int) d.getTime();
					}					 					 
					tempDate = startTime - stopTime; 
				}
			}});
			workerThread.start(); 											
	}

	 
	
	public void updateUI(final String message, Handler handler, final TextView textView)
	{
		handler.post(new Runnable(){
			@Override
			public void run() {						
				
				textView.setText("");							
			}					
		});			
	}
	
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.set_temperature, menu);
		return true;
	}*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_set_temperature,
					container, false);
			return rootView;
		}
	}

}
