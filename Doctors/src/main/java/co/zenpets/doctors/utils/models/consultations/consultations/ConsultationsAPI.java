package co.zenpets.doctors.utils.models.consultations.consultations;

import co.zenpets.doctors.utils.models.consultations.ReplyData;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ConsultationsAPI {

    /* ADD A NEW CONSULTATION REPLY */
    @POST("newConsultationReply")
    @FormUrlEncoded
    Call<ReplyData> newConsultationReply(
            @Field("consultationID") String consultationID,
            @Field("doctorID") String doctorID,
            @Field("replyText") String replyText,
            @Field("replyTimestamp") String replyTimestamp);

    /** FETCH CONSULTATIONS **/
    @GET("fetchConsultations")
    Call<Consultations> fetchConsultations(@Query("problemID") String problemID);

    /** FETCH CONSULTATIONS **/
    @GET("consultationDetails")
    Call<Consultation> consultationDetails(@Query("consultationID") String consultationID);
}