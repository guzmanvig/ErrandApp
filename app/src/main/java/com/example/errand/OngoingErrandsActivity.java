package com.example.errand;

import androidx.annotation.NonNull;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OngoingErrandsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private boolean mLocationPermissionGranted;

    private static final String TAG = OngoingErrandsActivity.class.getSimpleName();
    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Database database;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    private Map<String, ModelErrandOngoing> markersMap = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing_errands_map);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        checkLocationPermission();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        database = new Database();
    }

    private void checkLocationPermission() {

        mLocationPermissionGranted = ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        getNearbyErrands();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updateNearbyErrands(List<ModelErrandOngoing> errandsList) {
        int height = 100;
        int width = 100;
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.map_icon);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        for (ModelErrandOngoing model : errandsList) {

            double latitude = model.getVolunteerPosition().getLatitude();
            double longitude = model.getVolunteerPosition().getLongitude();
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .icon(smallMarkerIcon));

            markersMap.put(marker.getId(), model);

        }

        /*HashMap<String, Pair<Float, Float>> map = getNearbyErrands();
        for (Map.Entry<String, Pair<Float, Float>> entry : map.entrySet()) {
            String name =  entry.getKey();
            Pair<Float, Float> pair = entry.getValue();

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(pair.first, pair.second))
                    .icon(smallMarkerIcon));
            markersMap.put(marker.getId(), name);

        } */

    }

    private void getNearbyErrands() {

        database.retreiveOngoingErrands(new DatabaseListener() {
            @Override
            public void onOngoingErrandsFetchComplete(List<ModelErrandOngoing> list) {
                updateNearbyErrands(list);
            }

            @Override
            public void onOngoingRequestsFetchComplete(List<ModelErrandRequest> list) {}
        });

        /*HashMap<String, Pair<Float, Float>> map = new HashMap<>();
        map.put("Mike", new Pair<>(37.426f,  -121.888f));
        map.put("Tom", new Pair<>(37.423f, -121.886f));
        map.put("Susan", new Pair<>(37.417f, -121.885f));
        return map;*/
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        ModelErrandOngoing model = markersMap.get(marker.getId());
        FragmentManager fm = getSupportFragmentManager();
        RequestErrandDialog alertDialog = new RequestErrandDialog(model);
        alertDialog.show(fm, "map_dialog");
        return false;
    }
}
