package com.roshangautam.boxoffice.syncadapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SyncService extends Service {
    private static final String TAG = "SyncService";

    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter sSyncAdapter = null;
    
	@Override
	public IBinder onBind(Intent arg0) {
		  return sSyncAdapter.getSyncAdapterBinder();
	}
	
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }	
    
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service destroyed");
    }    

}
