package com.neo.ecopowermapsv1;

import com.google.android.gms.maps.GoogleMap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

public class SensorService {
	
	private SensorManager sensor;
	private float acceleration,currentAcceleration,lastAcceleration;
	private GoogleMap map;
	private static int casual=0;
	private int[] types={GoogleMap.MAP_TYPE_HYBRID,GoogleMap.MAP_TYPE_TERRAIN,GoogleMap.MAP_TYPE_SATELLITE,GoogleMap.MAP_TYPE_NORMAL};
	private String[] nameTypes={"Nessuno","Normale","Satellite","Terreno","Ibrido"};
	private Context context;
	
	public SensorService(GoogleMap map){
		
		this.map=map;
		this.acceleration=0;
		this.currentAcceleration=SensorManager.GRAVITY_EARTH;
		this.lastAcceleration=SensorManager.GRAVITY_EARTH;
	}
	
	
	public void activate(Context context){
		
		
		sensor=(SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		
		sensor.registerListener(listener, sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
		
		this.context=context;
		}
	

	public void deActivate(){
		
		if(sensor!=null){
			
			sensor.unregisterListener(listener);
		
			sensor=null;
			
		}
		
	}

	
	public SensorEventListener listener= new SensorEventListener(){

		@Override
		public void onSensorChanged(SensorEvent event) {
			
			
			float x=event.values[0];
			float y=event.values[1];
			float z=event.values[2];
			
			lastAcceleration=currentAcceleration;
			
			currentAcceleration=x*x+y*y+z*z;
			
			acceleration=currentAcceleration*(currentAcceleration-lastAcceleration);
			
			if (casual>3)
				casual=0;
			
			if(acceleration>80000){
				
				map.setMapType(types[casual]);
				casual++;
				Toast.makeText(context, "Il tipo di mappa è "+ nameTypes[map.getMapType()], Toast.LENGTH_SHORT).show();
				
			}
			
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}};
	
	
}
