package com.neo.ecopowermapsv1;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

@SuppressWarnings("unused")
public class ViewFavourite extends ActionBarActivity{
	
	 private long rowID; // seleziona la riga del preferito
	 private TextView nameTextView; // text view del nome
	 private TextView latitudeTextView; // textView della latitudine
	 private TextView longitudeTextView; // textView della longitudine
	 private double Navigatelat,Navigatelong; //utili per la navigazione
	 
	 
	 @Override
	   public void onCreate(Bundle savedInstanceState) 
	   {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.view_favourite);
          // prendo gli edit Text
	      nameTextView = (TextView) findViewById(R.id.nameTextView);
	      latitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
	      longitudeTextView = (TextView) findViewById(R.id.longitudeTextView);
	      
	      // prendo la riga che ho passato nell'activity FavouriteList
	      Bundle extras = getIntent().getExtras();
	      rowID = extras.getLong(FavouriteList.ROW_ID); 
	   } //finisco metodo
	 
	 
	 // chiamato quando l'activity è appena stata creata e la uso per riempire le textView
	   @Override
	   protected void onResume()
	   {
	      super.onResume();
	      // creo un nuovo thread e lo eseguo
	      new LoadFavouriteTask().execute(rowID);
	   } // end method onResume
	   
	   
	 // interrogo il database e mostro i risultati in un thread a parte
	   private class LoadFavouriteTask extends AsyncTask<Long, Object, Cursor> 
	   {
	      DatabaseConnector databaseConnector = new DatabaseConnector(ViewFavourite.this);
         
	      // accedo al database
	      @Override
	      protected Cursor doInBackground(Long... params)
	      {
	         databaseConnector.open();
	         
	         // restituisco un cursore contenete le informazioni relative al mio ID
	         return databaseConnector.getOnePref(params[0]);
	      } // fine metodo doInBackground

	      // uso il Cursor di doInBackground per riempire le textView
	      @Override
	      protected void onPostExecute(Cursor result)
	      {
	         super.onPostExecute(result);
	   
	         result.moveToFirst(); // vado al primo risultato senno' partivo da 0
	         // prendo l'indice delle colonne che contengono i dati
	         int nameIndex = result.getColumnIndex("nome");
	         int latitudeIndex = result.getColumnIndex("latitudine");
	         int longitudeIndex = result.getColumnIndex("longitudine");
	         
	         Navigatelat=Double.parseDouble(result.getString(latitudeIndex));
	         Navigatelong=Double.parseDouble(result.getString(longitudeIndex));
	        
	         // riempio le textView con i rispettivi dati
	         nameTextView.setText(result.getString(nameIndex));
	         latitudeTextView.setText(result.getString(latitudeIndex));
	         longitudeTextView.setText(result.getString(longitudeIndex));
	         
	         result.close(); // chiudo il cursor 
	         databaseConnector.close(); // chiudo il database
	         
	      } // fine metodo onPostExecute
	   } // fine classe LoadFavouriteTask
	   
	   
	
	   // creao il menu' rispettivo ad ogni preferito, ovvero modificare o eliminare il preferito
	   @Override
	   public boolean onCreateOptionsMenu(Menu menu) 
	   {
	      super.onCreateOptionsMenu(menu);
	      MenuInflater inflater = getMenuInflater();
	      inflater.inflate(R.menu.view_favourite_menu, menu);
	      return true;
	   } // fine metodo onCreateOptionsMenu
	   
	   
	   // gestisco le rispettive scelte
	   @Override
	   public boolean onOptionsItemSelected(MenuItem item) 
	   {
	      switch (item.getItemId()) 
	      {
	         case R.id.editItem:
	            // creo un intent dove si puo' modificare il nome
	            Intent addEditContact =new Intent(this, EditFavourite.class);
	            // passo i parametri all'intent
	            addEditContact.putExtra(FavouriteList.ROW_ID, rowID);
	            addEditContact.putExtra("nome", nameTextView.getText());
	            addEditContact.putExtra("latitudine", latitudeTextView.getText());
	            addEditContact.putExtra("longitudine", longitudeTextView.getText());
	            
	            startActivity(addEditContact); // inizio l'activity
	            return true;
	         
	         case R.id.deleteItem:
	            deleteContact(); // cancello il preferito
	            return true;
	            
	         case R.id.navigateItem:
	        	 //avvio la navigazione
	        	 Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+ Navigatelat + ","+ Navigatelong));
	        	 startActivity(i);
	        	 return true;
	 			
	         default:
	            return super.onOptionsItemSelected(item);
	      } // fine switch
	   } // fine metodo onOptionItemSelected
	   
	   
	   
	   // cancella un preferito
	   private void deleteContact()
	   {
	      // creo un AlertDialog
	      AlertDialog.Builder builder = new AlertDialog.Builder(ViewFavourite.this);

	      builder.setTitle(R.string.confirmTitle); 
	      builder.setMessage(R.string.confirmMessage); 

	      // creo un bottone per elimare il preferito con un listener
	      builder.setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener()
	         {
	            @Override
	            public void onClick(DialogInterface dialog, int button)
	            {
	               //chiamo il db
	            	final DatabaseConnector databaseConnector = new DatabaseConnector(ViewFavourite.this);

	               // creo un AsyncTask che mi cancella il preferito in un altro thread
	   
	               AsyncTask<Long, Object, Object> deleteTask = new AsyncTask<Long, Object, Object>()
	                  {
	                     @Override
	                     protected Object doInBackground(Long... params)
	                     {
	                        databaseConnector.deletePref(params[0]); 
	                        return null;
	                     } // fine metodo doInBackground

	                     @Override
	                     protected void onPostExecute(Object result)
	                     {
	                        finish(); // ritorno alla FavouriteListActivity
	                     } // fine metodo onPostExecute
	                  }; // finisco AsyncTask

	               // eseguo l'AsyncTask per eliminare il preferito di riga Rowid
	               deleteTask.execute(new Long[] { rowID });               
	            } // finisco metodo onClick
	         } 
	      ); // fine metodo per gestire il PositiveButton
	      
	      
	      //inserisco anche un Negative button con listener null per annullare l'operazione
	      builder.setNegativeButton(R.string.button_cancel, null);
	      builder.show(); // faccio vedere l'alert
	   } // finisco il metodo deleteContact


}
