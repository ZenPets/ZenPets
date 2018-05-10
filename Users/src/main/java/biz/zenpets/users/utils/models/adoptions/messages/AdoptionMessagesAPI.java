package biz.zenpets.users.utils.models.adoptions.messages;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AdoptionMessagesAPI {

    /** FETCH ADOPTION MESSAGES **/
    @GET("fetchAdoptionMessages")
    Call<AdoptionMessages> fetchAdoptionMessages(@Query("adoptionID") String adoptionID);

    /** POST A MESSAGE TO AN ADOPTION LISTING **/
    @POST("newAdoptionMessage")
    @FormUrlEncoded
    Call<AdoptionMessage> newAdoptionMessage(
            @Field("adoptionID") String adoptionID,
            @Field("userID") String userID,
            @Field("messageText") String messageText,
            @Field("messageTimeStamp") String messageTimeStamp);
}