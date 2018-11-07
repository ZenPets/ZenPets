package co.zenpets.doctors.utils.models.clinics.details;

import co.zenpets.doctors.utils.models.clinics.ClinicData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClinicDetailsAPI {

    /** FETCH THE CLINIC'S DETAILS **/
    @GET("fetchClinicDetails")
    Call<ClinicData> fetchClinicDetails(@Query("clinicID") String clinicID);
}