package co.zenpets.doctors.utils.models.doctors;

import co.zenpets.doctors.utils.models.doctors.details.DoctorDetail;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DoctorsAPI {
    /* FETCH THE LOGGED IN DOCTOR'S ID */
    @GET("fetchDoctorID")
    Call<Doctor> getDoctorID(@Query("doctorAuthID") String doctorAuthID);

    /* FETCH THE LIST OF DOCTORS AT A CLINIC */
    @GET("fetchClinicDoctors")
    Call<Doctors> fetchClinicDoctors(@Query("clinicID") String clinicID);

    /* SEARCH FOR DOCTORS */
    @GET("doctorSearch")
    Call<Doctors> doctorSearch(@Query("doctorName") String doctorName);

    /** FETCH A DOCTOR'S PROFILE **/
    @GET("fetchDoctorProfile")
    Call<Doctor> fetchDoctorProfile(@Query("doctorID") String doctorID);

    /** FETCH THE DOCTOR'S DETAILS **/
    @GET("fetchDoctorDetails")
    Call<DoctorDetail> fetchDoctorDetails(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);
}