package co.zenpets.trainers.utils.models.users;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UsersAPI {

    /** FETCH USER'S PROFILE DETAILS **/
    @GET("fetchUserDetails")
    Call<UserData> fetchUserDetails(@Query("userID") String userID);
}