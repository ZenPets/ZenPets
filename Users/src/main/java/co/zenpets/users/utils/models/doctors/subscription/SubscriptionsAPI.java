package co.zenpets.users.utils.models.doctors.subscription;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SubscriptionsAPI {

    /** FETCH THE DOCTOR'S SUBSCRIPTION **/
    @GET("fetchDoctorSubscription")
    Call<SubscriptionData> fetchDoctorSubscription(@Query("doctorID") String doctorID);
}