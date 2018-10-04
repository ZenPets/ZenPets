package biz.zenpets.users.utils.models.groomers.images;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GroomerImagesAPI {

    /** FETCH ALL GROOMER IMAGES **/
    @GET("fetchGroomerImages")
    Call<GroomerImages> fetchGroomerImages(@Query("groomerID") String groomerID);
}