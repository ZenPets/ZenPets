package co.zenpets.doctors.utils.models.doctors.exists;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DoctorExistsAPI {
    @GET("doctorExists")
    Call<DoctorExists> doctorExists(@Query("doctorEmail") String doctorEmail);
}