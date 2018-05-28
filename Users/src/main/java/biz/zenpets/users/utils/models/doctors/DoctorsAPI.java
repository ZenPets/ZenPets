package biz.zenpets.users.utils.models.doctors;

import biz.zenpets.users.utils.models.doctors.details.DoctorDetail;
import biz.zenpets.users.utils.models.doctors.list.DoctorPages;
import biz.zenpets.users.utils.models.doctors.list.Doctors;
import biz.zenpets.users.utils.models.doctors.profile.DoctorProfile;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DoctorsAPI {

    /** FETCH A DOCTOR'S PROFILE **/
    @GET("fetchDoctorProfile")
    Call<DoctorProfile> fetchDoctorProfile(@Query("doctorID") String doctorID);

    /** FETCH THE DOCTOR'S DETAILS **/
    @GET("fetchDoctorDetails")
    Call<DoctorDetail> fetchDoctorDetails(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE LIST OF DOCTORS **/
    @GET("fetchDoctorsList")
    Call<Doctors> fetchDoctorsList(
            @Query("cityID") String cityID,
            @Query("localityID") String localityID);

    /** FETCH THE LIST OF DOCTORS **/
    @GET("fetchDoctorsListTest")
    Call<Doctors> fetchDoctorsListTest(
            @Query("cityID") String cityID,
            /*@Query("localityID") String localityID,*/
            @Query("pageNumber") String pageNumber);

    /** FETCH THE TOTAL NUMBER OF DOCTOR PAGES **/
    @GET("fetchDoctorPages")
    Call<DoctorPages> fetchDoctorPages(
            @Query("cityID") String cityID,
            @Query("localityID") String localityID);
}