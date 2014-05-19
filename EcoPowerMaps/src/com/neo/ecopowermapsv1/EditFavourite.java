package com.neo.ecopowermapsv1;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditFavourite extends Activity{
	
	private long rowID; // id del preferito da modificare
	   
	   // EditText
	   private EditText nameEditText;
	   private String latitude;
	   private String longitude;
	   @SuppressWarnings("unused")
	private Bundle extras;
	   
	   
	   // chiamato quando inizia l'activity
	   @Override
	   public void onCreate(Bundle savedInstanceState) 
	   {
	      super.onCreate(savedInstanceState); 
	      setContentView(R.layout.edit_pref); // inflate dell'xml
	       nameEditText = (EditText) findViewById(R.id.nameEditText);
	       Bundle extras = getIntent().getExtras(); // mi prendo il nome passato

	      // se ci sono degli extra li uso per metterli di default
	      if (extras != null)
	      {
	         rowID = extras.getLong("row_id");
	         nameEditText.setText(extras.getString("nome")); 
	         latitude=extras.getString("latitudine");
	         longitude=extras.getString("longitudine");
	       	         
	      } // fine if
	       // metto il listener al bottone per salvare
	      Button saveContactButton = (Button) findViewById(R.id.saveContactButton);
	      saveContactButton.setOnClickListener(saveContactButtonClicked);
	   } // fine metodo onCreate
	   
	   
	   // listener del bottone Salva
	   OnClickListener saveContactButtonClicked = new OnClickListener() 
	   {
	      @Override
	      public void onClick(View v) 
	      {
	    	  //se ho inserito un nome valido
	          if (nameEditText.getText().length() != 0)
	         {
	            AsyncTask<Object, Object, Object> saveContactTask = new AsyncTask<Object, Object, Object>() 
	               {
	                  @Override
	                  protected Object doInBackground(Object... params) 
	                  {
	                     saveContact(); // salvo il contatto nel database
	                     return null;
	                  } // fine metodo doInBackGround
	      
	                  @Override
	                  protected void onPostExecute(Object result) 
	                  {
	                     finish(); // ritorno all'Activity precedente
	                  } // fine metodo onPostExecute
	               }; // fine AsyncTask
	               
	            // avvio il thread per salvare il contatto nel database
	            saveContactTask.execute((Object[]) null); 
	         } // fine if
	         else
	         {
	            // create un alertDialog per segnalare all'utente di inserire un nome valido
	            AlertDialog.Builder builder = new AlertDialog.Builder(EditFavourite.this);
	      
	            builder.setTitle(R.string.errorTitle); 
	            builder.setMessage(R.string.errorMessage);
	            builder.setPositiveButton(R.string.errorButton, null); 
	            builder.show(); 
	         } // fine else
	      } // fine metodo onClick
	   }; // fine OnClickListener

	  // salvo il preferito nel database
	   private void saveContact() 
	   {
	      System.out.println("Sto salvando");
	      DatabaseConnector databaseConnector = new DatabaseConnector(this);
	      System.out.println("Ho creato l'istanza");
	      
	     if (rowID==0)
	      {
	         // vuol dire che devo fare l'inserimento vero e proprio
	          System.out.println("Devo aggiungere");
	    	  databaseConnector.insertFavorites(nameEditText.getText().toString(),latitude,longitude);
	          
	      } // fine if
	      else
	     {
	    	  //devo aggiornare il preferito
	          System.out.println("Devo aggiornare");
	    	  databaseConnector.updateContact(rowID,latitude,longitude,nameEditText.getText().toString());
	       } // fine else
	   } // fine classe

}
