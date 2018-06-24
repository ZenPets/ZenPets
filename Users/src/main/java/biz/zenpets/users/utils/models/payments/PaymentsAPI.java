package biz.zenpets.users.utils.models.payments;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PaymentsAPI {

    /** CAPTURE AN AUTHORIZED PAYMENT USING RAZOR PAY**/
    @GET("json")
    Call<String> json(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("sensor") String sensor);
}