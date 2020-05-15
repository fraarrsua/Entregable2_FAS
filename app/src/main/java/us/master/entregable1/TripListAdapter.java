package us.master.entregable1;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import us.master.entregable1.entity.Constantes;
import us.master.entregable1.entity.Trip;
import us.master.entregable1.entity.Util;


public class TripListAdapter extends RecyclerView.Adapter<TripListAdapter.TripViewHolder> implements EventListener<QuerySnapshot> {

    private static final String TAG = TripListAdapter.class.getSimpleName();
    private List<Trip> tripList;
    LayoutInflater layoutInflater;
    private DataChangedListener mDataChangedListener;
    private ItemErrorListener errorListener;
    final ListenerRegistration listenerRegistration;
    private Context context;

    public TripListAdapter(Context context) {
        this.context = context;
        this.tripList = new ArrayList<>();
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());
        listenerRegistration = FirestoreService.getServiceInstance().getTrips(this);
    }


    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View tripView = layoutInflater.from(context).inflate(R.layout.trip_elemento, viewGroup, false);
        return new TripViewHolder(tripView, context);
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
                if (t != null) {
                    t.setId(documentSnapshot.getId());
                    tripList.add(t);
                    Log.i(TAG, "Campo getId " + documentSnapshot.getId());
                }


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

        tripViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, TripDetalleActivity.class);
                intent.putExtra(Constantes.IntentViaje, trip.getId());
                context.startActivity(intent);
            }
        });


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

    class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imagen, estrella;
        TextView ciudad, descripcion;
        CardView cardView;
        Context context;
        List<Trip> trips;

        public TripViewHolder(View view, Context context) {
            super(view);
            this.context = context;
            cardView = view.findViewById(R.id.trip_cardview);
            imagen = view.findViewById(R.id.trip_imagen);
            ciudad = view.findViewById(R.id.trip_ciudad);
            descripcion = view.findViewById(R.id.trip_descripcion);
            estrella = view.findViewById(R.id.estrella_imagen);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            final Trip trip = this.trips.get(position);
        }
    }
}

