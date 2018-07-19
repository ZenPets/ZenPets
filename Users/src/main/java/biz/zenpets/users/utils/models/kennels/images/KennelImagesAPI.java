package biz.zenpets.users.utils.models.kennels.images;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface KennelImagesAPI {

    /** FETCH ALL KENNEL IMAGES **/
    @GET("fetchKennelImages")
    Call<KennelImages> fetchKennelImages(@Query("kennelID") String kennelID);
}