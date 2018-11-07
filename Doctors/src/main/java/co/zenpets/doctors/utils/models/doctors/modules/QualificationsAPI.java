package co.zenpets.doctors.utils.models.doctors.modules;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface QualificationsAPI {

    @GET("fetchDoctorEducation")
    Call<Qualifications> fetchDoctorEducation(@Query("doctorID") String doctorID);

    @GET("fetchEducationDetails")
    Call<Qualification> fetchEducationDetails(@Query("doctorEducationID") String doctorEducationID);

    /* ADD A NEW EDUCATIONAL QUALIFICATION */
    @POST("newDoctorEducation")
    @FormUrlEncoded
    Call<Qualification> newDoctorEducation(
            @Field("doctorID") String doctorID,
            @Field("doctorCollegeName") String doctorCollegeName,
            @Field("doctorEducationName") String doctorEducationName,
            @Field("doctorEducationYear") String doctorEducationYear);

    /* UPDATE AN EDUCATIONAL QUALIFICATION */
    @POST("updateEducationDetails")
    @FormUrlEncoded
    Call<Qualification> updateEducationDetails(
            @Field("doctorEducationID") String doctorEducationID,
            @Field("doctorID") String doctorID,
            @Field("doctorCollegeName") String doctorCollegeName,
            @Field("doctorEducationName") String doctorEducationName,
            @Field("doctorEducationYear") String doctorEducationYear);

    /* DELETE AN EDUCATIONAL QUALIFICATION */
    @POST("deleteEducation")
    @FormUrlEncoded
    Call<Qualification> deleteEducation(@Field("doctorEducationID") String doctorEducationID);
}