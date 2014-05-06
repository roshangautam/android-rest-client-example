package com.roshangautam.boxoffice.models;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.roshangautam.boxoffice.resources.Utils;

@Table(name = "Movies")
public class Movie extends Model implements Serializable{

	private static final long serialVersionUID = 4588793483902682046L;

	@Column(name = "MovieId", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	public long movieId;
	
	@Column(name = "Title")
	public String title;
	
	@Column(name = "Year")
	public int year;
	
	@Column(name = "ReleasedDate")
	public Date releasedDate;
	
	@Column(name = "Synopsis")
	public String synopsis;
	
	@Column(name = "ThumbnailPosterUrl")
	public String thumbnailPosterUrl;
	
	@Column(name = "ProfilePosterUrl")
	public String profilePosterUrl;
	
	@Column(name = "DetailedPosterUrl")
	public String detailedPosterUrl;
	
	@Column(name = "OriginalPosterUrl")
	public String originalPosterUrl;	
	
	@Column(name = "CriticsScore")
	public int criticsScore;
	
	@Column(name = "AudienceScore")
	public int audienceScore;
	
	@Column(name = "CriticsConsensus")
	public String criticsConsensus;	
	
	public ArrayList<Actor> movieActors;
	
	public Movie() {
		super();
	}
	
	public List<Actor> actors() {
		return getMany(Actor.class, "Movie");
	}
	
	public String getCastList() {
		String castList;
		ArrayList<String> cast = new ArrayList<String>();
		for (Actor actor : getActorsForThisMovie()) {
			cast.add(actor.name);
		}
		castList = TextUtils.join(", ", cast);
		return castList;
	}
	
	public void saveActors() {
		for (Actor actor: this.movieActors) {
			actor.save();
		}
	}

    public static Movie fromJson(JSONObject jsonObject)  {
    	Movie movie = new Movie();
        try {
        	movie.movieId = jsonObject.getLong("id");
        	movie.title = jsonObject.getString("title");
        	movie.year = jsonObject.getInt("year"); 
        	String releasedDate = null;
        	if (jsonObject.has("release_dates")) {
				if (jsonObject.getJSONObject("release_dates").has("theater")) {
					releasedDate = jsonObject.getJSONObject("release_dates").getString("theater");
				}
			}
        	movie.releasedDate  = Utils.convertToDate(releasedDate);
        	movie.synopsis = jsonObject.getString("synopsis");
			movie.thumbnailPosterUrl = jsonObject.getJSONObject("posters").getString("thumbnail");				
        	movie.profilePosterUrl = jsonObject.getJSONObject("posters").getString("profile");
        	movie.detailedPosterUrl = jsonObject.getJSONObject("posters").getString("detailed");
        	movie.originalPosterUrl = jsonObject.getJSONObject("posters").getString("original");
        	movie.criticsScore = jsonObject.getJSONObject("ratings").getInt("critics_score");
        	movie.audienceScore = jsonObject.getJSONObject("ratings").getInt("audience_score");
        	movie.criticsConsensus = jsonObject.has("critics_consensus") ? jsonObject.getString("critics_consensus") : ""; 
        	movie.movieActors = new ArrayList<Actor>();
            JSONArray abridgedCast = jsonObject.getJSONArray("abridged_cast");
            movie.movieActors =  Actor.fromJson(abridgedCast, movie);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
			e.printStackTrace();
		}
        return movie;
    }	
    
    public static ArrayList<Movie> fromJson(JSONArray jsonArray) {
        ArrayList<Movie> movies = new ArrayList<Movie>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject movieJSON = null;
            try {
            	movieJSON = jsonArray.getJSONObject(i);
                Movie movie = Movie.fromJson(movieJSON);
                if (movie != null) {
                    movies.add(movie);
                }            	
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return movies;
    }    
    
	public static List<Movie> getRecentMovies(Integer limit) {
		return new Select().from(Movie.class).orderBy("criticsScore ASC")
				.limit(limit.toString()).execute();
	}    
	
	public static List<Movie> getAllMovies() {
		return new Select().from(Movie.class).execute();
	}  
	
    public List<Actor> getActorsForThisMovie() {
        return new Select().from(Actor.class)
            .where("MovieId = ?", movieId)
            .execute();
    } 	
}
