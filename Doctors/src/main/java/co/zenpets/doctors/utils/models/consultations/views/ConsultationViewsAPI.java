package co.zenpets.doctors.utils.models.consultations.views;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ConsultationViewsAPI {

    /** FETCH CONSULTATIONS **/
    @GET("fetchConsultationViews")
    Call<ConsultationViews> fetchConsultationViews(@Query("consultationID") String consultationID);

    /** FETCH A USER'S CONSULTATION VIEW STATUS **/
    @GET("getConsultationViewStatus")
    Call<ConsultationView> getConsultationViewStatus(
            @Query("userID") String userID,
            @Query("consultationID") String consultationID);

    /** PUBLISH A USER - CONSULTATION VIEW RECORD **/
    @POST("newConsultationView")
    @FormUrlEncoded
    Call<ConsultationView> newConsultationView(
            @Field("userID") String userID,
            @Field("consultationID") String consultationID);
}