package com.example.wforecast.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wforecast.Models.MessageList;
import com.example.wforecast.R;
import com.example.wforecast.utils.Common;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

@Setter
public class FavoriteCoordinatesAdapter extends RecyclerView.Adapter<FavoriteCoordinatesAdapter.ViewHolder> {

    private static final String TAG = "FavoriteCoordinatesAdapter";
    private MessageList messages;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onRemoveClickListener(int position) throws ExecutionException, InterruptedException;
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public FavoriteCoordinatesAdapter(MessageList messages, Context context) {
        this.messages = new MessageList();
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public FavoriteCoordinatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.favorite_city_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FavoriteCoordinatesAdapter.ViewHolder holder, int position) {
        if (messages != null && position <= messages.size()) {
            if (Common.UNITS == "metric") {
                holder.temperature.setText(Math.round(messages.getList().get(position).getMain().getTemp()) + "°C");
                holder.feelsLike.setText(Math.round(messages.getList().get(position).getMain().getFeels_like()) + "°C");
            }
            if (Common.UNITS == "imperial") {
                holder.temperature.setText(Math.round(messages.getList().get(position).getMain().getTemp()) + "°F");
                holder.feelsLike.setText(Math.round(messages.getList().get(position).getMain().getFeels_like()) + "°F");
            }
            holder.clouds.setText(messages.getList().get(position).getClouds().getAll() + "%");

            holder.description.setText(String.valueOf(messages.getList().get(position).getWeather().get(0).getDescription()));
            holder.cityName.setText(String.valueOf(messages.getList().get(position).getName()));
            OkHttpClient.Builder builderPicasso = new OkHttpClient.Builder()
                    .protocols(Collections.singletonList(Protocol.HTTP_1_1));

            final Picasso picasso = new Picasso.Builder(Objects.requireNonNull(context))
                    .downloader(new com.squareup.picasso.OkHttp3Downloader(builderPicasso.build()))
                    .listener((picasso1, uri, exception) -> Log.e("Picasso", exception.getMessage()))
                    .build();

            String URL = "http://openweathermap.org/img/w/" + messages.getList().get(position).getWeather().get(0).getIcon() + ".png";
            picasso.setLoggingEnabled(true);
            picasso.load(URL).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.icon);
        }
    }

    @Override
    public int getItemCount() {
        if (messages == null) {
            messages = new MessageList();
            return 0;
        }
        return messages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView description;
        TextView feelsLike;
        TextView clouds;
        TextView temperature;
        TextView cityName;
        Button removeButton;

        ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            icon = itemView.findViewById(R.id.cardIcon);
            description = itemView.findViewById(R.id.cardDescription);
            feelsLike = itemView.findViewById(R.id.cardFeelsLikeValues);
            clouds = itemView.findViewById(R.id.cardCloudsValue);
            temperature = itemView.findViewById(R.id.cardTemperature);
            cityName = itemView.findViewById(R.id.cardCityName);
            removeButton = itemView.findViewById(R.id.removeButton);

            removeButton.setOnClickListener(v -> {
                if (listener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        try {
                            listener.onRemoveClickListener(pos);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        messages.getList().remove(pos);
                    }
                }
            });
        }
    }
}
