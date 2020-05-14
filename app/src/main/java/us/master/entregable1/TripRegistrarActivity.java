package us.master.entregable1;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import us.master.entregable1.entity.Constantes;
import us.master.entregable1.entity.Trip;
import us.master.entregable1.entity.Util;

public class TripRegistrarActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 0x512;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 0x513;
    private static final int TAKE_PHOTO_CODE = 0x514;

    Calendar calendar = Calendar.getInstance();
    private AutoCompleteTextView lugarSalida, lugarDestino, descripcion;
    private EditText precio;
    private Button tripRegistro;
    private TextView fechaSalida, fechaLlegada;
    private long fechaSalidaLong = 0, fechaLlegadaLong = 0;
    private TextInputLayout lugarSalidaParent, precioParent, descripcionParent, lugarDestinoParent, fechaSalidaParent, fechaLlegadaParent;
    private Button take_picture_button;
    private ImageView take_picture_img;

    FirestoreService firestoreService = FirestoreService.getServiceInstance();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private Trip trip;
    private String file;

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

        take_picture_button = findViewById(R.id.trip_button_form_take_picture);
        take_picture_img = findViewById(R.id.take_picture_imagen);

        take_picture_button.setOnClickListener(l -> {
            takePicture();
        });

        findViewById(R.id.trip_button_form_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trip = new Trip();

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
                } else if (take_picture_img.getContentDescription() == null) {
                    Toast.makeText(TripRegistrarActivity.this, getString(R.string.trip_without_take_picture), Toast.LENGTH_LONG).show();
                } else {
                    trip.setLugarSalida(lugarSalida.getText().toString());
                    trip.setLugarDestino(lugarDestino.getText().toString());
                    trip.setDescripcion(descripcion.getText().toString());
                    trip.setPrecio(getValorPrecio(precio));
                    trip.setFechaSalida(sharedPreferences.getLong(Constantes.fechaInicio, 0));
                    trip.setFechaLlegada(sharedPreferences.getLong(Constantes.fechaFin, 0));
                    trip.setSeleccionado(false);
                    trip.setUrl(take_picture_img.getContentDescription().toString());
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

    private void takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Snackbar.make(take_picture_button, R.string.take_picture_rationale, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.take_picture_rationale_ok, click -> {
                    ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA }, CAMERA_PERMISSION_REQUEST);
                });
            } else {
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA }, CAMERA_PERMISSION_REQUEST);
            }
        } else {
            // Permisos concedidos
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Explicacion permisos
                    Snackbar.make(take_picture_button, R.string.take_picture_rationale, BaseTransientBottomBar.LENGTH_LONG).setAction(R.string.take_picture_rationale_ok, click -> {
                        ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
                    });
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
                }
            } else {
                // Todos los permisos concedidos

                // Privacidad
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                String dir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/tripsapp/";
                File newFile = new File(dir);

                newFile.mkdirs();

                file = dir + Calendar.getInstance().getTimeInMillis() + ".jpg";
                File newFilePicture = new File(file);

                try {
                    newFilePicture.createNewFile();
                } catch (Exception ignore) {
                }

                Uri outputFileDir = Uri.fromFile(newFilePicture);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileDir);
                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                Toast.makeText(this, R.string.camera_not_granted, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                Toast.makeText(this, R.string.storage_not_granted, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            File filePicture = new File(file);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child(filePicture.getName());
            UploadTask uploadTask = storageReference.putFile(Uri.fromFile(filePicture));

            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Log.i("TripsApp", "Firebase Storage completed: " + task.getResult().getTotalByteCount());

                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                String url_image = task.getResult().toString();
                                Log.i("TripsApp", "URL download image: " + url_image);
                                Glide.with(TripRegistrarActivity.this)
                                        .load(task.getResult())
                                        .centerCrop()
                                        .into(take_picture_img);
                                take_picture_img.setVisibility(View.VISIBLE);
                                take_picture_img.setContentDescription(url_image);
                            }
                        }
                    });
                }
            });

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("TripsApp", "FirebaseStorage error: " + e.getMessage());
                }
            });
        }
    }
}


