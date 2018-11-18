package co.zenpets.doctors.utils.models.doctors.clients;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DoctorClientsAPI {

    /** GET THE LIST OF THE DOCTOR'S CLIENTS ***/
    @GET("fetchDoctorClients")
    Call<Clients> fetchDoctorClients(@Query("doctorID") String doctorID);

    /** ADD A NEW CLIENT TO THE DOCTOR'S LIST OF CLIENTS **/
    @POST("newClient")
    @FormUrlEncoded
    Call<Client> newClient(
            @Field("doctorID") String doctorID,
            @Field("clientName") String clientName,
            @Field("clientPhonePrefix") String clientPhonePrefix,
            @Field("clientPhoneNumber") String clientPhoneNumber);
}