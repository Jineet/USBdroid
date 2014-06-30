package com.example.dr.droid;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String ACTION_USB_PERMISSION =
		    "com.android.example.USB_PERMISSION";
	UsbDeviceConnection connection;
	UsbInterface intf;
	ArrayList<UsbEndpoint> endPoints=new ArrayList<UsbEndpoint>();
	UsbManager mUsbManager;
	UsbDevice device=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
	    

		
		for (UsbDevice devices :  mUsbManager.getDeviceList().values()){
		        device=devices;
		      Log.d("device detected",""+device);
		            break;
		}
		
		PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter("ACTION_USB_PERMISSION");
		registerReceiver(mUsbReceiver, filter);
		mUsbManager.requestPermission(device, mPermissionIntent);
		Log.d("Permission","Intent fired");
		
        //int dir3=endPoints.get(2).getDirection();
        //Toast.makeText(getApplicationContext(), ""+ dir1 +""+ dir2  ,
    	//		   Toast.LENGTH_LONG).show();
        
         
         
        myThread thread=new myThread();
        thread.start();
	}
       
        
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onDestroy(){
		 connection.releaseInterface(intf);
	}
	
	
	
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

		    public void onReceive(Context context, Intent intent) {
		    	
		    	  
		            synchronized (this) {
		                //UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

		                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
		                    if(device != null){
		                    	Log.d("Connection notification", "device connected" );
		                    	  Toast.makeText(getApplicationContext(), "Device Connected"+ device,
		        			 			   Toast.LENGTH_LONG).show(); 
		                    	
		                    	
		                   }
		                } 
		                else {
		                    Log.d("Connection notification", "permission denied for device " + device);
		                    Toast.makeText(getApplicationContext(), "Failed to connect :(",
	                    			   Toast.LENGTH_LONG).show();
		                   ;
		                }
		            }
		        
		    }
		}; 
	
	private class myThread extends Thread{	
		@Override
		public void run(){
			Log.d("Thread","Thread started");
			 byte[] bytes= {(byte) 0xff,(byte) 0x1e,(byte) 0x54, (byte)0x64, (byte)0x72, (byte)0x92};
	         int TIMEOUT = 0;
	         boolean forceClaim = true;
	         try {
					sleep(100);
				} catch (InterruptedException e) {
				}
	         UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
	         Log.d("Device",""+device);
	         Log.d("Device Manager",""+mUsbManager);
	         if(mUsbManager.hasPermission(device)){
	        	 
	        	 UsbInterface intf = device.getInterface(0);
	         	
	             for(int i=0;i<intf.getEndpointCount();i++ ){
	             	endPoints.add(intf.getEndpoint(i));
	             }
	             int dir1=endPoints.get(0).getDirection();
	             Log.d("endPoint direction",""+dir1);
	             int dir2=endPoints.get(1).getDirection();
	             Log.d("endPoint direction",""+dir2);
	         UsbDeviceConnection connection = mUsbManager.openDevice(device); 
	         connection.claimInterface(intf, forceClaim);
	         connection.bulkTransfer(endPoints.get(1), bytes, bytes.length, TIMEOUT);
	         }
	         else{
	        	 Log.d("Error","Get Permission first!!");
	         }
			
	         
		}
	}
}

