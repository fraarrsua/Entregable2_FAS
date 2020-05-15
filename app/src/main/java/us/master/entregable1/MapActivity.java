package us.master.entregable1;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.fragment.app.FragmentActivity;
import us.master.entregable1.entity.Constantes;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapActivity.class.getSimpleName();
    private GoogleMap googleMap;
    private String coordenadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        coordenadas = getIntent().getStringExtra(Constantes.IntentViaje);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        double longitud = Double.parseDouble(coordenadas.substring(coordenadas.indexOf(",")+2));
        double latitud = Double.parseDouble(coordenadas.substring(0, coordenadas.indexOf(",")));

        Log.d(TAG, "longitud:"+coordenadas.substring(coordenadas.indexOf(",")+2));
        Log.d(TAG, "latitud:"+coordenadas.substring(0,coordenadas.indexOf(",")));
        LatLng location = new LatLng(latitud, longitud);
        googleMap.addMarker(new MarkerOptions().title("Mi primera posicion").position(location));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(location));
    }
}
