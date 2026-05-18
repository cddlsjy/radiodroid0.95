package net.programmierecke.radiodroid2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.programmierecke.radiodroid2.station.ItemAdapterStation;
import net.programmierecke.radiodroid2.station.DataRadioStation;
import net.programmierecke.radiodroid2.station.ItemAdapterIconOnlyStation;
import net.programmierecke.radiodroid2.interfaces.IAdapterRefreshable;
import net.programmierecke.radiodroid2.station.StationActions;
import net.programmierecke.radiodroid2.station.StationsFilter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class FragmentCustomStations extends Fragment implements IAdapterRefreshable, Observer {
    private static final String TAG = "FragmentCustomStations";

    private RecyclerView rvStations;
    private SwipeRefreshLayout swipeRefreshLayout;

    private CustomStationManager customStationManager;

    void onStationClick(DataRadioStation theStation) {
        RadioDroidApp radioDroidApp = (RadioDroidApp) getActivity().getApplication();
        Utils.showPlaySelection(radioDroidApp, theStation, getActivity().getSupportFragmentManager());
    }

    public void RefreshListGui() {
        if (BuildConfig.DEBUG) Log.d(TAG, "refreshing the custom stations list.");

        ItemAdapterStation adapter = (ItemAdapterStation) rvStations.getAdapter();

        if (BuildConfig.DEBUG) Log.d(TAG, "custom stations count:" + customStationManager.listStations.size());

        adapter.updateList(this, customStationManager.listStations);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RadioDroidApp radioDroidApp = null;
        if (getActivity() != null) {
            radioDroidApp = (RadioDroidApp) getActivity().getApplication();
        }
        
        if (radioDroidApp == null) {
            Log.e(TAG, "Cannot get RadioDroidApp, Activity is null");
            return new View(getContext());
        }
        
        customStationManager = radioDroidApp.getCustomStationManager();
        customStationManager.addObserver(this);

        View view = inflater.inflate(R.layout.fragment_stations, container, false);
        rvStations = (RecyclerView) view.findViewById(R.id.recyclerViewStations);

        rvStations.setAdapter(null);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "onRefresh called from SwipeRefreshLayout");
                            }
                            RefreshDownloadList();
                        }
                    }
            );
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        if (getActivity() != null) {
            ItemAdapterStation adapter;
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
            if (sharedPref.getBoolean("load_icons", false) && sharedPref.getBoolean("icons_only_favorites_style", false)) {
                adapter = new ItemAdapterIconOnlyStation(getActivity(), R.layout.list_item_icon_only_station);
                Context ctx = getContext();
                DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
                int itemWidth = (int) ctx.getResources().getDimension(R.dimen.regular_style_icon_container_width);
                int noOfColumns = displayMetrics.widthPixels / itemWidth;
                GridLayoutManager glm = new GridLayoutManager(ctx, noOfColumns);
                rvStations.setAdapter(adapter);
                rvStations.setLayoutManager(glm);
            } else {
                adapter = new ItemAdapterStation(getActivity(), R.layout.list_item_station);
                LinearLayoutManager llm = new LinearLayoutManager(getContext());
                llm.setOrientation(RecyclerView.VERTICAL);

                rvStations.setAdapter(adapter);
                rvStations.setLayoutManager(llm);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvStations.getContext(),
                        llm.getOrientation());
                rvStations.addItemDecoration(dividerItemDecoration);
                adapter.enableItemMoveAndRemoval(rvStations);
            }

            adapter.setStationActionsListener(new ItemAdapterStation.StationActionsListener() {
                @Override
                public void onStationClick(DataRadioStation station, int pos) {
                    FragmentCustomStations.this.onStationClick(station);
                }

                @Override
                public void onStationSwiped(final DataRadioStation station) {
                    if (getContext() != null && getView() != null) {
                        customStationManager.remove(station.StationUuid);
                        customStationManager.Save();
                        customStationManager.notifyObservers();
                    }
                }

                @Override
                public void onStationMoved(int from, int to) {
                    customStationManager.moveWithoutNotify(from, to);
                }

                @Override
                public void onStationMoveFinished() {
                    if (getView() != null) {
                        getView().post(() -> {
                            customStationManager.Save();
                            customStationManager.notifyObservers();
                        });
                    }
                }
            });
            
            RefreshListGui();
        } else {
            Log.e(TAG, "Activity is null in onActivityCreated, cannot initialize adapter");
        }
    }

    void RefreshDownloadList(){
        RefreshListGui();
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        rvStations.setAdapter(null);

        RadioDroidApp radioDroidApp = null;
        if (getActivity() != null) {
            radioDroidApp = (RadioDroidApp) getActivity().getApplication();
        }
        
        if (radioDroidApp != null) {
            customStationManager = radioDroidApp.getCustomStationManager();
            customStationManager.deleteObserver(this);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        RefreshListGui();
    }
}