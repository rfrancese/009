package com.neo.ecopowermapsv1;

import java.util.ArrayList;
import org.json.JSONArray;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.ActionBar;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends ActionBarActivity {

	private GoogleMap map;
	private ActionBar actionBar;
	
	private ArrayList<Location>listMethane;
	private ArrayList<Location>listGPL;
	private ArrayList<Location>listElectricStations;
	
	private final String methaneURLRequest 	= "http://fanteam.altervista.org/request_methane_data.php";
	private final String gplURLRequest 		= "http://fanteam.altervista.org/request_gpl_data.php";
	private final String electricURLRequest = "http://fanteam.altervista.org/request_electric_stations_data.php";
	
	private Dialog currentDialog;
	
	private JSONRequest jsonRequest;
	
	private MethaneAsyncTask methaneRequest;
	private GPLAsyncTask gplRequest;
	private ElectricAsyncTask electricRequest;
	
	private String address, price;
	private String formattedAddress, provider, jacks, description;
	
	private double navigateToLat, navigateToLong;
	
	private int scelta = 0;
	
	private SensorService sensorService;
	
	private ListView listView;
	
	private CameraPosition camera; 
	
	private PutElectricMarkersAsyncTask putElectricMarkersRequest;
	private PutMethaneMarkersAsyncTask putMethaneMarkersRequest;
	private PutGPLMarkersAsyncTask putGPLMarkersRequest;
	
	private int currentFilter; 
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.actionBar = getActionBar();
		this.actionBar.show();
		
		this.map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        
        android.location.Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        
        if (location != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
            .target(new LatLng(location.getLatitude(), location.getLongitude()))      
            .zoom(11)                   
            .bearing(90)                
            .tilt(40)                   
            .build();                   
        
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            
            sensorService = new SensorService(map);
        }
		
		this.listElectricStations 	= new ArrayList<Location>();
		this.listGPL				= new ArrayList<Location>();
		this.listMethane			= new ArrayList<Location>();
	}
	
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@SuppressLint("UseValueOf")
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			
			case R.id.action_settings:
				Toast.makeText(getBaseContext(), "Clicked on the settings item.", Toast.LENGTH_LONG).show();
				return true;
			
			case R.id.action_important:
				Toast.makeText(getBaseContext(), "Clicked on the add to favorite item.", Toast.LENGTH_LONG).show();
				return true;
			
			case R.id.action_electric_filter:
				
				//Toast.makeText(getBaseContext(), "Clicked on the electric filter item.", Toast.LENGTH_LONG).show();
				if (this.listElectricStations.size() == 0) {
					ConnectionDetector connectionDetectorElectric = new ConnectionDetector(getApplicationContext());
					boolean internetPresentElectric = connectionDetectorElectric.isConnectingToInternet();
				
					if (internetPresentElectric) {
						this.actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1E90FF")));
						this.map.clear();
						this.electricRequest = new ElectricAsyncTask();
						this.electricRequest.execute();
						this.currentFilter = 1;
					} else
						Toast.makeText(getApplicationContext(), "Internet connection error.", Toast.LENGTH_LONG).show();
	
				} else {
					this.map.clear();
					this.putElectricMarkersRequest = new PutElectricMarkersAsyncTask();
					this.putElectricMarkersRequest.execute();
					this.currentFilter = 1;
				}
				
				return true;
				
			case R.id.action_gpl_filter:
				
				//Toast.makeText(getBaseContext(), "Clicked on the gpl filter item.", Toast.LENGTH_LONG).show();
				this.scelta = 1;
				
				if (this.listGPL.size() == 0) {
					ConnectionDetector connectionDetectorGPL = new ConnectionDetector(getApplicationContext());
					boolean internetPresentGPL = connectionDetectorGPL.isConnectingToInternet();
				
					if (internetPresentGPL) {
						this.actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DC143C")));
						this.map.clear();
						this.gplRequest = new GPLAsyncTask();
						this.gplRequest.execute();
						this.currentFilter = 2;
					} else
						Toast.makeText(getApplicationContext(), "Internet connection error.", Toast.LENGTH_LONG).show();
				} else {
					this.map.clear();
					this.putGPLMarkersRequest = new PutGPLMarkersAsyncTask();
					this.putGPLMarkersRequest.execute();
					this.currentFilter = 2;
				}
				
				return true;
			
			case R.id.action_methane_filter:
				
				//Toast.makeText(getBaseContext(), "Cliecked on the methane filter item.", Toast.LENGTH_LONG).show();
				this.scelta = 0;
				
				if (this.listMethane.size() == 0) {
					ConnectionDetector connectionDetectorMethane = new ConnectionDetector(getApplicationContext());
					boolean internetPresentMethane = connectionDetectorMethane.isConnectingToInternet();
				
					if (internetPresentMethane) {
						this.actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2E8B57")));
						this.map.clear();
						this.methaneRequest = new MethaneAsyncTask();
						this.methaneRequest.execute();
						this.currentFilter = 3;
					} else
						Toast.makeText(getApplicationContext(), "Internet connection error.", Toast.LENGTH_LONG).show();
				} else {
					this.map.clear();
					this.putMethaneMarkersRequest = new PutMethaneMarkersAsyncTask();
					this.putMethaneMarkersRequest.execute();
					this.currentFilter = 3;
				}
				
				return true;
				
			case R.id.vista_setting:
				
				ChooseOption();
				return true;
				
			case R.id.to_the_nearest_service:
				
				//Toast.makeText(getApplicationContext(), "Richiesta del servizio: Portami alla più vicina.", Toast.LENGTH_SHORT).show();
				
				//Verifico se l'utente ha selezionato almeno una volta un filtro un filtro
				if (this.listElectricStations.size() != 0 || this.listGPL.size() != 0 || this.listMethane.size() != 0) {
					
					//Verifica della presenza della connessione ad Internet
					ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
					boolean internetPresent = connectionDetector.isConnectingToInternet();
				
					if (internetPresent) {
						//Indice dell'elemento che rappresenta la distanza lineare più breve
						int lowerDistanceIndex;
						
						//Conterrà la distanza più breve
						double lowerDistanceValue;	
						
						//ArrayList delle distanze calcolate
						ArrayList<Double> distancesArrey = new ArrayList<Double>(); 
						
						//Info sul marker più vicino
						double nearestMarkerLatitude = 0;
						double nearestMarkerLongitude = 0;
						
						//Acquisisco le informazioni sulla posizione attuale 
						LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
						Criteria criteria = new Criteria();
						android.location.Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
						
						ComputeDistanceBetween distanceBetween = new ComputeDistanceBetween();
						
						//Agisco in base all'ultimo filtro che è stato selezionato per ultimo
						switch (this.currentFilter) {
							//Colonnine elettriche
			        		case 1:
			        			//Calcolo la prima distanza tra la posizione attuale e il primo marker presente nell'array, per confronti successivi
			        			double initialMarkerLatitudeEle  = Double.parseDouble(this.listElectricStations.get(0).getLatitude());
		        				double initialMarkerLongitudeEle = Double.parseDouble(this.listElectricStations.get(0).getLongitude());
		        				double initialDistanceEle = distanceBetween.distance(location.getLatitude(), initialMarkerLatitudeEle, location.getLongitude(), initialMarkerLongitudeEle);
								lowerDistanceIndex = 0;
								lowerDistanceValue = initialDistanceEle;
			        			
			        			double tempDistanceEle;
			        			double tempMarkerLatitudeEle;
			        			double tempMarkerLongitudeEle;
			        			
			        			for (int i = 1; i < this.listElectricStations.size(); i++) {
			        				tempMarkerLatitudeEle  = Double.parseDouble(this.listElectricStations.get(i).getLatitude());
			        				tempMarkerLongitudeEle = Double.parseDouble(this.listElectricStations.get(i).getLongitude());
			        				
			        				tempDistanceEle = distanceBetween.distance(location.getLatitude(), tempMarkerLatitudeEle, location.getLongitude(), tempMarkerLongitudeEle);
			        				distancesArrey.add(new Double(tempDistanceEle));
			        			}
			        			
			        			for (int i = 0; i < distancesArrey.size(); i++) {
			        				if (distancesArrey.get(i).doubleValue() < lowerDistanceValue)
			        					lowerDistanceValue = distancesArrey.get(i).doubleValue();
			        					lowerDistanceIndex = i;
			        			}
			        			
			        			//Acquisisco latitudine e longitudine del marker più vicino alla posizione attuale
			        			nearestMarkerLatitude  = Double.parseDouble(this.listElectricStations.get(lowerDistanceIndex).getLatitude());
			        			nearestMarkerLongitude = Double.parseDouble(this.listElectricStations.get(lowerDistanceIndex).getLongitude());
			        			//String nearestMarkerFormattedAddressEle = this.listElectricStations.get(lowerDistanceIndex).getFormattedAddress();
			        			//Toast.makeText(getApplicationContext(), nearestMarkerFormattedAddressEle, Toast.LENGTH_LONG).show();
			        		
			        		//GPL
			        		case 2:
			        			//Calcolo la prima distanza tra la posizione attuale e il primo marker presente nell'array, per confronti successivi
			        			double initialMarkerLatitudeGPL  = Double.parseDouble(this.listGPL.get(0).getLatitude());
		        				double initialMarkerLongitudeGPL = Double.parseDouble(this.listGPL.get(0).getLongitude());
		        				double initialDistanceGPL = distanceBetween.distance(location.getLatitude(), initialMarkerLatitudeGPL, location.getLongitude(), initialMarkerLongitudeGPL);
								lowerDistanceIndex = 0;
								lowerDistanceValue = initialDistanceGPL;
			        			
			        			double tempDistanceGPL;
			        			double tempMarkerLatitudeGPL;
			        			double tempMarkerLongitudeGPL;
			        			
			        			for (int i = 1; i < this.listGPL.size(); i++) {
			        				tempMarkerLatitudeGPL  = Double.parseDouble(this.listGPL.get(i).getLatitude());
			        				tempMarkerLongitudeGPL = Double.parseDouble(this.listGPL.get(i).getLongitude());
			        				
			        				tempDistanceGPL = distanceBetween.distance(location.getLatitude(), tempMarkerLatitudeGPL, location.getLongitude(), tempMarkerLongitudeGPL);
			        				distancesArrey.add(new Double(tempDistanceGPL));
			        			}
			        			
			        			for (int i = 0; i < distancesArrey.size(); i++) {
			        				if (distancesArrey.get(i).doubleValue() < lowerDistanceValue)
			        					lowerDistanceValue = distancesArrey.get(i).doubleValue();
			        					lowerDistanceIndex = i;
			        			}
			        			
			        			//Acquisisco latitudine e longitudine del marker più vicino alla posizione attuale
			        			nearestMarkerLatitude  = Double.parseDouble(this.listGPL.get(lowerDistanceIndex).getLatitude());
			        			nearestMarkerLongitude = Double.parseDouble(this.listGPL.get(lowerDistanceIndex).getLongitude());
			        			//String nearestMarkerAddressGPL = this.listGPL.get(lowerDistanceIndex).getAddress();
			        			//Toast.makeText(getApplicationContext(), nearestMarkerAddressGPL, Toast.LENGTH_LONG).show();
			        		
			        		//Methane
			        		case 3:
			        			//Calcolo la prima distanza tra la posizione attuale e il primo marker presente nell'array, per confronti successivi
			        			double initialMarkerLatitudeMethane  = Double.parseDouble(this.listMethane.get(0).getLatitude());
		        				double initialMarkerLongitudeMethane = Double.parseDouble(this.listMethane.get(0).getLongitude());
		        				double initialDistanceMethane = distanceBetween.distance(location.getLatitude(), initialMarkerLatitudeMethane, location.getLongitude(), initialMarkerLongitudeMethane);
								lowerDistanceIndex = 0;
								lowerDistanceValue = initialDistanceMethane;
			        			
			        			double tempDistanceMethane;
			        			double tempMarkerLatitudeMeth;
			        			double tempMerkerLongitudeMath;
			        			
			        			for (int i = 1; i < this.listMethane.size(); i++) {
			        				tempMarkerLatitudeMeth  = Double.parseDouble(this.listMethane.get(i).getLatitude());
			        				tempMerkerLongitudeMath = Double.parseDouble(this.listMethane.get(i).getLongitude());
			        				
			        				tempDistanceMethane = distanceBetween.distance(location.getLatitude(), tempMarkerLatitudeMeth, location.getLongitude(), tempMerkerLongitudeMath);
			        				distancesArrey.add(new Double(tempDistanceMethane));
			        			}
			        			
			        			for (int i = 0; i < distancesArrey.size(); i++) {
			        				if (distancesArrey.get(i).doubleValue() < lowerDistanceValue)
			        					lowerDistanceValue = distancesArrey.get(i).doubleValue();
			        					lowerDistanceIndex = i;
			        			}
			        			
			        			//Acquisisco latitudine e longitudine del marker più vicino alla posizione attuale
			        			nearestMarkerLatitude  = Double.parseDouble(this.listMethane.get(lowerDistanceIndex).getLatitude());
			        			nearestMarkerLongitude = Double.parseDouble(this.listMethane.get(lowerDistanceIndex).getLongitude());
			        			//String nearestMarkerAddressMeth = this.listMethane.get(lowerDistanceIndex).getAddress();
			        			//Toast.makeText(getApplicationContext(), nearestMarkerAddressMeth, Toast.LENGTH_LONG).show();
						}// Fine switch
						
						//Svuoto l'ArrayList
						distancesArrey.clear();
						
						//Avvio il servizio di navigazione
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+ nearestMarkerLatitude + ","+ nearestMarkerLongitude));
						startActivity(intent);
						
					} else 
						Toast.makeText(getApplicationContext(), "Internet connection error.", Toast.LENGTH_LONG).show();
				} else
					Toast.makeText(getApplicationContext(), "È necessario selezionare un filtro.", Toast.LENGTH_SHORT).show();
				
		}
		return super.onOptionsItemSelected(item);
	}
	
	
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
				   String prezzo = jsonArray.getJSONObject(i).getString("prezzo");
				                
				   //System.out.println(indirizzo+"/"+latitudine);
				                 
				   listMethane.add(new Location(indirizzo, prezzo, latitudine, longitudine)); // la variabile list è una lista in Java
				}
			} catch (Exception e) {
				 e.printStackTrace();
			}
				
			return listMethane;
		 }
	}
	
	
	public class GPLAsyncTask extends AsyncTask<Void, Void, ArrayList<Location>> {

		private ProgressDialog dialog;

		protected void onPostExecute(ArrayList<Location> list) {
			marker(list);
			dialog.dismiss();
		}
			 
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainActivity.this);
		    dialog.setCancelable(false);
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
				
				    listElectricStations.add(new Location(formattedAddress, provider, jacks, latitude, longitude, numStations, description)); // la variabile list è una lista in Java
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
					
			return listElectricStations;
		}
	}
	
	public class PutMethaneMarkersAsyncTask extends AsyncTask<Void, Void, ArrayList<Location>> {

		private ProgressDialog dialog;

		protected void onPostExecute(ArrayList<Location>list) {
			 marker(list);
			 methaneRequest.cancel(true);
			 dialog.dismiss();
		}
			 
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainActivity.this);
		    dialog.setCancelable(false);
		    dialog.setTitle("Caricamento");
		    dialog.setMessage("Sto caricando i dati...");
		    dialog.show();
		}

		@Override
		protected ArrayList<Location> doInBackground(Void... params) {
				
			try {
				actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#2E8B57")));
            } catch (Exception e) {
            	e.printStackTrace();
			}
				
			return listMethane;
		}
	}		
		
	public class PutGPLMarkersAsyncTask extends AsyncTask<Void, Void, ArrayList<Location>> {

		private ProgressDialog dialog;
			
		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainActivity.this);
		    dialog.setCancelable(true);
		    dialog.setTitle("Caricamento");
		    dialog.setMessage("Sto caricando i dati...");
		    dialog.show();
		}

        @Override
		protected void onPostExecute(ArrayList<Location> result) {
			marker(result);
            gplRequest.cancel(true);
			dialog.dismiss();
		}
			 
			 
		@Override
		protected ArrayList<Location> doInBackground(Void... params) {
				
			try{
				actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DC143C")));
            } catch (Exception e) {
            	e.printStackTrace();
			}
				
			return listGPL;
		}
	}
		
	public class PutElectricMarkersAsyncTask extends AsyncTask<Void, Void, ArrayList<Location>> {

		private ProgressDialog dialog;

		protected void onPostExecute(ArrayList<Location> list) {
			 markerElectricStation(list);
			 electricRequest.cancel(true);
			 dialog.dismiss();
		}
			 
		protected void onPreExecute() {
			dialog = new ProgressDialog(MainActivity.this);
		    dialog.setCancelable(true);
		    dialog.setTitle("Caricamento");
		    dialog.setMessage("Sto caricando i dati...");
		    dialog.show();
		}

		@Override
		protected ArrayList<Location> doInBackground(Void... params) {
				
			try{
				actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1E90FF")));
            }catch (Exception e) {
            	e.printStackTrace();
			}
				
			return listElectricStations;
		}
	}

	public void marker(ArrayList<Location> lista) {
		for(int i = 0; i < lista.size(); i++) {
			double latitudine = Double.parseDouble(lista.get(i).getLatitude());
			double longitudine = Double.parseDouble(lista.get(i).getLongitude());
				
			this.address = lista.get(i).getAddress();
			this.price = lista.get(i).getPrice();
				
			//System.out.println(INDI+"/"+PRE);
				
			LatLng coor = new LatLng(latitudine, longitudine);
				
			if(this.scelta == 1)
				map.addMarker(new MarkerOptions().position(coor).title(this.address).snippet("Prezzo: "+this.price+"€"));
			else
				map.addMarker(new MarkerOptions().position(coor).title(this.address).snippet("Prezzo: "+this.price+"€").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));	
				
			//map.setOnMarkerClickListener(this);
				
			//creo la finestra delle info
			map.setInfoWindowAdapter(new InfoWindowAdapter() {

				@Override
				public View getInfoContents(Marker marker) {
						
	                View v = getLayoutInflater().inflate(R.layout.finestra, null);
		                
	                LatLng clickMarkerLatLng = marker.getPosition();
		                
	                navigateToLat = clickMarkerLatLng.latitude;
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
		               		String[] names = {"Vai al distributore","Salva nei preferiti"};
		           			currentDialog = new Dialog(MainActivity.this);
		           			currentDialog.setContentView(R.layout.scelta);
		           			currentDialog.setTitle("Cosa vuoi fare?");
		           			currentDialog.setCancelable(true);
		           			currentDialog.setCanceledOnTouchOutside(true);
		           			listView = (ListView) currentDialog.findViewById(R.id.lv);
		                		
		           			ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,names);
		        		    listView.setAdapter(adapter);
		                		
		        		    listView.setOnItemClickListener(new OnItemClickListener(){
		        		    	
		        		    	@Override
		        				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		        		    		if(position == 0)
		        		    			navigate();
		        					//else devo aggiungere nei preferiti
		        				}
		        	        });
		                	
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
		for(int i = 0; i < electricList.size(); i++) {
			double latitudine = Double.parseDouble(electricList.get(i).getLatitude());
			double longitudine = Double.parseDouble(electricList.get(i).getLongitude());
				
			this.formattedAddress = electricList.get(i).getFormattedAddress();
			this.provider = electricList.get(i).getProvider();
			this.jacks = electricList.get(i).getJacks();
			this.description = electricList.get(i).getDescription();
				
			//System.out.println(INDI+"/"+PRE);
				
			LatLng coor = new LatLng(latitudine, longitudine);
				
			if(description.equals("null"))
				map.addMarker(new MarkerOptions()
								.position(coor)
								.title(this.formattedAddress)
								.snippet("Provider: " + this.provider + "\nPrese: " + this.jacks)
								.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));																			
			else
				map.addMarker(new MarkerOptions()
								.position(coor)
								.title(this.formattedAddress)
								.snippet("Provider: " + this.provider + "\nPrese: " + this.jacks + "\nDescrizione: " + this.description)
								.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
					
			//map.setOnMarkerClickListener(this);
				
			//creo la finestra delle info
			map.setInfoWindowAdapter(new InfoWindowAdapter(){
				
				@Override
				public View getInfoContents(Marker marker) {
					View v = getLayoutInflater().inflate(R.layout.finestra, null);
		            
					LatLng clickMarkerLatLng = marker.getPosition();
		            
		            navigateToLat = clickMarkerLatLng.latitude;
		            navigateToLong = clickMarkerLatLng.longitude;
		                
		            TextView title = (TextView) v.findViewById(R.id.textView1);
		            title.setText(marker.getTitle());
		                
		            TextView prezzo = (TextView) v.findViewById(R.id.textView2);
		            prezzo.setText(marker.getSnippet());

		            map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

		            	//se clicco il marker apro un menu
		                @Override
						public void onInfoWindowClick(Marker marker) {
							// TODO Auto-generated method stub
		                	//avvio il navigatore
		                	String[] names = {"Vai al distributore","Salva nei preferiti"};
		           			currentDialog = new Dialog(MainActivity.this);
		           			currentDialog.setContentView(R.layout.scelta);
		           			currentDialog.setTitle("Cosa vuoi fare?");
		           			currentDialog.setCancelable(true);
		           			currentDialog.setCanceledOnTouchOutside(true);
		           			listView = (ListView) currentDialog.findViewById(R.id.lv);
		                		
		           			ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,names);
		        		    listView.setAdapter(adapter);
		                	listView.setOnItemClickListener(new OnItemClickListener(){

		        				@Override
		        				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		        					if(position == 0)
		        						navigate();
		        					//else devo aggiungere nei preferiti
		        				}
		        	        });
		                		
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
		
		
	public void navigate() {
		currentDialog.dismiss();
		
		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+ navigateToLat + ","+ navigateToLong));
		startActivity(i);
	}
		
	//funzione che mi permette la creazione di un alert per la funzionalità vista
	public void ChooseOption() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Informazione");
		builder.setMessage("Tale funzione permette di cambiare lo stile della mappa scuotendo il device. Vuoi attivare questa funzione?");
		builder.setCancelable(false);
		builder.setPositiveButton("SI", new android.content.DialogInterface.OnClickListener() {
				
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getBaseContext(), "Scuoti il device per cambiare lo stile!", Toast.LENGTH_LONG).show();
                sensorService.activate(MainActivity.this);
			}
		});
			
		builder.setNegativeButton("NO", new android.content.DialogInterface.OnClickListener() {
				
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(), "Hai disattivato la funzione!", Toast.LENGTH_LONG).show();
                sensorService.deActivate();
			}
		});
		
		AlertDialog alert = builder.create();
		alert.show();
	}
		
		
	//cerco di gestire la rotazione della mappa
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		this.camera = map.getCameraPosition();
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(this.camera != null)
			map.moveCamera(CameraUpdateFactory.newCameraPosition(this.camera));
	            
		this.camera = null;
			
			//if(this.actual.size() > 1000)
			//	marker(this.actual);
			//else
			//	markerElectricStation(this.actual);
	}
}