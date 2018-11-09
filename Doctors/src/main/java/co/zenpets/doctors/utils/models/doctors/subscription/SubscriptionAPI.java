package co.zenpets.doctors.utils.models.doctors.subscription;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SubscriptionAPI {

    /* GET A DOCTOR'S SUBSCRIPTION */
    @GET("fetchDoctorSubscription")
    Call<Subscription> fetchDoctorSubscription(@Query("doctorID") String doctorID);

    /* ADD A NEW DOCTOR SUBSCRIPTION */
    @POST("newDoctorSubscription")
    @FormUrlEncoded
    Call<Subscription> newDoctorSubscription(
            @Field("doctorID") String doctorID,
            @Field("validFrom") String validFrom,
            @Field("validTo") String validTo,
            @Field("subscriptionNote") String subscriptionNote);
}