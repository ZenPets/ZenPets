package biz.zenpets.users.utils.models.clinics.ratings;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ClinicRatingsAPI {

    /** POST A NEW CLINIC RATING **/
    @POST("newClinicRating")
    @FormUrlEncoded
    Call<ClinicRating> newClinicRating(
            @Field("clinicID") String clinicID,
            @Field("userID") String userID,
            @Field("clinicRating") String clinicRating);

    /** FETCH THE CLINICS RATING **/
    @GET("fetchClinicRatings")
    Call<ClinicRating> fetchClinicRatings(@Query("clinicID") String clinicID);
}