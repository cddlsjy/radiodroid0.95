package net.programmierecke.radiodroid2;

import android.content.Context;

import net.programmierecke.radiodroid2.station.DataRadioStation;

public class CustomStationManager extends StationSaveManager {
    @Override
    protected String getSaveId() {
        return "custom_stations";
    }

    public CustomStationManager(Context ctx) {
        super(ctx);
    }

    @Override
    public void add(DataRadioStation station) {
        if (!has(station.StationUuid)) {
            super.add(station);
        }
    }
}