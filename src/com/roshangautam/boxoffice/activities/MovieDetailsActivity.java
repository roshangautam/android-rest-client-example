package com.roshangautam.boxoffice.activities;


import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.roshangautam.boxoffice.R;
import com.roshangautam.boxoffice.models.Movie;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends ActionBarActivity {

	private ImageView ivPosterImage;
	private TextView tvTitle;
	private TextView tvSynopsis;
	private TextView tvCast;
	private TextView tvAudienceScore;
	private TextView tvCriticsScore;
	private TextView tvCriticsConsensus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_details);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		ivPosterImage = (ImageView) findViewById(R.id.ivPosterImage);
	    tvTitle = (TextView) findViewById(R.id.tvTitle);
	    tvSynopsis = (TextView) findViewById(R.id.tvSynopsis);
	    tvCast = (TextView) findViewById(R.id.tvCast);
	    tvCriticsConsensus = (TextView) findViewById(R.id.tvCriticsConsensus);
	    tvAudienceScore =  (TextView) findViewById(R.id.tvAudienceScore);
	    tvCriticsScore = (TextView) findViewById(R.id.tvCriticsScore);
	    Movie movie = (Movie)getIntent().getSerializableExtra(MovieListActivity.MOVIE_DETAIL_KEY);
	    setTitle(movie.title);
	    loadMovie(movie);
	}
	
	public void loadMovie(Movie movie) {
		tvTitle.setText(movie.title);
		tvCriticsScore.setText(Html.fromHtml("<b>Critics Score:</b>" + movie.criticsScore + "%"));
		tvAudienceScore.setText(Html.fromHtml("<b>Audience Score:</b> " + movie.audienceScore + "%"));
        tvCast.setText(movie.getCastList());
        tvSynopsis.setText(Html.fromHtml("<b>Synopsis:</b> " + movie.synopsis));
        tvCriticsConsensus.setText(Html.fromHtml("<b>Consensus:</b> " + movie.criticsConsensus));
        Picasso.with(this).load(movie.detailedPosterUrl).
          placeholder(R.drawable.large_movie_poster).
          into(ivPosterImage);		
	}
	
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    	overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }    
}
