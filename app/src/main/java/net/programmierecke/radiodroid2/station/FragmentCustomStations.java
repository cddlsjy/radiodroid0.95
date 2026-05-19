package net.programmierecke.radiodroid2.station;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.programmierecke.radiodroid2.ActivityMain;
import net.programmierecke.radiodroid2.CustomStationManager;
import net.programmierecke.radiodroid2.FragmentBase;
import net.programmierecke.radiodroid2.R;
import net.programmierecke.radiodroid2.RadioDroidApp;
import net.programmierecke.radiodroid2.StationUpdateListener;
import net.programmierecke.radiodroid2.Utils;
import net.programmierecke.radiodroid2.station.DataRadioStation;

import java.util.List;

public class FragmentCustomStations extends FragmentBase implements StationUpdateListener {
    private static final String TAG = "FragmentCustomStations";

    private RecyclerView recyclerViewStations;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyView;

    private CustomStationManager customStationManager;
    private ItemAdapterStation stationListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        
        View view = inflater.inflate(R.layout.fragment_stations, container, false);

        recyclerViewStations = view.findViewById(R.id.recyclerViewStations);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        
        emptyView = view.findViewById(R.id.textErrorMessage);
        if (emptyView == null) {
            emptyView = new TextView(getContext());
        }

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewStations.setLayoutManager(llm);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewStations.getContext(), llm.getOrientation());
        recyclerViewStations.addItemDecoration(dividerItemDecoration);

        swipeRefreshLayout.setOnRefreshListener(this::loadStations);

        customStationManager = new CustomStationManager(getContext());
        customStationManager.addStationUpdateListener(this);

        FragmentActivity activity = getActivity();
        if (activity != null) {
            stationListAdapter = new ItemAdapterStation(activity, R.layout.list_item_station);
            stationListAdapter.setStationActionsListener(new ItemAdapterStation.StationActionsListener() {
                @Override
                public void onStationClick(DataRadioStation station, int pos) {
                    Log.d(TAG, "onStationClick: " + station.Name);
                    ActivityMain mainActivity = (ActivityMain) getActivity();
                    if (mainActivity != null) {
                        RadioDroidApp radioDroidApp = (RadioDroidApp) mainActivity.getApplication();
                        Utils.showPlaySelection(radioDroidApp, station, mainActivity.getSupportFragmentManager());
                    }
                }

                @Override
                public void onStationMoved(int from, int to) {
                }

                @Override
                public void onStationSwiped(DataRadioStation station) {
                }

                public void onStationLongClick(DataRadioStation station, int pos) {
                }

                @Override
                public void onStationMoveFinished() {
                }
            });
            recyclerViewStations.setAdapter(stationListAdapter);
        }

        loadStations();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (customStationManager != null) {
            customStationManager.removeStationUpdateListener(this);
        }
    }

    private void loadStations() {
        Log.d(TAG, "loadStations");
        
        List<DataRadioStation> stations = customStationManager.getList();
        
        if (stations.isEmpty()) {
            recyclerViewStations.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(R.string.empty_custom_stations);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerViewStations.setVisibility(View.VISIBLE);
            
            if (stationListAdapter != null) {
                stationListAdapter.updateList(null, stations);
            }
        }
        
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onStationListUpdated() {
        Log.d(TAG, "onStationListUpdated");
        if (isCreated()) {
            loadStations();
        }
    }

    public CustomStationManager getCustomStationManager() {
        return customStationManager;
    }
}