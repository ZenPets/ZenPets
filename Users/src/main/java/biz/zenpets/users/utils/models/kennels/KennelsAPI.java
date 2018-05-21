package biz.zenpets.users.utils.models.kennels;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface KennelsAPI {

    /** FETCH THE LIST OF A USER'S PETS **/
    @GET("fetchKennelsListByCity")
    Call<Kennels> fetchKennelsListByCity(
            @Query("cityID") String cityID,
            @Query("pageNumber") String pageNumber);
}