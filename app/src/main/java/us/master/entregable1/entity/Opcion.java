package us.master.entregable1.entity;

import java.util.ArrayList;
import java.util.List;

import us.master.entregable1.R;
import us.master.entregable1.TripActivity;
import us.master.entregable1.TripSeleccionadoActivity;

public class Opcion {
    private String descripcion;
    private int recursoImageView;
    private Class clase;

    private Opcion(String descripcion, int recursoImageView, Class clase) {
        this.descripcion = descripcion;
        this.recursoImageView = recursoImageView;
        this.clase = clase;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getRecursoImageView() {
        return recursoImageView;
    }

    public Class getClase() {
        return clase;
    }

    public static List<Opcion> crearOpciones() {
        List<Opcion> opciones = new ArrayList<>();
        opciones.add(new Opcion("Viajes disponibles", R.drawable.trip_disponible, TripActivity.class));
        opciones.add(new Opcion("Viajes seleccionados", R.drawable.trip_seleccionado, TripSeleccionadoActivity.class));
        return opciones;
    }

}
