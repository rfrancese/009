package com.neo.ecopowermapsv1;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.app.Dialog;
import android.os.Bundle;
import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	private GoogleMap map;
	private ActionBar actionBar;
	private ArrayList<Location>listMethaneAddresses;
	private ArrayList<Location>listGPLAddresses;
	private ArrayList<Location>listElectricStationsAddresses;
	private final String methaneURLRequest = "http://fanteam.altervista.org/request_methane_data.php";
	private final String gplURLRequest = "http://fanteam.altervista.org/request_gpl_data.php";
	private final String electricURLRequest = "http://fanteam.altervista.org/request_electric_stations_data.php";
	private Dialog currentDialog;
	private JSONRequest jsonRequest;
	
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/* Con il metodo hide() è possibile nascondere la ActionBar*/
		this.actionBar = getActionBar();
		this.actionBar.show();
		
		this.map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		
		android.location.Location myLocation = map.getMyLocation();
		LatLng myPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 5));
	}
	
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_settings:
				Toast.makeText(getBaseContext(), "Clicked on the settings item.", Toast.LENGTH_LONG).show();
				return true;
			
			case R.id.action_important:
				Toast.makeText(getBaseContext(), "Clicked on the add to favorite item.", Toast.LENGTH_LONG).show();
				return true;
			
			case R.id.action_electric_filter:
				//Toast.makeText(getBaseContext(), "Clicked on the electric filter item.", Toast.LENGTH_LONG).show();
				
				ConnectionDetector connectionDetectorElectric = new ConnectionDetector(getApplicationContext());
				boolean internetPresentElectric = connectionDetectorElectric.isConnectingToInternet();
				
				if (internetPresentElectric) {
					this.actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("lightgrey")));
					return true;
				}
			    else
					Toast.makeText(getApplicationContext(), "No connection to Internet.", Toast.LENGTH_LONG).show();
			
			case R.id.action_gpl_filter:
				//Toast.makeText(getBaseContext(), "Clicked on the gpl filter item.", Toast.LENGTH_LONG).show();
				
				ConnectionDetector connectionDetectorGPL = new ConnectionDetector(getApplicationContext());
				boolean internetPresentGPL = connectionDetectorGPL.isConnectingToInternet();
				
				if (internetPresentGPL) {
					this.actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("lightgrey")));
					return true;
				}
			    else
					Toast.makeText(getApplicationContext(), "No connection to Internet.", Toast.LENGTH_LONG).show();
			
			case R.id.action_methane_filter:
				//Toast.makeText(getBaseContext(), "Cliecked on the methane filter item.", Toast.LENGTH_LONG).show();
				
				ConnectionDetector connectionDetectorMethane = new ConnectionDetector(getApplicationContext());
				boolean internetPresentMethane = connectionDetectorMethane.isConnectingToInternet();
				
				if (internetPresentMethane) {
					this.actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("lightgrey")));
					return true;
				}
			    else
					Toast.makeText(getApplicationContext(), "No connection to Internet.", Toast.LENGTH_LONG).show();
				
		}
		return super.onOptionsItemSelected(item);
	}
}