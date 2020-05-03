package us.master.entregable1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;
import java.util.stream.Collectors;

import us.master.entregable1.entity.Constantes;
import us.master.entregable1.entity.Trip;
import us.master.entregable1.entity.Util;

public class TripActivity extends AppCompatActivity {

    private static final String TAG = TripActivity.class.getSimpleName();
    RecyclerView recyclerView;
    Switch switchCol;
    ImageView estrella;
    TripAdapter tripAdapter;
    GridLayoutManager gridLayoutManager;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        recyclerView = findViewById(R.id.recyclerView);
        switchCol = findViewById(R.id.switchColumnas);
        estrella = findViewById(R.id.estrella_imagen);
        sharedPreferences = getSharedPreferences(Constantes.filtroPreferences, MODE_PRIVATE);

        List<Trip> trips = Util.triplist, tripsFiltrados;
        tripsFiltrados = validarFiltros(trips);

        tripAdapter = new TripAdapter(tripsFiltrados, this);

        if (tripsFiltrados.size() == 0) {
            Toast.makeText(this, "No hay viajes con las condiciones de filtro", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, tripsFiltrados.size() + " viajes con filtros aplicados", Toast.LENGTH_LONG).show();
        }

        if (switchCol.isChecked()) {
            gridLayoutManager = new GridLayoutManager(this, 2);

        } else {
            gridLayoutManager = new GridLayoutManager(this, 1);
        }

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(tripAdapter);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switchColumnas:
                if (switchCol.isChecked()) {
                    gridLayoutManager.setSpanCount(2);
                } else {
                    gridLayoutManager.setSpanCount(1);
                }
                recyclerView.setLayoutManager(gridLayoutManager);
                break;
            default:
                startActivity(new Intent(this, FilterActivity.class));
                finish();
                break;
        }

    }

    private List<Trip> validarFiltros(List<Trip> trips) {

        long fechaInicioSharedPref = sharedPreferences.getLong(Constantes.fechaInicio, 0);
        long fechaFinSharedPref = sharedPreferences.getLong(Constantes.fechaFin, 0);
        int precioMinSharedPref = sharedPreferences.getInt(Constantes.precioMin, 0);
        int precioMaxSharedPref = sharedPreferences.getInt(Constantes.precioMax, 0);

        if (fechaInicioSharedPref != 0 && fechaFinSharedPref == 0) {
            trips = trips.stream().filter(trip -> trip.getFechaSalida() >= fechaInicioSharedPref).collect(Collectors.toList());

        } else if (fechaInicioSharedPref == 0 && fechaFinSharedPref != 0) {
            trips = trips.stream().filter(trip -> trip.getFechaLlegada() < fechaFinSharedPref).collect(Collectors.toList());

        } else if (fechaInicioSharedPref != 0) {
            trips = trips.stream().filter((trip) ->
                    trip.getFechaSalida() >= fechaInicioSharedPref &&
                            trip.getFechaSalida() < fechaFinSharedPref &&
                            trip.getFechaLlegada() > fechaInicioSharedPref &&
                            trip.getFechaLlegada() <= fechaFinSharedPref).collect(Collectors.toList());
        }

        if ((precioMinSharedPref > 0) && (precioMaxSharedPref == 0)) {
            trips = trips.stream().filter(trip -> trip.getPrecio() >= precioMinSharedPref).collect(Collectors.toList());
            //Uso de Logs para chequear la salida del filtro
            Log.d(TAG, "comprobacion de Filtros: Precio Minimo " + precioMinSharedPref);
            Log.d(TAG, "Lista de viajes:" + trips);

        } else if (precioMaxSharedPref > 0 && precioMinSharedPref == 0) {
            trips = trips.stream().filter(trip -> trip.getPrecio() <= precioMaxSharedPref).collect(Collectors.toList());
        } else if (precioMinSharedPref != 0 && precioMaxSharedPref != 0) {
            trips = trips.stream().filter((trip) ->
                    trip.getPrecio() >= precioMinSharedPref &&
                            trip.getPrecio() <= precioMaxSharedPref).collect(Collectors.toList());
        }

        return trips;
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
