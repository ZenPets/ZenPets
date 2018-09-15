package biz.zenpets.users.utils.models.doctors.modules;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServicesAPI {

    /** FETCH  THE DOCTOR'S SERVICES **/
    @GET("fetchDoctorServices")
    Call<Services> fetchDoctorServices(@Query("doctorID") String doctorID);

    /** FETCH A SUBSET OF THE DOCTOR'S SERVICES **/
    @GET("fetchServicesSubset")
    Call<Services> fetchServicesSubset(@Query("doctorID") String doctorID);

    /** FETCH THE NUMBER OF A DOCTOR'S SERVICES **/
    @GET("fetchDoctorServicesCount")
    Call<ServicesCount> fetchDoctorServicesCount(@Query("doctorID") String doctorID);
}