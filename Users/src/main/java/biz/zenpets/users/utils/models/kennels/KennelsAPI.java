package biz.zenpets.users.utils.models.kennels;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface KennelsAPI {

    /** REGISTER A NEW KENNEL **/
    @POST("registerNewKennel")
    @FormUrlEncoded
    Call<Kennel> registerNewKennel(
            @Field("kennelOwnerID") String kennelOwnerID,
            @Field("kennelName") String kennelName,
            @Field("kennelAddress") String kennelAddress,
            @Field("kennelPinCode") String kennelPinCode,
            @Field("countryID") String countryID,
            @Field("stateID") String stateID,
            @Field("cityID") String cityID,
            @Field("kennelLatitude") String kennelLatitude,
            @Field("kennelLongitude") String kennelLongitude,
            @Field("kennelPhonePrefix1") String kennelPhonePrefix1,
            @Field("kennelPhoneNumber1") String kennelPhoneNumber1,
            @Field("kennelPhonePrefix2") String kennelPhonePrefix2,
            @Field("kennelPhoneNumber2") String kennelPhoneNumber2);

    /** FETCH THE LIST OF A USER'S PETS **/
    @GET("fetchKennelsListByCity")
    Call<Kennels> fetchKennelsListByCity(
            @Query("cityID") String cityID,
            @Query("pageNumber") String pageNumber);
}