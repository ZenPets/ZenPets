package biz.zenpets.users.utils.models.consultations.consultations;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ConsultationsAPI {

    /** ADD A NEW PUBLIC CONSULTATION **/
    @POST("newConsultation")
    @FormUrlEncoded
    Call<Consultation> newConsultation(
            @Field("userID") String userID,
            @Field("petID") String petID,
            @Field("problemID") String problemID,
            @Field("consultationTitle") String consultationTitle,
            @Field("consultationDescription") String consultationDescription,
            @Field("consultationPicture") String consultationPicture,
            @Field("consultationTimestamp") String consultationTimestamp);

    /** FETCH CONSULTATIONS **/
    @GET("fetchConsultations")
    Call<Consultations> fetchConsultations(@Query("problemID") String problemID);

    /** FETCH A USER'S LIST OF PUBLIC CONSULTATIONS **/
    @GET("listUserConsultations")
    Call<Consultations> listUserConsultations(
            @Query("userID") String userID,
            @Query("problemID") String problemID);

    /** FETCH CONSULTATIONS **/
    @GET("consultationDetails")
    Call<Consultation> consultationDetails(@Query("consultationID") String consultationID);

    /** FETCH CONSULTATION VIEWS **/
    @GET("fetchConsultationViews")
    Call<Consultations> fetchConsultationViews(@Query("problemID") String problemID);

    /** FETCH CONSULTATION REPLIES **/
    @GET("fetchConsultationReplies")
    Call<Consultations> fetchConsultationReplies(@Query("problemID") String problemID);
}