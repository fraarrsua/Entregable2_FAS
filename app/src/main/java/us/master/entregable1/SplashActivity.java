package us.master.entregable1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import us.master.entregable1.entity.Trip;
import us.master.entregable1.entity.Util;

public class SplashActivity extends Activity {
    final int DURACION_SPLASH = 1000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Util.triplist = Trip.generaViajes(20);
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, DURACION_SPLASH);


    }
}