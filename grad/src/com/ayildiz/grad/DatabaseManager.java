package com.ayildiz.grad;

import java.util.ArrayList;
import java.util.List;

import com.ayildiz.model.Building;
import com.ayildiz.model.Distance;
import com.ayildiz.model.Place;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "gradDatabase";
	
	private static final String BUILDINGS = "buildings";
	private static final String PLACES = "places";
	private static final String DISTANCES = "distances";
	
	private static final String BUILDINGS_ID = "id";
	private static final String BUILDINGS_EXPLANATION = "explanation";
	private static final String PLACES_ID = "id";
	private static final String PLACES_BUILDING_ID = "building_id";
	private static final String PLACES_IMAGE = "image";
	private static final String PLACES_FLOOR = "floor";
	private static final String PLACES_EXPLANATION = "explanation";
	private static final String DISTANCES_FROM = "from";
	private static final String DISTANCES_TO = "to";
	private static final String DISTANCES_COST = "cost";
	private static final String DISTANCES_DIRECTION = "direction";
	
	public DatabaseManager(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_BUILDINGS = "CREATE TABLE " + BUILDINGS + "(" +
				BUILDINGS_ID + " INTEGER PRIMARY KEY," +
				BUILDINGS_EXPLANATION + " TEXT)";
		
		String CREATE_PLACES = "CREATE TABLE " + PLACES + "(" +
				PLACES_ID + " INTEGER PRIMARY KEY," +
				PLACES_BUILDING_ID + " INTEGER," +
				PLACES_IMAGE + " BLOB," +
				PLACES_FLOOR + " INTEGER," +
				PLACES_EXPLANATION + " TEXT)";
		
		String CREATE_DISTANCES = "CREATE TABLE " + DISTANCES + "(" +
				DISTANCES_FROM + " INTEGER PRIMARY KEY," +
				DISTANCES_TO + " INTEGER PRIMARY KEY," +
				DISTANCES_COST + " INTEGER," +
				DISTANCES_DIRECTION + " TEXT)";
		
		db.execSQL(CREATE_BUILDINGS);
		db.execSQL(CREATE_PLACES);
		db.execSQL(CREATE_DISTANCES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion != newVersion){
			db.execSQL("DROP TABLE IF EXISTS " + BUILDINGS);
			db.execSQL("DROP TABLE IF EXISTS " + PLACES);
			db.execSQL("DROP TABLE IF EXISTS " + DISTANCES);
			onCreate(db);
		}
	}
	
	public List<Building> getBuildings(){
		List<Building> buildings = new ArrayList<Building>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from buildings", null);
		
		try{
			if(cursor.moveToFirst()){
				do{
					Building building = new Building();
					building.Id = cursor.getInt(cursor.getColumnIndex(BUILDINGS_ID));
					building.Explanation = cursor.getString(cursor.getColumnIndex(BUILDINGS_EXPLANATION));
					buildings.add(building);
				} while(cursor.moveToNext());
			}
		}
		catch(Exception ex){
			Log.e(DATABASE_NAME, "getBuildings");
			ex.printStackTrace();
		}
		finally{
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
			}
		}
		
		return buildings;
	}
	
	public List<Place> getPlaces(long buildingId){
		List<Place> places = new ArrayList<Place>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from places where building_id = ?", new String[] { String.valueOf(buildingId) });
		
		try{
			if(cursor.moveToFirst()){
				do{
					Place place= new Place();
					place.Id = cursor.getInt(cursor.getColumnIndex(PLACES_ID));
					place.BuildingId = cursor.getInt(cursor.getColumnIndex(PLACES_BUILDING_ID));
					place.Image = cursor.getBlob(cursor.getColumnIndex(PLACES_IMAGE));
					place.Floor = cursor.getInt(cursor.getColumnIndex(PLACES_FLOOR));
					place.Explanation = cursor.getString(cursor.getColumnIndex(PLACES_EXPLANATION));
					places.add(place);
				} while(cursor.moveToNext());
			}
		}
		catch(Exception ex){
			Log.e(DATABASE_NAME, "getPlaces");
			ex.printStackTrace();
		}
		finally{
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
			}
		}
		
		return places;
	}
	
	public List<Distance> getDistances(){
		List<Distance> distances = new ArrayList<Distance>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from distances", null);
		
		try{
			if(cursor.moveToFirst()){
				do{
					Distance distance = new Distance();
					distance.From = cursor.getInt(cursor.getColumnIndex(DISTANCES_FROM));
					distance.To = cursor.getInt(cursor.getColumnIndex(DISTANCES_TO));
					distance.Cost = cursor.getInt(cursor.getColumnIndex(DISTANCES_COST));
					distance.Direction = cursor.getString(cursor.getColumnIndex(DISTANCES_DIRECTION));
					distances.add(distance);
				} while(cursor.moveToNext());
			}
		}
		catch(Exception ex){
			Log.e(DATABASE_NAME, "getDistances");
			ex.printStackTrace();
		}
		finally{
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
			}
		}
		
		return distances;
	}
}
