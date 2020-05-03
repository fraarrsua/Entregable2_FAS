package us.master.entregable1;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import us.master.entregable1.entity.Constantes;
import us.master.entregable1.entity.Trip;
import us.master.entregable1.entity.Util;

public class TripDetalleActivity extends AppCompatActivity {

    ImageView imagen, estrella;
    TextView ciudad, precio, fechaSalida, fechaLlegada, ciudadSalida;
    Trip seleccionadoTrip, trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detalle);

        seleccionadoTrip = (Trip) getIntent().getSerializableExtra(Constantes.IntentViaje);
        trip = Util.triplist.get(seleccionadoTrip.getId());
        imagen = findViewById(R.id.tripDetalleImageViewTrip);
        ciudad = findViewById(R.id.tripDetalleTextViewDestino);
        precio = findViewById(R.id.tripDetalleTextViewPrecio);
        fechaSalida = findViewById(R.id.tripDetalleTextViewFechaSalida);
        fechaLlegada = findViewById(R.id.tripDetalleTextViewFechaLlegada);
        ciudadSalida = findViewById(R.id.tripDetailTextViewLugarSalida);
        estrella = findViewById(R.id.tripDetalleTextViewSeleccion);

        if (!trip.getUrl().isEmpty()) {
            Picasso.get()
                    .load(trip.getUrl())
                    .placeholder(android.R.drawable.ic_menu_myplaces)
                    .error(android.R.drawable.ic_menu_myplaces)
                    .into(imagen);

        }

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
        startActivity(new Intent(this, TripActivity.class));
        finish();
    }
}
