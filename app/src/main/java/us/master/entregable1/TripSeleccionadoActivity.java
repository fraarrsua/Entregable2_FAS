package us.master.entregable1;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import java.util.List;
import java.util.stream.Collectors;

import us.master.entregable1.entity.Trip;
import us.master.entregable1.entity.Util;

public class TripSeleccionadoActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TripAdapter tripAdapter;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_seleccionado);
        recyclerView = findViewById(R.id.recycler_view_seleccionado);

        List<Trip> trips = Util.triplist, tripsFiltrados;
        tripsFiltrados = trips.stream().filter((trip) -> trip.isSeleccionado()).collect(Collectors.toList());

        if (tripsFiltrados.size() == 0) {
            Toast.makeText(this, "No hay viajes seleccionados", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, +tripsFiltrados.size() + " viajes seleccionados", Toast.LENGTH_LONG).show();
        }
        tripAdapter = new TripAdapter(tripsFiltrados, this);
        gridLayoutManager = new GridLayoutManager(this, 1);

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(tripAdapter);

    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
