package us.master.entregable1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import us.master.entregable1.entity.Trip;
import us.master.entregable1.entity.Util;


public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.TripViewHolder> implements EventListener<QuerySnapshot> {

    private List<Trip> tripList;
    LayoutInflater layoutInflater;
    private DataChangedListener mDataChangedListener;
    private ItemErrorListener errorListener;
    final ListenerRegistration listenerRegistration;

    public TripListAdapter() {
        this.tripList = new ArrayList<>();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());
        listenerRegistration = FirestoreService.getServiceInstance().getTrips(this);
    }


    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View tripView = layoutInflater.from(viewGroup.getContext()).inflate(R.layout.trip_elemento, viewGroup, false);
        return new TripViewHolder(tripView);
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            errorListener.onItemError(e);
        }

        tripList.clear();
        if (queryDocumentSnapshots != null) {
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                Trip t = documentSnapshot.toObject(Trip.class);
                tripList.add(t);
            }
        }

        notifyDataSetChanged();
        mDataChangedListener.onDataChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull TripListAdapter.TripViewHolder tripViewHolder, int i) {
        final Trip trip = tripList.get(i);
        tripViewHolder.itemView.setTag(trip);
        tripViewHolder.ciudad.setText(trip.getLugarDestino());
        int precio = trip.getPrecio();
        String fechaSalida = Util.formateaFecha(trip.getFechaSalida());
        String fechaLlegada = Util.formateaFecha(trip.getFechaLlegada());
        tripViewHolder.descripcion.setText("\nFecha de Salida: " + fechaSalida + "\nFecha de Llegada: " + fechaLlegada + " - " + precio + "€");

        Glide.with(tripViewHolder.imagen.getContext())
                .load(trip.getUrl())
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .error(android.R.drawable.ic_menu_myplaces)
                .into(tripViewHolder.imagen);
        if (trip.isSeleccionado()) {
            tripViewHolder.estrella.setImageResource(android.R.drawable.star_on);
        } else {
            tripViewHolder.estrella.setImageResource(android.R.drawable.star_off);
        }

    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public void setErrorListener(ItemErrorListener itemErrorListener) {
        errorListener = itemErrorListener;
    }

    public interface ItemErrorListener {
        void onItemError(FirebaseFirestoreException error);
    }

    public void setDataChangedListener(DataChangedListener dataChangedListener) {
        mDataChangedListener = dataChangedListener;
    }

    public interface DataChangedListener {
        void onDataChanged();
    }

    class TripViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen, estrella;
        TextView ciudad, descripcion;
        CardView cardView;
        Context context;
        List<Trip> trips;

        public TripViewHolder(View view) {
            super(view);
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
