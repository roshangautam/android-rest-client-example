package com.roshangautam.boxoffice.syncadapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.roshangautam.boxoffice.models.Actor;
import com.roshangautam.boxoffice.models.Movie;
import com.roshangautam.boxoffice.restclient.RottenTomatoesClient;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
	public static final String TAG = "DEBUG";
	ContentResolver mContentResolver;
	Context mContext;
	RottenTomatoesClient client;
	ArrayList<Movie> fetchedMovies;
	String contentUri;
	
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mContext = context;
		mContentResolver = context.getContentResolver();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }	

	@Override
    public void onPerformSync(Account account, Bundle extras, String authority,
            ContentProviderClient provider, SyncResult syncResult) {
		Log.i(TAG, "Beginning network synchronization");
		int syncType = extras.getInt("syncType");
		long limit = extras.getLong("limit");
		contentUri = extras.getString("contentUri");
		Log.i(TAG, "Limit:" + limit);
		Log.i(TAG, "SyncType:" + syncType);
		
		switch (syncType) {
		case 0:
			updateLocalMovieData(limit, syncResult);
			break;
		default:
			Log.i(TAG, "Nothing to synchronize");
			break;
		}		
	}
	
	public void updateLocalMovieData(long limit, final SyncResult syncResult) {
		Log.i(TAG, "Requesting movies from server");
		Log.i(TAG, "Limit:" + limit);
		client = new RottenTomatoesClient();	
		client.getBoxOfficeMovies(limit, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int code, JSONObject body) {
				try {
                	final JSONArray items = body.getJSONArray("movies");
                    Log.i(TAG, "Found " + items.length() + " movies");   
                    fetchedMovies = Movie.fromJson(items);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFinish() {
				Log.d(TAG, "Finished Server Request");
				checkMovieData(fetchedMovies, syncResult);
			}
			
		});				
	}
	
	public void checkMovieData(ArrayList<Movie> fetchedMovies, SyncResult syncResult) {
        HashMap<Long,Movie> movieMap = new HashMap<Long, Movie>();
        for (Movie movie : fetchedMovies) {
            movieMap.put(movie.movieId, movie);
        }
    	Log.i(TAG, "Querying local movies for merge");
        List<Movie> existingMovies = Movie.getAllMovies();
        Log.i(TAG, "Found " + existingMovies.size() + " local movies. Computing merge solution...");
        ActiveAndroid.beginTransaction();
        try {	        
	        for (Movie movie : existingMovies) {
//	        	Log.i(TAG, "Checking Movie: " + movie.title);
	        	syncResult.stats.numEntries++;
	        	Movie match = movieMap.get(movie.movieId);
	        	if (match != null) {
//	        		Log.i(TAG, movie.title + " exists. Remove from entry map to prevent insert later");
	                movieMap.remove(movie.movieId);
	                if ((match.title != null && !match.title.equals(movie.title)) ||
	                		(match.year !=  movie.year) ||
	                		(match.releasedDate != null && !match.releasedDate.equals(movie.releasedDate)) ||
	                		(match.criticsConsensus != null && !match.criticsConsensus.equals(movie.criticsConsensus)) ||
	                		(match.criticsScore != movie.criticsScore) ||
	                		(match.audienceScore != movie.audienceScore) ||
	                		(match.synopsis != null && !match.synopsis.equals(movie.synopsis)) ||
	                		(match.thumbnailPosterUrl != null && !match.thumbnailPosterUrl.equals(movie.thumbnailPosterUrl)) ||
	                		(match.profilePosterUrl != null && !match.profilePosterUrl.equals(movie.profilePosterUrl)) ||
	                		(match.detailedPosterUrl != null && !match.detailedPosterUrl.equals(movie.detailedPosterUrl)) ||
	                		(match.originalPosterUrl != null && !match.originalPosterUrl.equals(movie.originalPosterUrl))
	                		) {
	                	// Update existing record
//	                	Log.i(TAG, "Updating Movie: " + match.title);
	                	match.save();	                	
					} else {
//	                    Log.i(TAG, "No action Required for: " + match.title);
	                }
	                
	                
	                ArrayList<Actor> fetchedActors = match.movieActors;
//	                Log.i(TAG, "Fetched " + fetchedActors.size() + " actors");  
	                HashMap<String,Actor> actorMap = new HashMap<String, Actor>();
	                for (Actor actor: fetchedActors) {
//	                	Log.i(TAG, "Fetched actor:" + actor.name);
	                    actorMap.put(actor.name, actor);
	                }   
	                
                    List<Actor> existingActors = match.getActorsForThisMovie();
//	                Log.i(TAG, "Found " + existingActors.size() + " actors in this movie. Computing merge solution...");
	                for (Actor actor : existingActors) {
						Actor actorMatch = actorMap.get(actor.name);
						if (actorMatch != null) {
							actorMap.remove(actor.name);
							if ((actorMatch.name != null && !actorMatch.name.equals(actor.name))) {
//								Log.i(TAG, "Updating Actor: " + actorMatch.name);
								actorMatch.save();
							} else {
//								Log.i(TAG, "No action Required for: " + actorMatch.name);
							}
						} else {
//							Log.i(TAG, "Deleting actor:" + actor.name);
							actor.delete();
						}
					}	 
	                for (Actor actor : actorMap.values()) {
//	                	Log.i(TAG, "Adding actor:" + actor.name);
						actor.save();
					}

				} else {
//					Log.i(TAG, "Deleting Movie: " + movie.title);
					movie.delete();
					syncResult.stats.numDeletes++;
				}
			}
	        ActiveAndroid.setTransactionSuccessful();
        } finally {
        	ActiveAndroid.endTransaction();
        }
        ActiveAndroid.beginTransaction();
        try {	        
        // Add new items
	        for (Movie movie : movieMap.values()) {
//	        	Log.i(TAG, "Adding new movie:" + movie.title);
				movie.save();
                ArrayList<Actor> fetchedActors = movie.movieActors;
                for (Actor actor: fetchedActors) {
//                	Log.i(TAG, "Adding new actor:" + actor.name + " for movie:" + movie.title + " with movieid:" + actor.movieId);
                    actor.save();
                }  				
				syncResult.stats.numInserts++;
			}
	        ActiveAndroid.setTransactionSuccessful();
        } finally {
        	ActiveAndroid.endTransaction();
        }
        mContentResolver.notifyChange(
                Uri.parse(contentUri), // URI where data was modified
                null,                           // No local observer
                false);          
	}
}
