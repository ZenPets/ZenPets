package biz.zenpets.trainers.utils.helpers;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ZenApiClient {

    /** THE BASE URL **/
    private static final String BASE_URL = "http://192.168.11.2/zenpets/public/";
//    private static final String BASE_URL = "http://34.235.84.116/zenpets/public/";

    /** THE RETROFIT INSTANCE **/
    private static Retrofit retrofit = null;

    /** INSTANTIATE THE RETROFIT INSTANCE **/
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}