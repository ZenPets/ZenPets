package co.zenpets.doctors.utils.models.doctors.modules;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServicesAPI {

    /* GET ALL SERVICES */
    @GET("fetchDoctorServices")
    Call<Services> fetchDoctorServices(@Query("doctorID") String doctorID);

    /* ADD A NEW SERVICE */
    @POST("newDoctorService")
    @FormUrlEncoded
    Call<Service> newDoctorService(
            @Field("doctorID") String doctorID,
            @Field("doctorServiceName") String doctorServiceName);

    /* UPDATE AN EXISTING SERVICE */
    @POST("updateServiceDetails")
    @FormUrlEncoded
    Call<Service> updateServiceDetails(
            @Field("doctorServiceID") String doctorServiceID,
            @Field("doctorID") String doctorID,
            @Field("doctorServiceName") String doctorServiceName);

    /* DELETE AN EXISTING SERVICE */
    @POST("deleteService")
    @FormUrlEncoded
    Call<Service> deleteService(@Field("doctorServiceID") String doctorServiceID);
}