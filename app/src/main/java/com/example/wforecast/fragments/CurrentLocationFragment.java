package com.example.wforecast.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.wforecast.Models.Hourly;
import com.example.wforecast.R;
import com.example.wforecast.utils.Common;
import com.example.wforecast.utils.ValueFormatter;
import com.example.wforecast.viewmodels.CurrentLocationViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;

import static android.content.Context.MODE_PRIVATE;

public class CurrentLocationFragment extends Fragment {

    private static final String TAG = "CurrentLocationFragment";
    private static final int LOCATION_REQUEST = 123;
    private CurrentLocationViewModel mViewModel;
    private TextView cityName, temperature, coordinates, description, feels_like, pressure, humidity, wind_speed, clouds, sunrise, sunset;
    private ImageView icon;
    private ProgressBar mProgressBar;
    private LineChart lineChart;
    private LocationManager locationManager;

    public static CurrentLocationFragment newInstance() {
        return new CurrentLocationFragment();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupLocation();
        Log.i(TAG, "onCreate: " + Common.CURRENT_LOCATION);

        mViewModel = new ViewModelProvider(this).get(CurrentLocationViewModel.class);
        mViewModel.updateHourlyForecast();

        mViewModel.getHourlyForecast().observe(this, message -> {

            // Graph setup

            Calendar now = Calendar.getInstance();
            int now_hour = now.get(Calendar.HOUR_OF_DAY);

            ArrayList<Entry> vals = new ArrayList<>();

            for (Hourly data : message.getHourly()) { // insert data from API

                String t = Common.convertUnixToDate(data.getDt());
                String[] ts = t.split(":");
                long hr = Long.parseLong(ts[0].trim());

                if (hr >= now_hour - 1 && hr <= now_hour + 5) { // range from now to +6 hours
                    vals.add(new Entry(hr, Math.round((float) data.getTemp())));
                } else {
                    break;
                }
            }

            LineDataSet dataSet = new LineDataSet(vals, "hourly");
            dataSet.setValueTextColor(getResources().getColor(R.color.White));
            dataSet.setValueTextSize(12);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSet.setDrawFilled(true);
            dataSet.setFillColor(getResources().getColor(R.color.White));

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet);

            LineData data = new LineData(dataSets);
            data.setValueFormatter(new ValueFormatter());

            // line chart customization
            lineChart.setData(data);

            configureLineChart();

            // Other components
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(Common.CURRENT_LOCATION.getLatitude(), Common.CURRENT_LOCATION.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String name = addresses.get(0).getLocality();
            String countryName = addresses.get(0).getCountryCode();

            cityName.setText(name + ", " + countryName);
            if (Common.UNITS.equals("metric")) {
                temperature.setText(Math.round(message.getCurrent().getTemp()) + "°C");
            } else if (Common.UNITS.equals("imperial")) {
                temperature.setText(Math.round(message.getCurrent().getTemp()) + "°F");
            }
            coordinates.setText(String.format("%s\" - %s\"", message.getLat(), message.getLon()));

            String desc = message.getCurrent().getWeather().get(0).getDescription();
            desc = desc.substring(0, 1).toUpperCase() + desc.substring(1);
            description.setText(String.format("%s", desc));
            wind_speed.setText(String.format("%s mph", message.getCurrent().getWind_speed()));

            OkHttpClient.Builder builderPicasso = new OkHttpClient.Builder()
                    .protocols(Collections.singletonList(Protocol.HTTP_1_1));

            final Picasso picasso = new Picasso.Builder(requireContext())
                    .downloader(new com.squareup.picasso.OkHttp3Downloader(builderPicasso.build()))
                    .listener((picasso1, uri, exception) -> Log.e(TAG, exception.getMessage()))
                    .build();

            String URL = "http://openweathermap.org/img/w/" + message.getCurrent().getWeather().get(0).getIcon() + ".png";
            picasso.setLoggingEnabled(true);
            picasso.load(URL).memoryPolicy(MemoryPolicy.NO_CACHE).into(icon);

            feels_like.setText(Math.round(message.getCurrent().getFeels_like()) + "°");
            feels_like.setText(Math.round(message.getCurrent().getFeels_like()) + "°");
            pressure.setText(message.getCurrent().getPressure() + " hPa");
            humidity.setText(message.getCurrent().getHumidity() + "%");
            clouds.setText(message.getCurrent().getClouds() + "%");

            sunrise.setText(Common.convertUnixToDate(message.getCurrent().getSunrise()));
            sunset.setText(Common.convertUnixToDate(message.getCurrent().getSunset()));
        });

        mViewModel.isLoading().observe(this, isLoading -> {
            int visibility = isLoading ? View.VISIBLE : View.INVISIBLE;
            if (mProgressBar != null) {
                mProgressBar.setVisibility(visibility);
            }
        });

    }

    private void configureLineChart() {
        lineChart.invalidate();
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.getAxisRight().setDrawLabels(false); // remove y axis labels
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getDescription().setEnabled(false); // remove description
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); // positioning x axis
        lineChart.getXAxis().setTextColor(getResources().getColor(R.color.White)); // text color
        lineChart.getXAxis().setTextSize(12); // text size
        lineChart.setDrawBorders(false); // remove borders
        lineChart.getXAxis().setDrawGridLines(false); // remove background grid lines
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getXAxis().setLabelCount(6, true); // set x axis label count
        if(Common.UNITS.equals("metric")) {
            lineChart.getAxisLeft().setAxisMinimum(0); // set bounds
            lineChart.getAxisLeft().setAxisMaximum(25);
        } else {
            lineChart.getAxisLeft().setAxisMinimum(0); // set bounds
            lineChart.getAxisLeft().setAxisMaximum(70);
        }

        lineChart.getLegend().setEnabled(false);   // Hide legend
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_location, container, false);
        cityName = view.findViewById(R.id.cityName);
        temperature = view.findViewById(R.id.temperature);
        coordinates = view.findViewById(R.id.coordinates);
        icon = view.findViewById(R.id.icon);
        mProgressBar = view.findViewById(R.id.progressBar);
        description = view.findViewById(R.id.description);
        feels_like = view.findViewById(R.id.feels_like);
        pressure = view.findViewById(R.id.pressure);
        humidity = view.findViewById(R.id.humidity);
        wind_speed = view.findViewById(R.id.wind_speed);
        clouds = view.findViewById(R.id.clouds);
        sunrise = view.findViewById(R.id.sunrise);
        sunset = view.findViewById(R.id.sunset);
        wind_speed = view.findViewById(R.id.wind_speed);
        lineChart = view.findViewById(R.id.linechart);
        return view;
    }


    private void requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {

            new AlertDialog.Builder(getActivity())
                    .setTitle("Permission needed")
                    .setMessage("Permission for coarse location and fine location")
                    .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_REQUEST))
                    .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss()).create().show();

        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
        }
        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        assert locationManager != null;
        Log.i(TAG, "setupLocation: " + LocationManager.PASSIVE_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Common.CURRENT_LOCATION = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
        Common.CURRENT_LOCATION = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("unit", Common.UNITS);
        editor.apply();

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPreferences", MODE_PRIVATE);
        Common.UNITS = prefs.getString("unit", "metric");
    }

}