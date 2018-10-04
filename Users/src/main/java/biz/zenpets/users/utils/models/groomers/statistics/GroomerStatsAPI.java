package biz.zenpets.users.utils.models.groomers.statistics;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface GroomerStatsAPI {

    /** PUBLISH A NEW GROOMER VIEWED STATUS **/
    @POST("publishGroomerViewStatus")
    @FormUrlEncoded
    Call<GroomerStats> publishGroomerViewStatus(
            @Field("groomerID") String groomerID,
            @Field("userID") String userID,
            @Field("groomerViewDate") String groomerViewDate);

    /** PUBLISH A NEW GROOMER CLICK STATUS **/
    @POST("publishGroomerClickStatus")
    @FormUrlEncoded
    Call<GroomerStats> publishGroomerClickStatus(
            @Field("groomerID") String groomerID,
            @Field("userID") String userID,
            @Field("groomerClickDate") String groomerClickDate);
}