package co.zenpets.users.utils.models.kennels.statistics;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface StatisticsAPI {

    /** PUBLISH A NEW KENNEL VIEWED STATUS **/
    @POST("publishKennelViewStatus")
    @FormUrlEncoded
    Call<Stat> publishKennelViewStatus(
            @Field("kennelID") String kennelID,
            @Field("userID") String userID,
            @Field("kennelViewedDate") String kennelViewedDate);

    /** PUBLISH A NEW KENNEL DISPLAYED STATUS **/
    @POST("publishKennelDisplayStatus")
    @FormUrlEncoded
    Call<Stat> publishKennelDisplayStatus(
            @Field("kennelID") String kennelID,
            @Field("userID") String userID,
            @Field("kennelDisplayedDate") String kennelDisplayedDate);
}