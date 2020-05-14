package us.master.entregable1.restypes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherRetrofitInterface {
    @GET("data/2.5/weather")
    Call<WeatherResponse> getCurrenWeatherResponse(@Query("lat") float latitude, @Query("lon") float longitude, @Query("appid") String appId, @Query("units") String unidad, @Query("lang") String lang);
}
