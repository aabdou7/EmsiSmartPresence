package com.example.emsi_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LocationRequest locationRequest;
    private Marker userMarker;
    private boolean firstUpdate = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupLocationUpdates() {
        // Vérifie permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Création de la LocationRequest avec le nouveau Builder
        locationRequest = new LocationRequest.Builder(5000) // interval
                .setMinUpdateIntervalMillis(2000) // fastest interval
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        // Callback pour recevoir les mises à jour de position
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    updateLocationOnMap(location);
                }
            }
        };

        // Démarrer la localisation
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback, 
                null);
    }

    private void updateLocationOnMap(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (userMarker == null) {
            // Premier marker
            userMarker = mMap.addMarker(new MarkerOptions()
                .position(userLocation)
                .title("Vous êtes ici")
                .snippet("Position actuelle"));
        } else {
            userMarker.setPosition(userLocation);
        }

        if (firstUpdate) {
            animateCamera(userLocation, 15f); // Initial zoom
            firstUpdate = false;
        } else {
            animateCamera(userLocation, mMap.getCameraPosition().zoom);
        }
    }

    private void animateCamera(LatLng target, float zoomLevel) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(target)
                .zoom(zoomLevel)
                .bearing(0) // Orientation nord
                .tilt(45)   // Inclinaison de la caméra pour un effet 3D
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupLocationUpdates();
            } else {
                Toast.makeText(requireContext(), "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupLocationUpdates();

        // EMSI Center coordinates
        LatLng emsi = new LatLng(33.589194315969172, -7.603527086230895);
        
        // Add marker
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(emsi)
                .title("EMSI Center")
                .snippet("EMSI Center Location"));
        
        // Move camera to location with zoom
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(emsi, 15));
        
        // Enable zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        
        // Enable my location button
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        
        marker.setTag("destination");

        // Using lambda expression for OnMarkerClickListener
        mMap.setOnMarkerClickListener(marker1 -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

            fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                        LatLng destination = marker1.getPosition();

                        // Tracer une ligne (polyline) entre la position actuelle et le marker
                        mMap.addPolyline(new PolylineOptions()
                                .add(current, destination)
                                .width(5)
                                .color(Color.BLUE));
                    }
                });
            return false;
        });

        // Add all EMSI locations as markers
        LatLng emsiMaarif = new LatLng(33.587750, -7.627054);
        mMap.addMarker(new MarkerOptions().position(emsiMaarif).title("EMSI Maarif"));

        LatLng emsiRoudani = new LatLng(33.5793, -7.6397);
        mMap.addMarker(new MarkerOptions().position(emsiRoudani).title("EMSI Roudani"));

        LatLng emsiMoulayYoussef = new LatLng(33.5892, -7.6277);
        mMap.addMarker(new MarkerOptions().position(emsiMoulayYoussef).title("EMSI Moulay Youssef"));

        LatLng emsiCentre1 = new LatLng(33.5903, -7.6111);
        mMap.addMarker(new MarkerOptions().position(emsiCentre1).title("EMSI Centre 1"));

        LatLng emsiCentre2 = new LatLng(33.5905, -7.6115);
        mMap.addMarker(new MarkerOptions().position(emsiCentre2).title("EMSI Centre 2"));

        LatLng emsiOrangers = new LatLng(33.5359, -7.6495);
        mMap.addMarker(new MarkerOptions().position(emsiOrangers).title("EMSI Les Orangers (Oulfa)"));

        // Centrer la caméra sur EMSI Centre 1
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(emsiCentre1, 13));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}
