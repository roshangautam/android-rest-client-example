package com.roshangautam.boxoffice.adapters;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.roshangautam.boxoffice.R;
import com.roshangautam.boxoffice.models.Movie;
import com.squareup.picasso.Picasso;

public class MoviesAdapter extends ArrayAdapter<Movie> {

	int width, height;
	
	public MoviesAdapter(Context context, ArrayList<Movie> aMovies) {
		super(context,	0,  aMovies);
		checkScreenSize(context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Movie movie = getItem(position);

		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.movie_cell, parent, false);
		}
		
		TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
		TextView tvCriticsScore = (TextView) convertView.findViewById(R.id.tvCriticsScore);
        TextView tvCast = (TextView) convertView.findViewById(R.id.tvCast);
        ImageView ivPosterImage = (ImageView) convertView.findViewById(R.id.ivPosterImage);
        // Populate the data into the template view using the data object
        tvTitle.setText(movie.title);
        tvCriticsScore.setText("Score: " + movie.criticsScore + "%");
        tvCast.setText(movie.getCastList());
        if (width > 1000) {
        	Picasso.with(getContext()).load(movie.detailedPosterUrl).into(ivPosterImage);
		} else {
			Picasso.with(getContext()).load(movie.profilePosterUrl).into(ivPosterImage);
		}
        
        // Return the completed view to render on screen
        return convertView;
	}
	
    @SuppressWarnings("deprecation")	
    @TargetApi(19)
    private void checkScreenSize(Context context) {
    	WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
    	Display display = wm.getDefaultDisplay();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			width = size.x;
			height = size.y;        	
		} else {
            width = display.getWidth();  // deprecated
            height = display.getHeight();  // deprecated	
		}   	
    }
	
}
