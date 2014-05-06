package com.roshangautam.boxoffice.application;

import android.app.Application;

import com.activeandroid.ActiveAndroid;


public class BoxOfficeApplication extends Application {

	public static final String AUTHORITY = "com.roshangautam.boxoffice";
	
	@Override
	public void onCreate() {
		super.onCreate();
		ActiveAndroid.initialize(this);
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		ActiveAndroid.dispose();
	}
}
