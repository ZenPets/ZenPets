package co.zenpets.doctors.utils.models.doctors.clients;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DoctorClientsAPI {
    @GET("fetchDoctorClients")
    Call<Clients> fetchDoctorClients(@Query("doctorID") String doctorID);
}