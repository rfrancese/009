package com.neo.ecopowermapsv1;


import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;



@SuppressWarnings("unused")
public class FavouriteList extends ListActivity{
	
	public static final String ROW_ID = "row_id"; // Intent extra key
	private ListView prefListView; // id della ListView
	private CursorAdapter contactAdapter; // cursorAdapter per l'estrazione dei record
	
	 // metodo chiamato quando viene creata l'activity
	   @SuppressWarnings("deprecation")
	@Override
	   public void onCreate(Bundle savedInstanceState) 
	   {
	      super.onCreate(savedInstanceState);
	      prefListView = getListView(); // prende la listView
	      prefListView.setOnItemClickListener(viewPrefListener);      

	      // prendo tutti i record nome dei preferiti e li metto nella ListView
	      String[] from = new String[] { "nome" };
	      
	      int[] to = new int[] { R.id.prefTextView };
	     
	      contactAdapter = new SimpleCursorAdapter(FavouriteList.this, R.layout.pref_list_item, null, from, to);
	      setListAdapter(contactAdapter); // set contactView's adapter
	  
	   } // fine metodo onCreate
	   
	   
	   //metodo chiamato quando accade un cambio di configurazione
	   @Override
	   protected void onResume() 
	   {
	      super.onResume(); 
	      // create new GetContactsTask and execute it 
	       new GetPrefTask().execute((Object[]) null);
	    } // end method onResume

	  
	   //metodo che interrompe l'activity rilasciando le risorse
	   @SuppressWarnings("deprecation")
	@Override
	   protected void onStop() 
	   {
	      Cursor cursor = contactAdapter.getCursor(); // prendo il Cursor corrente
	      
	      if (cursor != null) 
	         cursor.deactivate(); // lo disattivo
	      contactAdapter.changeCursor(null); // metto il contactAdapter a null
	      super.onStop();
	   } // fine metodo
	   
	   
	   // faccio le query in un secondo thread
	   private class GetPrefTask extends AsyncTask<Object, Object, Cursor> 
	   {
	      DatabaseConnector databaseConnector = new DatabaseConnector(FavouriteList.this);
           // accedo al database
	      @Override
	      protected Cursor doInBackground(Object... params)
	      {
	         databaseConnector.open();
             // prendo il cursor di tutti i contatti
	         return databaseConnector.getAllPref(); 
	      } //fine metodo doInBackground

	     
	      // uso il cursore con tutti i preferiti ritornato in doInBackground
	      @Override
	      protected void onPostExecute(Cursor result)
	      {
	         contactAdapter.changeCursor(result); // setto il Cursor ai risultati ottenuti
	         databaseConnector.close();
	      } // finisco il metodo onPostExecute
	   } // finisco AsyncTask
	   
	   
	// evento che risponde quando viene premuto un preferito dove si vedono i dettagli
	   OnItemClickListener viewPrefListener = new OnItemClickListener() 
	   {
	      @Override
	      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
	      {
	         // creo un Intent che mi apre una seconda activity per vedere i risultati
	         Intent viewContact = new Intent(FavouriteList.this, ViewFavourite.class);
	         // passo alla seconda acrivity l'id della riga della List Activity che è nell arg3
	         viewContact.putExtra(ROW_ID, arg3);
	         startActivity(viewContact); // inizio l'activity viewFavourite
	      } //fine metodo onItemClick
	   }; // fine viewContactListener
	   
}
