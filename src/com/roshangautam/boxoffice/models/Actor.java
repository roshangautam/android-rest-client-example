package com.roshangautam.boxoffice.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Actors")
public class Actor extends Model implements Serializable {

	private static final long serialVersionUID = -3382193969606715934L;

	@Column(name = "Name")
	public String name;
	
	@Column(name = "MovieId")
	public long movieId;
	
	@Column(name = "Movie", onDelete = ForeignKeyAction.CASCADE)
	public Movie movie;

    public static Actor fromJson(JSONObject jsonObject, Movie movie) {
    	Actor actor = new Actor();
        try {
        	actor.name = jsonObject.getString("name");
        	actor.movieId = movie.movieId;
        	actor.movie = movie;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return actor;
    }	
    
    public static ArrayList<Actor> fromJson(JSONArray jsonArray, Movie movie) {
        ArrayList<Actor> actors = new ArrayList<Actor>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject actorJSON = null;
            try {
            	actorJSON = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Actor actor = Actor.fromJson(actorJSON, movie);
            if (actor != null) {
            	actors.add(actor);
            }
        }
        return actors;
    }  
    
	public static List<Actor> getAllActors() {
		return new Select().from(Actor.class).execute();
	}    
       
}
