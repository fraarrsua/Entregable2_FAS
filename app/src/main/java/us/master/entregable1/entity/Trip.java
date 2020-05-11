package us.master.entregable1.entity;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Trip implements Serializable {
    private String lugarSalida, lugarDestino, descripcion, url;
    private long fechaSalida, fechaLlegada;
    private int precio, id;
    private boolean seleccionado;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return fechaSalida == trip.fechaSalida &&
                fechaLlegada == trip.fechaLlegada &&
                precio == trip.precio &&
                id == trip.id &&
                seleccionado == trip.seleccionado &&
                Objects.equals(lugarSalida, trip.lugarSalida) &&
                Objects.equals(lugarDestino, trip.lugarDestino) &&
                Objects.equals(descripcion, trip.descripcion) &&
                Objects.equals(url, trip.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lugarSalida, lugarDestino, descripcion, url, fechaSalida, fechaLlegada, precio, id, seleccionado);
    }

    public Trip(int id, String lugarSalida, String lugarDestino, String descripcion, long fechaSalida, long fechaLlegada, int precio, String url, Boolean seleccionado) {
        this.id = id;
        this.lugarSalida = lugarSalida;
        this.lugarDestino = lugarDestino;
        this.descripcion = descripcion;
        this.fechaSalida = fechaSalida;
        this.fechaLlegada = fechaLlegada;
        this.precio = precio;
        this.url = url;
        this.seleccionado = seleccionado;


    }

    public int getId() {
        return id;
    }

    public String getLugarSalida() {
        return lugarSalida;
    }

    public String getLugarDestino() {
        return lugarDestino;
    }

    public long getFechaSalida() {
        return fechaSalida;
    }

    public Trip() {
    }

    public long getFechaLlegada() {
        return fechaLlegada;
    }

    public int getPrecio() {
        return precio;
    }

    public String getUrl() {
        return url;
    }

    public void setLugarSalida(String lugarSalida) {
        this.lugarSalida = lugarSalida;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setLugarDestino(String lugarDestino) {
        this.lugarDestino = lugarDestino;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFechaSalida(long fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public void setFechaLlegada(long fechaLlegada) {
        this.fechaLlegada = fechaLlegada;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    @Override
    public String toString() {
        return lugarDestino +
                ", Fecha salida: " + Util.formateaFecha(fechaSalida) +
                ", Fecha llegada: " + Util.formateaFecha(fechaLlegada) +
                ", Precio: " + precio;
    }

    public static List<Trip> generaViajes(int numViajes) {
        List<Trip> trips = new ArrayList<>();
        int min = 75, max = 2050, aleatorio, precio;
        String lugarSalida, lugarDestino, descripcion, url;
        Calendar fechaSalida, fechaLlegada, fechaActual = Calendar.getInstance();
        long fsal, flle;
        boolean seleccionado = false;
        for (int id = 0; id < numViajes; id++) {
            aleatorio = ThreadLocalRandom.current().nextInt(min, max);
            lugarSalida = Constantes.lugarSalida[aleatorio % Constantes.lugarSalida.length];
            lugarDestino = Constantes.ciudades[aleatorio % Constantes.ciudades.length];
            url = Constantes.urlImagenes[aleatorio % Constantes.urlImagenes.length];
            descripcion = "Viaje precioso por " + lugarDestino;
            fechaSalida = (Calendar) fechaActual.clone();
            fechaSalida.add(Calendar.DAY_OF_MONTH, aleatorio % 10);
            fsal = fechaSalida.getTimeInMillis() / 1000;
            fechaLlegada = (Calendar) fechaSalida.clone();
            fechaLlegada.add(Calendar.DAY_OF_MONTH, 3 + aleatorio % 2);
            flle = fechaLlegada.getTimeInMillis() / 1000;
            precio = aleatorio;
            trips.add(new Trip(id, lugarSalida, lugarDestino, descripcion, fsal, flle, precio, url, seleccionado));
        }
        return trips;
    }
}