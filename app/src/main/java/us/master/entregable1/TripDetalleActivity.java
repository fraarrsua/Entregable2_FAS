package us.master.entregable1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import androidx.appcompat.app.AppCompatActivity;
import us.master.entregable1.entity.Constantes;
import us.master.entregable1.entity.Trip;
import us.master.entregable1.entity.Util;

public class TripDetalleActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = TripDetalleActivity.class.getSimpleName();
    ImageView imagen, estrella;
    TextView ciudad, precio, fechaSalida, fechaLlegada, ciudadSalida;
    Trip trip;
    private TripListAdapter.ItemErrorListener errorListener;
    FirestoreService firestoreService;
    String idTrip;
    GoogleMap googleMap;
    public TextView coordenadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detalle);
        imagen = findViewById(R.id.tripDetalleImageViewTrip);
        ciudad = findViewById(R.id.tripDetalleTextViewDestino);
        precio = findViewById(R.id.tripDetalleTextViewPrecio);
        fechaSalida = findViewById(R.id.tripDetalleTextViewFechaSalida);
        fechaLlegada = findViewById(R.id.tripDetalleTextViewFechaLlegada);
        ciudadSalida = findViewById(R.id.tripDetailTextViewLugarSalida);
        estrella = findViewById(R.id.tripDetalleTextViewSeleccion);
        firestoreService = FirestoreService.getServiceInstance();
        idTrip = getIntent().getStringExtra(Constantes.IntentViaje);
        coordenadas = findViewById(R.id.coordenadas);


        firestoreService.getTrip(idTrip, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    trip = documentSnapshot.toObject(Trip.class);
                    ciudad.setText(trip.getLugarDestino());
                    precio.setText(String.valueOf(trip.getPrecio() + "€"));
                    String salida = Util.formateaFecha(trip.getFechaSalida());
                    String llegada = Util.formateaFecha(trip.getFechaLlegada());
                    fechaSalida.setText(salida);
                    fechaLlegada.setText(llegada);
                    ciudadSalida.setText(trip.getLugarSalida());

                    if (trip.isSeleccionado()) {
                        estrella.setImageResource(android.R.drawable.star_on);
                    } else {
                        estrella.setImageResource(android.R.drawable.star_off);
                    }

                    GeoLocation geoLocation = new GeoLocation();
                    geoLocation.geoCodeAddress(trip.getLugarDestino(), getApplicationContext(), new GeoHandler());

                    Glide.with(TripDetalleActivity.this)
                            .load(trip.getUrl())
                            .placeholder(android.R.drawable.ic_menu_myplaces)
                            .error(android.R.drawable.ic_menu_myplaces)
                            .into(imagen);

                }

            }
        });

    }

    public void abrirmapa(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(Constantes.IntentViaje, this.coordenadas.getText());
        startActivity(intent);
    }

    public class GeoHandler extends Handler {
        public void handleMessage(Message msg) {
            String address;
            double lat = 0d, longi = 0d;

            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    address = bundle.getString("address");
                    lat = bundle.getDouble("latitud");
                    longi = bundle.getDouble("longitud");
                    break;
                default:
                    address = null;
            }
            coordenadas.setText(String.valueOf(lat).concat(", " + longi));
            Log.d(TAG, "handleMessage: " + lat + " " + longi + " " + address);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void seleccionar(View view) {
        if (!trip.isSeleccionado()) {
            trip.setSeleccionado(true);
            estrella.setImageResource(android.R.drawable.star_on);

        } else {
            trip.setSeleccionado(false);
            estrella.setImageResource(android.R.drawable.star_off);
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, TripListActivity.class));
        finish();
    }
}
