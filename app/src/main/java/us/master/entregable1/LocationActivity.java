package us.master.entregable1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LocationActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 0x123;
    public static Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        TextView location = findViewById(R.id.location);

        String[] permissions = new String[]{ Manifest.permission.ACCESS_FINE_LOCATION };
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Snackbar.make(location, R.string.location_needed, Snackbar.LENGTH_LONG).setAction(R.string.location_rationale_ok, view -> {
                    ActivityCompat.requestPermissions(LocationActivity.this, permissions, PERMISSION_REQUEST_CODE_LOCATION);
                }).show();
            } else {
                ActivityCompat.requestPermissions(LocationActivity.this, permissions, PERMISSION_REQUEST_CODE_LOCATION);
            }
        } else {

            startLocationService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            }
        }
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null || locationResult.getLastLocation() == null || !locationResult.getLastLocation().hasAccuracy()) {
                return;
            }

            lastLocation = locationResult.getLastLocation();
            Log.i("TripsApp", "LocationFuncionando: " + lastLocation.getLatitude() + ", " + lastLocation.getLongitude() + ", acc: " + lastLocation.getAccuracy());
        }
    };

    @SuppressLint("MissingPermission")
    private void startLocationService() {
        FusedLocationProviderClient locationServices = LocationServices.getFusedLocationProviderClient(this);
        /* Ubicacion puntual.
        locationServices.getLastLocation().addOnCompleteListener(task -> {
           if (task.isSuccessful() && task.getResult() != null) {
               Location location = task.getResult();
               Log.i("TripsApp", "Location: " + location.getLatitude() + ", " + location.getLongitude() + ", acc: " + location.getAccuracy());
           }
        });
        */
        //locationRequest.setSmallestDisplacement(10); //Si nos movemos no  se actualiza la localización
        //showCurrentLocation: Dado una posición dame su nombre.
        //showCurrentLocationAddress: Dado una posición dame su nombre.
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(3000); //Ms en la que recogemos los datos de la localización.
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //Prioridad de este sistema de localización
        //Ubicación en tiempo real
        locationServices.requestLocationUpdates(locationRequest, locationCallback, null);

        startActivity(new Intent(LocationActivity.this, MainActivity.class));
        finish();
    }

    public void stopService() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
    }


}
