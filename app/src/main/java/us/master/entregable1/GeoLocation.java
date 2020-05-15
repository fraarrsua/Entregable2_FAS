package us.master.entregable1;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class GeoLocation {

    public void geoCodeAddress(final String locationAddress, Context context, Handler handler) {

        Thread thread = new Thread() {
            private String TAG = GeoLocation.class.getSimpleName();

            public void run() {
                Geocoder gecoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                double latitude = 0D;
                double longitude = 0D;
                try {
                    List addressList = gecoder.getFromLocationName(locationAddress, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = (Address) addressList.get(0);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(address.getLatitude()).append("\n");
                        stringBuilder.append(address.getLongitude()).append("\n");
                        result = stringBuilder.toString();
                        latitude = address.getLatitude();
                        longitude = address.getLongitude();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Address :  " + locationAddress + "\nLatitude and Longitude\n" + result;
                        bundle.putString("address", result);
                        bundle.putDouble("latitud", latitude);
                        bundle.putDouble("longitud", longitude);
                        message.setData(bundle);
                        Log.d(TAG, "handleMessageGeoLocation: " + result);
                    }
                    message.sendToTarget();
                }
            }

        };
        thread.start();
    }
}
