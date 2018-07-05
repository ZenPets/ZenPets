package biz.zenpets.users.utils.models.adoptions.notifications;

import biz.zenpets.users.utils.models.adoptions.messages.AdoptionMessages;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AdoptionNotificationAPI {

    /** FETCH USER'S PROFILE **/
    @GET("fetchAdoptionParticipants")
    Call<AdoptionMessages> fetchAdoptionParticipants(
            @Query("adoptionID") String adoptionID,
            @Query("userID") String userID);
}