package us.master.entregable1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import us.master.entregable1.entity.Opcion;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridView = findViewById(R.id.gridView_main);
        OpcionAdapter opcionAdapter = new OpcionAdapter(Opcion.crearOpciones(), this);
        gridView.setAdapter(opcionAdapter);
    }

    class OpcionAdapter extends BaseAdapter {
        List<Opcion> opciones;
        Context context;

        private OpcionAdapter(List<Opcion> opciones, Context context) {
            this.opciones = opciones;
            this.context = context;
        }

        @Override
        public int getCount() {
            return opciones.size();
        }

        @Override
        public Object getItem(int i) {
            return opciones.get(i);
        }

        @Override
        public long getItemId(int i) {
            return opciones.get(i).hashCode();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final Opcion opcion = opciones.get(i);
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.seleccion_opcion, viewGroup, false);
            }
            CardView cardView = view.findViewById(R.id.cardView);
            ImageView imageView = view.findViewById(R.id.opcion_imagen);
            TextView textView = view.findViewById(R.id.opcion_nombre);

            imageView.setImageResource(opcion.getRecursoImageView());
            textView.setText(opcion.getDescripcion());

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, opcion.getClase()));
                    finish();
                }
            });
            return view;
        }
    }
}

