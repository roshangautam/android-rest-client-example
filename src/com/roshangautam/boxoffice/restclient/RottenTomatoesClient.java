package com.roshangautam.boxoffice.restclient;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RottenTomatoesClient {

		private final String API_KEY = "USE_YOUR_KEY";
		private final String API_BASE_URL = "http://api.rottentomatoes.com/api/public/v1.0/";
		private AsyncHttpClient client;
		
		public RottenTomatoesClient() {
			this.client = new AsyncHttpClient();
		}
		
		private String getApiUrl(String relativeUrl) {
			return API_BASE_URL + relativeUrl;
		}
		
		public void getBoxOfficeMovies(final Long limit, JsonHttpResponseHandler handler) {
			String url = getApiUrl("lists/movies/box_office.json");
			RequestParams params = new RequestParams("apikey", API_KEY);
			params.put("limit", limit.toString());
			params.put("country", "us");
			client.get(url, params, handler);
		}
}
