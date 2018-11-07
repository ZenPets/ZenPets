package co.zenpets.doctors.utils.models.doctors.clinic;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClinicCheckerAPI {
    @GET("fetchDoctorClinics")
    Call<ClinicCheckerData> fetchDoctorClinics(@Query("doctorID") String doctorID);
}