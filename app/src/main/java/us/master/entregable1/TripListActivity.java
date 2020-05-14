package us.master.entregable1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;
import java.util.stream.Collectors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import us.master.entregable1.entity.Constantes;
import us.master.entregable1.entity.Trip;
import us.master.entregable1.restypes.WeatherResponse;
import us.master.entregable1.restypes.WeatherRetrofitInterface;

public class TripListActivity extends AppCompatActivity {

    private static final String TAG = TripActivity.class.getSimpleName();
    RecyclerView recyclerView;
    Switch switchCol;
    ImageView estrella;
    TripListAdapter tripListAdapter;
    GridLayoutManager gridLayoutManager;
    SharedPreferences sharedPreferences;
    TextView ciudad, temperatura;
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build();

        recyclerView = findViewById(R.id.recyclerView);
        switchCol = findViewById(R.id.switchColumnas);
        estrella = findViewById(R.id.estrella_imagen);
        sharedPreferences = getSharedPreferences(Constantes.filtroPreferences, MODE_PRIVATE);
        ciudad = findViewById(R.id.textview_ciudad);
        temperatura = findViewById(R.id.textview_temp);
        fieldsAPIWeather();
        tripListAdapter = new TripListAdapter();

        tripListAdapter.setDataChangedListener(() -> {
            if (tripListAdapter.getItemCount() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.GONE);
            }
            tripListAdapter.setErrorListener(error -> {
                recyclerView.setVisibility(View.GONE);
            });
        });

        if (switchCol.isChecked()) {
            gridLayoutManager = new GridLayoutManager(this, 2);

        } else {
            gridLayoutManager = new GridLayoutManager(this, 1);
        }

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(tripListAdapter);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tripListAdapter != null && tripListAdapter.listenerRegistration != null) {
            tripListAdapter.listenerRegistration.remove();
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

    private void fieldsAPIWeather() {
        WeatherRetrofitInterface service = retrofit.create(WeatherRetrofitInterface.class);
        Call<WeatherResponse> response = service.getCurrenWeatherResponse((float) LocationActivity.lastLocation.getLatitude(), (float) LocationActivity.lastLocation.getLongitude(),
                getString(R.string.open_weather_map_api_key), getString(R.string.open_weather_map_api_unit), getString(R.string.open_weather_map_api_language));
        response.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String cityname = response.body().getName();
                    int humidity = response.body().getMain().getHumidity();
                    Float degrees = response.body().getMain().getTemp();
                    ciudad.setText("Estas en " + cityname);
                    temperatura.setText(String.format("a " + "%.2f", degrees) + "ºC" + "  y  " + humidity + "%");
                    Log.i("TripApp", "La temperatura actual en " + response.body().getName() + response.body().getMain().getTemp());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.i("TripApp", "Error en la llamada" + t.getMessage());
            }
        });
    }
}
