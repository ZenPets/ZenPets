package co.zenpets.doctors.utils.models.users;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UsersAPI {

    /** FETCH A USER'S TOKEN (FOR NOTIFICATIONS) **/
    @GET("fetchUserToken")
    Call<UserData> fetchUserToken(@Query("userID") String userID);
}