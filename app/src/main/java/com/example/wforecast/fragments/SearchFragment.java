package com.example.wforecast.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.wforecast.R;
import com.example.wforecast.utils.Common;
import com.example.wforecast.viewmodels.SearchViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class SearchFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SearchFragment";
    private SearchViewModel mViewModel;

    private SupportMapFragment fragment;
    private GoogleMap map;

    private GoogleApiClient mGoogleApiClient;
    private PlacesClient placesClient;
    private AutocompleteSupportFragment autocompleteFragment;

    private ImageView currentLoc;
    private FloatingActionButton addFav;
    private LatLng currentMarker;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        mViewModel.getCityInfo().observe(getActivity(), cityData ->
                mViewModel.addFavoriteCoordinate(cityData.getId(),
                        new LatLng(cityData.getCoord().getLat(),
                                cityData.getCoord().getLon())));

        Places.initialize(requireActivity(), Common.API_KEY_GOOGLE_MAPS);
        placesClient = Places.createClient(getActivity());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.getView().setBackgroundColor(Color.WHITE);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ID));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                map.clear();
                Toast.makeText(getContext(), "Searching...", Toast.LENGTH_SHORT).show();
                MarkerOptions markerOptions = new MarkerOptions();
                currentMarker = place.getLatLng();
                markerOptions.position(place.getLatLng());
                map.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                map.addMarker(markerOptions);
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        currentLoc = view.findViewById(R.id.currentLocationIcon);
        addFav = view.findViewById(R.id.addFavoritesIcon);

        addFav.setOnClickListener(v -> { //TODO update gui
            try {
                Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                mViewModel.updateCityInfo(currentMarker);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        currentLoc.setOnClickListener(v -> {
            map.clear();
            Toast.makeText(getContext(), "Searching..", Toast.LENGTH_SHORT).show();
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(Common.CURRENT_LOCATION.getLatitude(), Common.CURRENT_LOCATION.getLongitude());
            currentMarker = latLng;
            markerOptions.position(latLng);
            map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            map.addMarker(markerOptions);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(markerOptions.getPosition())
                    .zoom(1000).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        });

        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.googleMap);
        assert fragment != null;
        fragment.getMapAsync(googleMap -> {
            map = googleMap;
        });
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: connection failed");
    }
}


























