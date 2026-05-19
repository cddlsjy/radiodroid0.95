package net.programmierecke.radiodroid2;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import net.programmierecke.radiodroid2.station.DataRadioStation;
import net.programmierecke.radiodroid2.service.PlayerServiceUtil;

import java.util.List;

public class FavouriteListAdapter extends RecyclerView.Adapter<FavouriteListAdapter.FavouriteViewHolder> {

    interface OnStationClickListener {
        void onStationClick(DataRadioStation station);
    }

    static class FavouriteViewHolder extends RecyclerView.ViewHolder {
        final View rootView;
        final ImageView imageViewIcon;
        final TextView textViewName;

        FavouriteViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            imageViewIcon = itemView.findViewById(R.id.imageViewStationIcon);
            textViewName = itemView.findViewById(R.id.textViewStationName);
        }
    }

    private Context context;
    private List<DataRadioStation> stations;
    private OnStationClickListener listener;
    private boolean shouldLoadIcons;
    private Drawable stationImagePlaceholder;

    public FavouriteListAdapter(Context context, OnStationClickListener listener) {
        this.context = context;
        this.listener = listener;
        stationImagePlaceholder = AppCompatResources.getDrawable(context, R.drawable.ic_photo_24dp);
    }

    public void setStations(List<DataRadioStation> stations) {
        this.stations = stations;
        shouldLoadIcons = Utils.shouldLoadIcons(context);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_favourite_compact, parent, false);
        return new FavouriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        DataRadioStation station = stations.get(position);

        holder.textViewName.setText(station.Name);
        holder.textViewName.setSelected(true);

        if (shouldLoadIcons) {
            holder.imageViewIcon.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(station.IconUrl)) {
                PlayerServiceUtil.getStationIcon(holder.imageViewIcon, station.IconUrl);
            } else {
                holder.imageViewIcon.setImageDrawable(stationImagePlaceholder);
            }
        } else {
            holder.imageViewIcon.setVisibility(View.GONE);
        }

        holder.rootView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStationClick(station);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stations != null ? stations.size() : 0;
    }
}