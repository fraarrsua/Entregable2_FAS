package us.master.entregable1;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import us.master.entregable1.entity.Constantes;
import us.master.entregable1.entity.Trip;
import us.master.entregable1.entity.Util;


public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> trips;
    private Context context;
    LayoutInflater layoutInflater;

    public TripAdapter(List<Trip> trips, Context context) {
        this.trips = trips;
        this.context = context;
    }


    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.from(context).inflate(R.layout.trip_elemento, viewGroup, false);
        return new TripViewHolder(view, context, trips);
    }


    @Override
    public void onBindViewHolder(@NonNull TripAdapter.TripViewHolder tripViewHolder, int i) {
        final Trip trip = trips.get(i);
        tripViewHolder.itemView.setTag(trip);
        tripViewHolder.ciudad.setText(trip.getLugarDestino());
        int precio = trip.getPrecio();
        String fechaSalida = Util.formateaFecha(trip.getFechaSalida());
        String fechaLlegada = Util.formateaFecha(trip.getFechaLlegada());
        tripViewHolder.descripcion.setText("   Fecha de Salida: " + fechaSalida + "             Fecha de Llegada: " + fechaLlegada + " - " + precio + "€");

        if (!trip.getUrl().isEmpty()) {
            Picasso.get()
                    .load(trip.getUrl())
                    .placeholder(android.R.drawable.ic_menu_myplaces)
                    .error(android.R.drawable.ic_menu_myplaces)
                    .into(tripViewHolder.imagen);
        }

        if (trip.isSeleccionado()) {
            tripViewHolder.estrella.setImageResource(android.R.drawable.star_on);
        } else {
            tripViewHolder.estrella.setImageResource(android.R.drawable.star_off);
        }
        tripViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TripDetalleActivity.class);
                intent.putExtra(Constantes.IntentViaje, trip);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    class TripViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen, estrella;
        TextView ciudad, descripcion;
        CardView cardView;
        Context context;
        List<Trip> trips;

        public TripViewHolder(View view, Context context, List<Trip> trips) {
            super(view);
            this.trips = trips;
            cardView = view.findViewById(R.id.trip_cardview);
            imagen = view.findViewById(R.id.trip_imagen);
            ciudad = view.findViewById(R.id.trip_ciudad);
            descripcion = view.findViewById(R.id.trip_descripcion);
            estrella = view.findViewById(R.id.estrella_imagen);
        }

        public void onClick(View v) {
            int position = getAdapterPosition();
            final Trip trip = this.trips.get(position);
            Toast.makeText(this.context, "Nombre: " + trip.getId(), Toast.LENGTH_SHORT).show();
        }
    }

}
