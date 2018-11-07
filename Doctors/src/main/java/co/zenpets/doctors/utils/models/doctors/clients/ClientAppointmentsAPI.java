package co.zenpets.doctors.utils.models.doctors.clients;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClientAppointmentsAPI {
    @GET("fetchClientAppointments")
    Call<ClientAppointments> getClientAppointments(@Query("doctorID") String doctorID, @Query("userID") String userID);
}