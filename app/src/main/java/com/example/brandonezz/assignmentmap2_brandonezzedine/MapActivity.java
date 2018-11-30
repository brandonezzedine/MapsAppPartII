package com.example.brandonezz.assignmentmap2_brandonezzedine;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapActivity extends Activity implements OnMapReadyCallback {

    private final String LOG_MAP = "MAPS";

    private GoogleMap googleMap;
    private LatLng currentLatLng;
    private MapFragment mapFragment;
    private Marker currentMapMarker;
    private ArrayList<MapLocation> mapLocations;
    private IntentFilter intentFilter = null;
    private BroadtcastReceiverMap broadcastReceiverMap = null;
    public ArrayList<MapLocation> arrayLocations = new ArrayList<MapLocation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("location");

        intentFilter = new IntentFilter("com.example.brandonezz.assignmentmap2_brandonezzedine.action.NEW_MAP_LOCATION_BROADCAST");
        broadcastReceiverMap = new BroadtcastReceiverMap();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiverMap, intentFilter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(broadcastReceiverMap);
        super.onStop();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Intent intent = getIntent();
        Double latiude = intent.getDoubleExtra("LAT", Double.NaN);
        Double longitude = intent.getDoubleExtra("LON", Double.NaN);
        String location = intent.getStringExtra("LOC");

        currentLatLng = new LatLng(latiude, longitude);

        googleMap.addMarker(new MarkerOptions()
                .position(currentLatLng)
                .title(location)
        );

        mapCameraConfiguration(googleMap);
        useMapClickListener(googleMap);
        useMarkerClickListener(googleMap);
    }

    private void mapCameraConfiguration(GoogleMap googleMap) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLatLng)
                .zoom(10)
                .bearing(0)
                .build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        googleMap.animateCamera(cameraUpdate, 2000, new GoogleMap.CancelableCallback() {

            @Override
            public void onFinish() {
                Log.i(LOG_MAP, "onFinish is active");
            }

            @Override
            public void onCancel() {
                Log.i(LOG_MAP, "onCancel is active");
            }
        });
    }

    private void createCustomMapMarkers(GoogleMap googleMap, LatLng latlng, String title, String snippet) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng)
                .title(title)
                .snippet(snippet);

        currentMapMarker = googleMap.addMarker(markerOptions);

    }

    private void useMapClickListener(final GoogleMap googleMap) {

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latltn) {
                Log.i(LOG_MAP, "setOnMapClickListener");

                if (currentMapMarker != null) {
                    // Remove current marker from the map.
                    currentMapMarker.remove();
                }
                // The current marker is updated with the new position based on the click.
                createCustomMapMarkers(
                        googleMap,
                        new LatLng(latltn.latitude, latltn.longitude),
                        "New Marker",
                        "Listener onMapClick - new position"
                                + "Latitude: " + latltn.latitude
                                + "Longitude: " + latltn.longitude);
            }
        });
    }


    public void firebaseLoadData(GoogleMap googleMap) {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {


                    Double latitude = locationSnapshot.child("latitude").getValue(Double.class);

                    Double longitude = locationSnapshot.child("longitude").getValue(Double.class);

                    String location = locationSnapshot.child("location").getValue(String.class);

                    Log.d("Loading Data", "location" + location + "longitude" +
                            longitude + "latitude" + latitude);


                    arrayLocations.add(new MapLocation(location, longitude, latitude));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        }

    public void createMarkerFromFirebase(GoogleMap googleMap)
    {
        firebaseLoadData(googleMap);
    }

    private void useMapLongClickListener(final GoogleMap googleMap){


        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {


            @Override
            public void onMapLongClick(LatLng latltn) {
                Log.i(LOG_MAP, "onMapLongClick");


                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latltn)
                        .zoom(10)
                        .bearing(0)
                        .build();


                // Camera that makes reference to the maps view
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);


                googleMap.animateCamera(cameraUpdate, 2000, new GoogleMap.CancelableCallback() {


                    @Override
                    public void onFinish() {
                        Log.i(LOG_MAP, "googleMap.animateCamera:onFinish is active");
                    }


                    @Override
                    public void onCancel() {
                        Log.i(LOG_MAP, "googleMap.animateCamera:onCancel is active");
                    }});
            }
        });
    }

    private void useMarkerClickListener(GoogleMap googleMap) {
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i(LOG_MAP, "setOnMarkerClickListener");

                return false;
            }
        });
    }

    private ArrayList<MapLocation> loadData() {

        ArrayList<MapLocation> mapLocations = new ArrayList<>();

        mapLocations.add(new MapLocation("New York", "City never sleeps", String.valueOf(39.953348), String.valueOf(-75.163353)));
        mapLocations.add(new MapLocation("Paris", "City of lights", String.valueOf(48.856788), String.valueOf(2.351077)));
        mapLocations.add(new MapLocation("Las Vegas", "City of dreams", String.valueOf(36.167114), String.valueOf(-115.149334)));
        mapLocations.add(new MapLocation("Tokyo", "City of technology", String.valueOf(35.689506), String.valueOf(139.691700)));

        return mapLocations;
    }
}
