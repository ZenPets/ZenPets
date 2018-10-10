package co.zenpets.users.utils.models.doctors;

import co.zenpets.users.utils.models.doctors.list.Doctor;
import co.zenpets.users.utils.models.doctors.list.DoctorPages;
import co.zenpets.users.utils.models.doctors.list.Doctors;
import co.zenpets.users.utils.models.doctors.profile.DoctorProfile;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DoctorsAPI {

    /** FETCH A DOCTOR'S PROFILE **/
    @GET("fetchDoctorProfile")
    Call<DoctorProfile> fetchDoctorProfile(@Query("doctorID") String doctorID);

    /** FETCH THE LIST OF DOCTORS **/
    @GET("fetchDoctorsList")
    Call<Doctors> fetchDoctorsList(
            @Query("cityID") String cityID,
            @Query("localityID") String localityID,
            @Query("originLat") String originLat,
            @Query("originLon") String originLon,
            @Query("pageNumber") String pageNumber);

    /** FETCH THE DOCTOR'S DETAILS **/
    @GET("fetchDoctorDetails")
    Call<Doctor> fetchDoctorDetails(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID,
            @Query("originLat") String originLat,
            @Query("originLon") String originLon);

    /** FETCH THE LIST OF DOCTORS **/
    @GET("fetchDoctorsSubset")
    Call<Doctors> fetchDoctorsSubset(
            @Query("cityID") String cityID,
            @Query("localityID") String localityID,
            @Query("originLat") String originLat,
            @Query("originLon") String originLon);

    /** FETCH THE TOTAL NUMBER OF DOCTOR PAGES **/
    @GET("fetchDoctorPages")
    Call<DoctorPages> fetchDoctorPages(
            @Query("cityID") String cityID,
            @Query("localityID") String localityID);
}