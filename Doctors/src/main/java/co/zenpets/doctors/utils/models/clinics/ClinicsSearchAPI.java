package co.zenpets.doctors.utils.models.clinics;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClinicsSearchAPI {
    @GET("clinicSearch")
    Call<ClinicsData> clinicSearch(@Query("clinicName") String clinicName);
}