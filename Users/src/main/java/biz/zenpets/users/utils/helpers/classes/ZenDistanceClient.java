package biz.zenpets.users.utils.helpers.classes;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ZenDistanceClient {

    /** THE BASE URL **/
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/";

    /** THE RETROFIT INSTANCE **/
    private static Retrofit retrofit = null;

    /** INSTANTIATE THE RETROFIT INSTANCE **/
    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}