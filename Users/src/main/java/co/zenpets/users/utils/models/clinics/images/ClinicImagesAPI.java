package co.zenpets.users.utils.models.clinics.images;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClinicImagesAPI {

    /** FETCH THE CLINICS IMAGES **/
    @GET("fetchClinicImages")
    Call<ClinicImages> fetchClinicImages(@Query("clinicID") String clinicID);
}