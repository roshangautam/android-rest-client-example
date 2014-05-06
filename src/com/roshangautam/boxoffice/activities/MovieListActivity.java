package com.roshangautam.boxoffice.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.loaders.ModelLoader;
import com.roshangautam.boxoffice.R;
import com.roshangautam.boxoffice.accounts.GuestAccount;
import com.roshangautam.boxoffice.adapters.MoviesAdapter;
import com.roshangautam.boxoffice.application.BoxOfficeApplication;
import com.roshangautam.boxoffice.models.Movie;
import com.roshangautam.boxoffice.resources.Constants;
import com.roshangautam.boxoffice.resources.Utils;

public class MovieListActivity extends FragmentActivity {
	
	AccountManager mAccountManager;
	Account mAccount;
	private ListView lvMovies;
	private MoviesAdapter moviesAdapter;
	MovieLoader movieLoader;
	ProgressDialog mProgressDialog;
	
	
	public static final String MOVIE_DETAIL_KEY = "movie";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_list);
		mAccountManager = (AccountManager) this.getSystemService(ACCOUNT_SERVICE);
		mAccount = new Account("guest", "com.roshangautam.boxoffice.accounts.guestaccount");
		ContentResolver.setSyncAutomatically(mAccount, BoxOfficeApplication.AUTHORITY, true);		
		ContentResolver.setIsSyncable(mAccount, BoxOfficeApplication.AUTHORITY, 1);
		mAccountManager.setAuthToken(mAccount, GuestAccount.AUTHTOKEN_TYPE_GUEST_ACCESS, GuestAccount.AUTHTOKEN_TYPE_GUEST_ACCESS_LABEL);
		mAccountManager.addAccountExplicitly(mAccount, null, null);
		
        lvMovies = (ListView)findViewById(R.id.lvMovies);
        
        ArrayList<Movie> aMovies = new ArrayList<Movie>();
        moviesAdapter = new MoviesAdapter(this, aMovies);
        lvMovies.setAdapter(moviesAdapter);
        movieLoader = new MovieLoader();
    	getSupportLoaderManager().initLoader(0, null, movieLoader);      	
		
        setupMovieSelectedListener();
        if (Utils.isNetworkAvailable(this)) {
        	syncToServer();	
		} else {
			Toast.makeText(this, "No network connection found. Please make sure that you have your wifi or celluar data enabled. You will not receive any updates until you resolve this situation.", Toast.LENGTH_LONG).show();
		}
        
	}
	
	private void syncToServer() {
		Bundle settingsBundle = new Bundle();
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		settingsBundle.putInt(Constants.SYNC_TYPE, 0);
		settingsBundle.putLong("limit", 50);
		settingsBundle.putString("contentUri", createUri());
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Loading ..");
		mProgressDialog.setMessage("Loading Movies for the first time. Please be patient");
		mProgressDialog.setCancelable(false);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.show();	
		ContentResolver.requestSync(mAccount, BoxOfficeApplication.AUTHORITY, settingsBundle);		
	}

	public String createUri()
	{
		StringBuilder uri = new StringBuilder();
		uri.append("content://");
		uri.append(getPackageName());
		uri.append("/");
		return uri.toString();
	}
	
    public void setupMovieSelectedListener() {
        lvMovies.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View item, int position, long rowId) {
                // Launch the detail view passing movie as an extra
                Intent i = new Intent(MovieListActivity.this, MovieDetailsActivity.class);
                Movie movie = moviesAdapter.getItem(position);
                i.putExtra(MOVIE_DETAIL_KEY, movie);
                startActivity(i);
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
            }
         });
     } 
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
    }
  

    public class CustomScoreComparator implements Comparator<Movie> {

    	@Override
    	public int compare(Movie movie1, Movie movie2) {
    		final int score1 = movie1.criticsScore;
    		final int score2 = movie2.criticsScore;
    		return Double.compare(score2, score1);
    	}

    }
    
    public class CustomDateComparator implements Comparator<Movie> {

    	@Override
    	public int compare(Movie movie1, Movie movie2) {
    		Calendar calendar1 = Calendar.getInstance();
    		calendar1.setTime(movie2.releasedDate);
    		Calendar calendar2 = Calendar.getInstance();
    		calendar2.setTime(movie1.releasedDate);
    		return new Date(calendar1.getTimeInMillis()).compareTo(new Date(
    				calendar2.getTimeInMillis()));
    	}

    }

    public class MovieLoader implements LoaderManager.LoaderCallbacks<List<Movie>> {

    	@Override
    	public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
    		return new ModelLoader<Movie>(MovieListActivity.this, Movie.class, true);
    	}

    	@Override
    	public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {

    		Log.i("DEBUG", "Fetched " + data.size());
			moviesAdapter.clear();
			moviesAdapter.addAll(data);
			moviesAdapter.notifyDataSetChanged();
    		if (mProgressDialog != null && !ContentResolver.isSyncActive(mAccount, BoxOfficeApplication.AUTHORITY)) {
				mProgressDialog.dismiss();
			}			
    	}

    	@Override
    	public void onLoaderReset(Loader<List<Movie>> arg0) {
			moviesAdapter.clear();
			moviesAdapter.notifyDataSetChanged();
    	}

    }
    
}
