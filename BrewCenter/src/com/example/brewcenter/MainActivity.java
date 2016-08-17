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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	public final static String EXTRA_MESSAGE = "com.example.BrewCenter.MESSAGE"; 
	int readBufferPosition;
	byte[] readBuffer;
	public final static int REQUEST_ENABLE_BT = 1; 	
	public ArrayAdapter<String> mArrayAdapter = null;
	public OutputStream mmOutputStream = null; 
	public InputStream mmInputStream = null;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   
        /*TextView textView = (TextView)findViewById(R.id.current_temperature);  
        textView.setText("Halló");*/
        
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
    } 
   
	private void startBlueTooth() {
    	//Start BlueTooth adapter
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
        	Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }		
        
        Set pairedDevices = mBluetoothAdapter.getBondedDevices(); 
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (Object device : pairedDevices) {
            	BluetoothDevice d = (BluetoothDevice)device; 
            	UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID                
            	BluetoothSocket mmSocket = null; 
                try {
					String name = d.getName();
					if(name.equals("itead"))
                	{
						mmSocket = d.createRfcommSocketToServiceRecord(uuid);
						mmSocket.connect();
						mmOutputStream = mmSocket.getOutputStream();
						mmInputStream = mmSocket.getInputStream();
                	}
				} catch (IOException e) {				
					try {
						mmSocket.close();
					} catch (IOException e1) {						
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
                            	
            }
        }               
        // Create a BroadcastReceiver for ACTION_FOUND
        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);                   
                    // Add the name and address to an array adapter to show in a ListView
                    mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }
        };
        
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
	}
	
	public String readTemperature()
	{
		final byte delimiter = 10; //This is the ASCII code for a newline character
		readBufferPosition = 0;
		readBuffer = new byte[1024];
		try {
			int bytesAvailable = mmInputStream.available();
			if(bytesAvailable > 0)
			{
				byte[] packetBytes = new byte[bytesAvailable];
				mmInputStream.read(packetBytes);
				for(int i=0;i<bytesAvailable;i++)
                {
                    byte b = packetBytes[i];
                    if(b == delimiter)
                    {
                        byte[] encodedBytes = new byte[readBufferPosition];
                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                        final String data = new String(encodedBytes, "US-ASCII");
                        readBufferPosition = 0;
                        return data; 
                    }
                    else
                    {
                        readBuffer[readBufferPosition++] = b;
                    }				
                }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ""; 
		}
		return null;
	
	}
	
	public void setTemp(View view)
    {
    	Intent intent = new Intent(this, SetTemperature.class); 
    	EditText editText = (EditText) findViewById(R.id.target_temperature);
    	String message = editText.getText().toString(); 
    	intent.putExtra(EXTRA_MESSAGE, message);    	
    	startActivity(intent); 
    
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
