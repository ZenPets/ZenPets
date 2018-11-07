package co.zenpets.doctors.utils.models.doctors.modules;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SpecializationsAPI {

    @GET("fetchDoctorSpecializations")
    Call<Specializations> fetchDoctorSpecializations(@Query("doctorID") String doctorID);

    @GET("fetchSpecializationDetails")
    Call<Specializations> fetchSpecializationDetails(@Query("doctorSpecializationID") String doctorSpecializationID);

    /* ADD A NEW SPECIALIZATION */
    @POST("newDoctorSpecialization")
    @FormUrlEncoded
    Call<Specialization> newDoctorSpecialization(
            @Field("doctorID") String doctorID,
            @Field("doctorSpecializationName") String doctorSpecializationName);

    /* UPDATE AN EXISTING SPECIALIZATION */
    @POST("updateSpecializationDetails")
    @FormUrlEncoded
    Call<Specialization> updateSpecializationDetails(
            @Field("doctorSpecializationID") String doctorSpecializationID,
            @Field("doctorID") String doctorID,
            @Field("doctorSpecializationName") String doctorSpecializationName);

    /* DELETE AN EXISTING SPECIALIZATION */
    @POST("deleteSpecialization")
    @FormUrlEncoded
    Call<Specialization> deleteSpecialization(@Field("doctorSpecializationID") String doctorSpecializationID);
}