package com.neo.ecopowermapsv1;

import java.util.ArrayList;

import org.json.JSONArray;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	private GoogleMap map;
	private ActionBar actionBar;
	private ArrayList<Location>listMethane;
	private ArrayList<Location>listGPL;
	private ArrayList<Location>listElectricStations;
	private final String methaneURLRequest = "http://fanteam.altervista.org/request_methane_data.php";
	private final String gplURLRequest = "http://fanteam.altervista.org/request_gpl_data.php";
	private final String electricURLRequest = "http://fanteam.altervista.org/request_electric_stations_data.php";
	private Dialog currentDialog;
	private JSONRequest jsonRequest;
	private MethaneAsyncTask methaneRequest;
	private GPLAsyncTask gplRequest;
	private ElectricAsyncTask electricRequest;
	private String address, price;
	private String formattedAddress, provider, jacks, description;
	private double navigateToLat, navigateToLong;
	
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

	
	//AsyncTask per i dati
	public class MethaneAsyncTask extends AsyncTask<Void, Void, ArrayList<Location>> {

		 private ProgressDialog dialog;

		 protected void onPostExecute(ArrayList<Location> list) {
			 marker(list);
			 dialog.dismiss();
		 }
		 
		 protected void onPreExecute() {
			 dialog = new ProgressDialog(MainActivity.this);
	         dialog.setCancelable(true);
	         dialog.setTitle("Caricamento");
	         dialog.setMessage("Sto contattando il server...");
	         dialog.show();
	     }

		 protected ArrayList<Location> doInBackground(Void... params) {
			jsonRequest = new JSONRequest();	
			
			String json = jsonRequest.getTextFromUrl(methaneURLRequest);

			try {
				JSONArray jsonArray = new JSONArray(json);
				          
				// looping through all item nodes <item>    
				for (int i = 0; i < jsonArray.length(); i++) {
				    String indirizzo = jsonArray.getJSONObject(i).getString("indirizzo");
				    String latitudine = jsonArray.getJSONObject(i).getString("latitudine");
				    String longitudine= jsonArray.getJSONObject(i).getString("longitudine");
				    String prezzo=jsonArray.getJSONObject(i).getString("prezzo");
				                
				    //System.out.println(indirizzo+"/"+latitudine);
				                 
				    listMethane.add(new Location(indirizzo, prezzo, latitudine, longitudine)); // la variabile list è una lista in Java
				 }
			 } catch (Exception e) {
				 e.printStackTrace();
			 }
				
		return listMethane;
		 }
	}
	
	
	
	
	//AsyncTask per i dati
		public class GPLAsyncTask extends AsyncTask<Void, Void, ArrayList<Location>> {

			 private ProgressDialog dialog;

			 protected void onPostExecute(ArrayList<Location> list) {
				 marker(list);
				 dialog.dismiss();
			 }
			 
			 protected void onPreExecute() {
				 dialog = new ProgressDialog(MainActivity.this);
		         dialog.setCancelable(true);
		         dialog.setTitle("Caricamento");
		         dialog.setMessage("Sto contattando il server...");
		         dialog.show();
		     }

			 protected ArrayList<Location> doInBackground(Void... params) {
				jsonRequest = new JSONRequest();	
				
				String json = jsonRequest.getTextFromUrl(gplURLRequest);

				try {
					JSONArray jsonArray = new JSONArray(json);
					          
					// looping through all item nodes <item>    
					for (int i = 0; i < jsonArray.length(); i++) {
					    String indirizzo = jsonArray.getJSONObject(i).getString("indirizzo");
					    String latitudine = jsonArray.getJSONObject(i).getString("latitudine");
					    String longitudine= jsonArray.getJSONObject(i).getString("longitudine");
					    String prezzo=jsonArray.getJSONObject(i).getString("prezzo");
					                
					    //System.out.println(indirizzo+"/"+latitudine);
					                 
					    listGPL.add(new Location(indirizzo, prezzo, latitudine, longitudine)); // la variabile list è una lista in Java
					 }
				 } catch (Exception e) {
					 e.printStackTrace();
				 }
					
			return listGPL;
			 }
		}
		
		
		
		
		
		
		//AsyncTask Colonnine Elettriche
		public class ElectricAsyncTask extends AsyncTask<Void, Void, ArrayList<Location>> {

			 private ProgressDialog dialog;

			 protected void onPostExecute(ArrayList<Location> list) {
				 markerElectricStation(list);
				 dialog.dismiss();
			 }
			 
			 protected void onPreExecute() {
				 dialog = new ProgressDialog(MainActivity.this);
		         dialog.setCancelable(true);
		         dialog.setTitle("Caricamento");
		         dialog.setMessage("Sto contattando il server...");
		         dialog.show();
		     }

			 protected ArrayList<Location> doInBackground(Void... params) {
				jsonRequest = new JSONRequest();	
				
				String json = jsonRequest.getTextFromUrl(electricURLRequest);

				try {
					JSONArray jsonArray = new JSONArray(json);
					          
					// looping through all item nodes <item>    
					for (int i = 0; i < jsonArray.length(); i++) {
					    String formattedAddress = jsonArray.getJSONObject(i).getString("IndirizzoFormattato");
					    String latitude = jsonArray.getJSONObject(i).getString("Latitudine");
					    String longitude = jsonArray.getJSONObject(i).getString("Longitudine");
					    String provider =jsonArray.getJSONObject(i).getString("Provider");
					    String jacks = jsonArray.getJSONObject(i).getString("Prese");
					    int numStations = Integer.parseInt(jsonArray.getJSONObject(i).getString("NumPostazioni"));
					    String description = jsonArray.getJSONObject(i).getString("Descrizione");
					    
					                
					    //System.out.println(indirizzo+"/"+latitudine);
					                 
					    listElectricStations.add(new Location(formattedAddress, provider, jacks, latitude, longitude, numStations, description)); // la variabile list è una lista in Java
					 }
				 } catch (Exception e) {
					 e.printStackTrace();
				 }
					
			return listElectricStations;
			 }
		}
	
	
	
	
	
	
	
	
	
	public void marker(ArrayList<Location> lista){
			
			for(int i=0;i<lista.size();i++){
				
				double latitudine= Double.parseDouble(lista.get(i).getLatitude());
				double longitudine=Double.parseDouble(lista.get(i).getLongitude());
				
				this.address=lista.get(i).getAddress();
				this.price=lista.get(i).getPrice();
				
				//System.out.println(INDI+"/"+PRE);
				
				LatLng coor= new LatLng(latitudine, longitudine);
				
				map.addMarker(new MarkerOptions().position(coor).title(this.address).snippet("Prezzo: "+this.price+"€"));
				
				//map.setOnMarkerClickListener(this);
				
				//creo la finestra delle info
				
				map.setInfoWindowAdapter(new InfoWindowAdapter(){

					@Override
					public View getInfoContents(Marker marker) {
						
		                View v = getLayoutInflater().inflate(R.layout.finestra, null);
		                
		                LatLng clickMarkerLatLng = marker.getPosition();
		                
		                navigateToLat=clickMarkerLatLng.latitude;
		                navigateToLong=clickMarkerLatLng.longitude;
		                
		                TextView title = (TextView) v.findViewById(R.id.textView1);
		                title.setText(marker.getTitle());
		                
		                TextView prezzo=(TextView) v.findViewById(R.id.textView2);
		                prezzo.setText(marker.getSnippet());

		                map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

							//se clicco il marker apro un menu
		                	
		                	@Override
							public void onInfoWindowClick(Marker marker) {
								// TODO Auto-generated method stub
		                		
		                		//avvio il navigatore
		                		
		                		currentDialog=new Dialog(MainActivity.this);
		                		currentDialog.setContentView(R.layout.scelta);
		                		
		                		currentDialog.setTitle("Scegli cosa fare");
		                		
		                		Button vai=(Button) currentDialog.findViewById(R.id.vaiqui);
		                		Button pref=(Button) currentDialog.findViewById(R.id.pref);
		                		
		                		vai.setOnClickListener(l);
		                		
		                		currentDialog.show();
								
							}
		                	
		                	
		                });
						
						return v;
					}

					@Override
					public View getInfoWindow(Marker marker) {
						// TODO Auto-generated method stub
						return null;
					}
					
					
					
					});
				
				
			}
			
			
		}
	
	
		public void markerElectricStation(ArrayList<Location> electricList) {
			
			for(int i=0;i<electricList.size();i++){
				
				double latitudine= Double.parseDouble(electricList.get(i).getLatitude());
				double longitudine=Double.parseDouble(electricList.get(i).getLongitude());
				
				this.formattedAddress=electricList.get(i).getFormattedAddress();
				this.provider=electricList.get(i).getProvider();
				this.jacks=electricList.get(i).getJacks();
				this.description=electricList.get(i).getDescription();
				
				//System.out.println(INDI+"/"+PRE);
				
				LatLng coor= new LatLng(latitudine, longitudine);
				
				map.addMarker(new MarkerOptions().position(coor).title(this.formattedAddress).snippet("Provider: "+ this.provider
																									+ "Prese: " + this.jacks
																									+ "Descrizione: " + this.description));
				
				//map.setOnMarkerClickListener(this);
				
				//creo la finestra delle info
				
				map.setInfoWindowAdapter(new InfoWindowAdapter(){

					@Override
					public View getInfoContents(Marker marker) {
						
		                View v = getLayoutInflater().inflate(R.layout.finestra, null);
		                
		                LatLng clickMarkerLatLng = marker.getPosition();
		                
		                navigateToLat=clickMarkerLatLng.latitude;
		                navigateToLong=clickMarkerLatLng.longitude;
		                
		                TextView title = (TextView) v.findViewById(R.id.textView1);
		                title.setText(marker.getTitle());
		                
		                TextView prezzo=(TextView) v.findViewById(R.id.textView2);
		                prezzo.setText(marker.getSnippet());

		                map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

							//se clicco il marker apro un menu
		                	
		                	@Override
							public void onInfoWindowClick(Marker marker) {
								// TODO Auto-generated method stub
		                		
		                		//avvio il navigatore
		                		
		                		currentDialog=new Dialog(MainActivity.this);
		                		currentDialog.setContentView(R.layout.scelta);
		                		
		                		currentDialog.setTitle("Scegli cosa fare");
		                		
		                		Button vai=(Button) currentDialog.findViewById(R.id.vaiqui);
		                		Button pref=(Button) currentDialog.findViewById(R.id.pref);
		                		
		                		vai.setOnClickListener(l);
		                		
		                		currentDialog.show();
								
							}
		                	
		                	
		                });
						
						return v;
					}

					@Override
					public View getInfoWindow(Marker marker) {
						// TODO Auto-generated method stub
						return null;
					}
					
					
					
					});
				
				
			}
		}
		
		public OnClickListener l= new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				currentDialog.dismiss();
				
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+ navigateToLat + ","+ navigateToLong));
		
				startActivity(i);
			
			}

			
		};
	 
	
}