package net.programmierecke.radiodroid2;

import android.content.Context;
import android.util.Log;

public class CustomStationManager extends StationSaveManager {
    private static final String TAG = "CustomStationManager";
    
    private static final String CUSTOM_STATIONS_KEY = "custom_stations";

    public CustomStationManager(Context ctx) {
        super(ctx);
        Log.d(TAG, "CustomStationManager initialized");
    }

    @Override
    protected String getSaveId() {
        return CUSTOM_STATIONS_KEY;
    }
}