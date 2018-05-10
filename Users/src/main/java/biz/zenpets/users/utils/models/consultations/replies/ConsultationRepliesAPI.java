package biz.zenpets.users.utils.models.consultations.replies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ConsultationRepliesAPI {

    /** FETCH CONSULTATIONS **/
    @GET("fetchConsultationReplies")
    Call<ConsultationReplies> fetchConsultationReplies(@Query("consultationID") String consultationID);
}