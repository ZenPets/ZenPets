package co.zenpets.doctors.utils.models.doctors.clinic;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DoctorClinicsAPI {

    /* FETCH THE LIST OF CLINICS WHERE THE DOCTOR PRACTICES */
    @GET("fetchDoctorClinics")
    Call<DoctorClinics> fetchDoctorClinics(@Query("doctorID") String doctorID);
}