package com.example.pizzamania;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private Location currentLocation;
    private Marker marker;
    private FusedLocationProviderClient fusedClient;

    private static final int REQUEST_CODE = 101;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        searchView = findViewById(R.id.search);
        searchView.clearFocus();

        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();  // will call getMapAsync once we have a (non-null) last location
    }

    private void getLocation() {
        // Check permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        // Fetch last known location
        Task<Location> task = fusedClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            currentLocation = location; // may be null — handle later
            SupportMapFragment smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (smf != null) smf.getMapAsync(MapsActivity.this);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        // Optionally: enable the my-location blue dot (requires permission checks)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
        }

        // Move camera if we have a last known location
        if (currentLocation != null) {
            LatLng me = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me, 15f));
            gMap.addMarker(new MarkerOptions().position(me).title("My Current Location"));
        } else {
            Toast.makeText(this, "No last known location. Try again outdoors.", Toast.LENGTH_SHORT).show();
        }

        // Set SearchView listener only after map is ready
        setupSearch();
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                if (gMap == null) return false; // safety
                if (query == null || query.trim().isEmpty()) {
                    Toast.makeText(MapsActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                    return false;
                }

                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(query, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        LatLng latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

                        if (marker != null) marker.remove();

                        marker = gMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(query)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                    } else {
                        Toast.makeText(MapsActivity.this, "No results for \"" + query + "\"", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(MapsActivity.this, "Geocoder error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override public boolean onQueryTextChange(String newText) { return false; }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
    }
}
