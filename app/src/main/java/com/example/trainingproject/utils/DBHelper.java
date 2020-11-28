package com.example.trainingproject.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.trainingproject.models.Movie;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String FAVOURITES_TABLE_NAME = "favourite_movies";
    public static final String COLUMN_ID ="id";
    public static final String MOVIE_ID = "movieid";
    public static final String MOVIE_TITLE = "title";
    public static final String MOVIE_RELEASE_DATE = "releasedate";
    public static final String MOVIE_LENGTH = "length";
    public static final String MOVIE_RATING = "rating";
    public static final String AVERAGE_MOVIE_RATING = "avgrating";
    private static final String TAG = "DBHelper";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+FAVOURITES_TABLE_NAME+" ("
                +MOVIE_ID+" INTEGER PRIMARY KEY, "
                +COLUMN_ID+" INTEGER, "
                +MOVIE_TITLE+" VARCHAR, " +
                ""+MOVIE_RELEASE_DATE
                +" VARCHAR, "+MOVIE_LENGTH
                +" INTEGER, "+MOVIE_RATING
                +" VRACHAR, "+AVERAGE_MOVIE_RATING
                +" VARCHAR)");
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+FAVOURITES_TABLE_NAME);
        onCreate(db);
    }

    public void insertMovie(int id, Movie movie){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        contentValues.put(MOVIE_ID,movie.getMovieid());
        contentValues.put(COLUMN_ID, id);
        contentValues.put(MOVIE_TITLE, movie.getTitle());
        contentValues.put(MOVIE_RELEASE_DATE, movie.getReleasedate());
        contentValues.put(MOVIE_LENGTH, movie.getLength());
        contentValues.put(MOVIE_RATING, movie.getRating());
        contentValues.put(AVERAGE_MOVIE_RATING, movie.getAvgrating());
        db.insert(FAVOURITES_TABLE_NAME, null, contentValues);

    }

    public void deleteMovie (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FAVOURITES_TABLE_NAME,
                MOVIE_ID + " = ? ",
                new String[]{Integer.toString(id)});
    }

    public List<Movie> getMovieList(){
        List<Movie> favouriteMovies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+FAVOURITES_TABLE_NAME, null );
        res.moveToFirst();
        while(!res.isAfterLast()) {
            favouriteMovies.add(new Movie(res.getInt(res.getColumnIndex(MOVIE_ID)),
                    res.getString(res.getColumnIndex(MOVIE_TITLE)), null, null,
                    res.getString(res.getColumnIndex(MOVIE_RELEASE_DATE)),
                    res.getInt(res.getColumnIndex(MOVIE_LENGTH)),
                    res.getString(res.getColumnIndex(MOVIE_RATING)),
                    res.getString(res.getColumnIndex(AVERAGE_MOVIE_RATING)), null, null));
            Log.i(TAG, String.valueOf(res.getInt(res.getColumnIndex(COLUMN_ID))));
            res.moveToNext();
        }
        res.close();
        return favouriteMovies;
    }

    public boolean isPresent(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM "+FAVOURITES_TABLE_NAME+" WHERE "+MOVIE_ID+"="+id+"", null );

        if(res.getCount()<=0){
            res.close();
            return false;
        }else{
            return true;
        }
    }

    public void emptyTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM "+FAVOURITES_TABLE_NAME);
    }

    public int getId(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM "+FAVOURITES_TABLE_NAME+" WHERE "+MOVIE_ID+"="+id, null );
        if(res.getCount()>0){
            res.moveToFirst();
            int columnId=  res.getInt(res.getColumnIndex(COLUMN_ID));
            res.close();
            return columnId;

        }else{
           return -1;
        }

    }

}
