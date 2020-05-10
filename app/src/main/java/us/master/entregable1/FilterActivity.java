package us.master.entregable1;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import us.master.entregable1.entity.Constantes;
import us.master.entregable1.entity.Util;

public class FilterActivity extends AppCompatActivity {


    Calendar calendar = Calendar.getInstance();
    TextView fechaInicio, fechaFin;
    EditText precioMin, precioMax;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, TripListActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        sharedPreferences = getSharedPreferences(Constantes.filtroPreferences, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        fechaInicio = findViewById(R.id.textViewFechaInicio);
        fechaFin = findViewById(R.id.textViewFechaFin);
        precioMin = findViewById(R.id.editTextPrecioMin);
        precioMax = findViewById(R.id.editTextPrecioMax);

        long fechaInicioSharedPref = sharedPreferences.getLong(Constantes.fechaInicio, 0);
        long fechaFinSharedPref = sharedPreferences.getLong(Constantes.fechaFin, 0);
        int precioMinSharePref = sharedPreferences.getInt(Constantes.precioMin, 0);
        int precioMaxSharePref = sharedPreferences.getInt(Constantes.precioMax, 0);

        if (fechaInicioSharedPref != 0) {
            fechaInicio.setText(Util.formateaFecha(fechaInicioSharedPref));
        }
        if (fechaFinSharedPref != 0) {
            fechaFin.setText(Util.formateaFecha(fechaFinSharedPref));
        }
        if (precioMinSharePref != 0) {
            precioMin.setText(String.valueOf(precioMinSharePref));
        }
        if (precioMaxSharePref != 0) {
            precioMax.setText(String.valueOf(precioMaxSharePref));
        }
    }

    public void fecha(View view) {
        switch (view.getId()) {
            case R.id.calendarioInicio:
                asignaFecha(Constantes.fechaInicio, fechaInicio);
                break;
            case R.id.calendarioFin:
                asignaFecha(Constantes.fechaFin, fechaFin);
                break;
        }
    }

    public void precio(View view) {
        int precioMinAsignado = getValorPrecio(precioMin);
        int precioMaxAsignado = getValorPrecio(precioMax);

        if (precioMinAsignado >= 0 && precioMaxAsignado >= 0)
            if (precioValidado(precioMinAsignado, precioMaxAsignado)) {
                //(boolean)?+:-;
                asignaPrecio(Constantes.precioMin,
                        getValorPrecio(precioMin));
                asignaPrecio(Constantes.precioMax,
                        getValorPrecio(precioMax));
                onBackPressed();
            } else {
                Toast.makeText(this, "Valores de precios incorrectos", Toast.LENGTH_LONG).show();
            }
    }

    private int getValorPrecio(EditText editTextPrecio) {
        return Integer.parseInt(editTextPrecio.getText().toString().equals("")
                ? "0"
                : editTextPrecio.getText().toString());
    }

    private void asignaPrecio(final String precio, final int valor) {
        editor.putInt(precio, valor).commit();
    }

    private boolean precioValidado(int min, int max) {
        if (max > 0)
            if (min > 0)
                return max > min;
            else return true;
        else return true;
    }

    private void asignaFecha(final String fecha, final TextView textView) {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, yy, mm, dd) -> {
            calendar.set(yy, mm, dd, 0, 0, 0);
            String fechaFormateada = Util.formateaFecha(calendar);
            textView.setText(fechaFormateada);
            editor.putLong(fecha, Util.Calendar2long(calendar)).commit();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

}
