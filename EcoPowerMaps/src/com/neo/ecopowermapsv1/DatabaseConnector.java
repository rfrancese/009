package com.neo.ecopowermapsv1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseConnector {
	
	private SQLiteDatabase database; //oggetto database
	private DatabaseOpenHelper databaseOpen; //istanza che mi permette la creazione del db
	
	
	//Metodo che prende come parametro il context
	
	public DatabaseConnector(Context context){
		
		databaseOpen=new DatabaseOpenHelper(context,"Preferiti",null,1);

	}
	
	public void open(){
		
		//crea database per leggere o scrivere
		database=databaseOpen.getWritableDatabase();
	}
	
	//metodo per chiudere il database ed eliminare le risorse
	public void close(){
		
		if(database!=null)
			database.close();
	}
	
	//Query per inserire le coordinate preferite nel database
	public void insertFavorites(String value,String latitude, String longitude){
		
		 ContentValues newPref = new ContentValues();
		 newPref.put("nome", value);
		 newPref.put("latitudine", latitude);
	     newPref.put("longitudine", longitude);
	     open(); // apro il database
	     database.insert("favourite", null, newPref);
	     close(); // chiudo il database
		
	}//fine metodo inserimento preferito
	
	
	//metodo per aggiornare i preferiti
	public void updateContact(long id, String latitude, String longitude,String value)
		   {
		      ContentValues editPref = new ContentValues();
		      editPref.put("latitudine", latitude);
		      editPref.put("longitudine", longitude);
		      editPref.put("nome", value);

		      open(); // apro il database
		      database.update("favourite", editPref, "_id=" + id, null); //faccio la query
		      close(); // chiudo il database
		   } // fine metodo per aggiornare
	
	
	//metodo che ritorna un Cursor con tutti i preferiti
	   public Cursor getAllPref() 
	   {
	      return database.query("favourite", new String[] {"_id", "nome"}, null, null, null, null, "nome");
	   } // fine metodo che restituisce tutti i nomi dei preferiti


	// metodo che ritorna un Cursor su un preferito specifico dato l'id della ListView
	   public Cursor getOnePref(long id) 
	   {
	      return database.query("favourite", null, "_id=" + id, null, null, null, null);
	   } // fine metodo che restituisce un contatto

	 
	   
	   // metodo che cancella un preferito passando un id ListView
	   public void deletePref(long id) 
	   {
	      open(); // apro il database
	      database.delete("favourite", "_id=" + id, null);
	      close(); // chiudo il database
	   } // fine metodo deletePrefs
	   
	   
	  
	   //Classe che estende SQLiteOpenHelper e crea il database
	   
	   private class DatabaseOpenHelper extends SQLiteOpenHelper{
		   
		// costruttore
		  public DatabaseOpenHelper(Context context, String name, CursorFactory factory, int version) 
		      
		      {
		         super(context, name, factory, version);
		      
		      } //fine costruttore

		@Override
		public void onCreate(SQLiteDatabase db) {
			// in questo metodo devo creare la tabella
			// query to create a new table named contacts
	         String createQuery = "CREATE TABLE favourite" +
	            "(_id integer primary key autoincrement," +
	            "nome TEXT, latitudine TEXT, longitudine TEXT);";
	                  
	         db.execSQL(createQuery); // eseguo la query per la creazione della tabella
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
		}

	   }
	
	}
