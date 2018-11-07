package co.zenpets.doctors.utils.models.doctors.clients;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClientDetailsAPI {
    @GET("fetchClientDetails")
    Call<Client> fetchClientDetails(@Query("clientID") String clientID);
}