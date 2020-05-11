package us.master.entregable1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import us.master.entregable1.entity.Constantes;
import us.master.entregable1.entity.Trip;
import us.master.entregable1.entity.Util;

public class TripRegistrarActivity extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();
    private AutoCompleteTextView lugarSalida, lugarDestino, descripcion;
    private EditText precio;
    private Button tripRegistro;
    private TextView fechaSalida, fechaLlegada;
    private long fechaSalidaLong = 0, fechaLlegadaLong = 0;
    private TextInputLayout lugarSalidaParent, precioParent, descripcionParent, lugarDestinoParent, fechaSalidaParent, fechaLlegadaParent;
    FirestoreService firestoreService = FirestoreService.getServiceInstance();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_registrar);
        sharedPreferences = getSharedPreferences(Constantes.newTripPreference, MODE_PRIVATE);

        lugarSalida = findViewById(R.id.trip_lugarSalida_form_et);
        lugarDestino = findViewById(R.id.trip_lugarDestino_form_et);
        precio = findViewById(R.id.trip_precio_form_et);
        descripcion = findViewById(R.id.trip_descripcion_form_et);
        fechaSalida = findViewById(R.id.trip_date_start_et);
        fechaLlegada = findViewById(R.id.trip_date_end_et);

        tripRegistro = findViewById(R.id.trip_button_form_create);

        lugarSalidaParent = findViewById(R.id.trip_lugarSalida_form);
        lugarDestinoParent = findViewById(R.id.trip_lugarDestino_form);
        precioParent = findViewById(R.id.trip_precio_form);
        descripcionParent = findViewById(R.id.trip_descripcion_form);
        fechaSalidaParent = findViewById(R.id.trip_date_start);
        fechaLlegadaParent = findViewById(R.id.trip_date_end);


        findViewById(R.id.trip_button_form_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Trip trip = new Trip();

                if (lugarSalida.getText().length() == 0) {
                    lugarSalidaParent.setErrorEnabled(true);
                    lugarSalidaParent.setError(getString(R.string.trip_field_empthy));
                } else if (lugarDestino.getText().length() == 0) {
                    lugarDestinoParent.setErrorEnabled(true);
                    lugarDestinoParent.setError(getString(R.string.trip_field_empthy));
                } else if (descripcion.getText().length() == 0) {
                    descripcionParent.setErrorEnabled(true);
                    descripcionParent.setError(getString(R.string.trip_field_empthy));
                } else if (fechaSalida.getText().length() == 0) {
                    fechaSalidaParent.setErrorEnabled(true);
                    fechaSalidaParent.setError(getString(R.string.trip_field_empthy));
                } else if (fechaLlegada.getText().length() == 0) {
                    fechaLlegadaParent.setErrorEnabled(true);
                    fechaLlegadaParent.setError(getString(R.string.trip_field_empthy));
                } else if (lugarSalida.getText().length() == 0) {
                    lugarSalidaParent.setErrorEnabled(true);
                    lugarSalidaParent.setError(getString(R.string.trip_field_empthy));
                } else {
                    trip.setLugarSalida(lugarSalida.getText().toString());
                    trip.setLugarDestino(lugarDestino.getText().toString());
                    trip.setDescripcion(descripcion.getText().toString());
                    trip.setPrecio(getValorPrecio(precio));
                    trip.setFechaSalida(sharedPreferences.getLong(Constantes.fechaInicio, 0));
                    trip.setFechaLlegada(sharedPreferences.getLong(Constantes.fechaFin, 0));
                    //TODO
                    trip.setUrl("");
                    trip.setSeleccionado(false);

                    firestoreService.saveTrip(trip, new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                DocumentReference documentReference = task.getResult();
                                Log.i("Trips", "Trip nuevo por formulario añadido" + task.getResult().getId());
                                Toast.makeText(TripRegistrarActivity.this, R.string.trip_created, Toast.LENGTH_LONG).show();
                            } else {
                                Log.i("Trips", "No se ha podido añadir el viaje ");
                            }
                        }
                    });
                }
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    private int getValorPrecio(EditText editTextPrecio) {
        return Integer.parseInt(editTextPrecio.getText().toString().equals("")
                ? "0"
                : editTextPrecio.getText().toString());
    }

    private void asignaFechaRegistro(final String fecha, final TextView textView) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, yy, mm, dd) -> {
            editor = sharedPreferences.edit();
            calendar.set(yy, mm, dd, 0, 0, 0);
            String fechaFormateada = Util.formateaFecha(calendar);
            textView.setText(fechaFormateada);
            editor.putLong(fecha, Util.Calendar2long(calendar)).apply();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void fechaRegistro(View view) {
        switch (view.getId()) {
            case R.id.calendarioInicio:
                asignaFechaRegistro(Constantes.fechaInicio, fechaSalida);
                break;
            case R.id.calendarioFin:
                asignaFechaRegistro(Constantes.fechaFin, fechaLlegada);
                break;
        }
    }

}
