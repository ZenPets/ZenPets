package co.zenpets.users.utils.models.doctors.clinics;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClinicsAPI {

    /** CLINICS WHERE THE DOCTOR PRACTICES **/
    @GET("fetchDoctorClinics")
    Call<Clinics> fetchDoctorClinics(@Query("doctorID") String doctorID);
}