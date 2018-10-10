package co.zenpets.users.utils.models.user;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UsersAPI {

    /** CHECK IF A USER'S RECORD EXISTS **/
    @GET("profileExists")
    Call<UserExistsData> profileExists(@Query("userAuthID") String userAuthID);

    /** CREATE A NEW USER RECORD **/
    @POST("register")
    @FormUrlEncoded
    Call<UserData> register(
            @Field("userAuthID") String userAuthID,
            @Field("userName") String userName,
            @Field("userEmail") String userEmail,
            @Field("userDisplayProfile") String userDisplayProfile,
            @Field("userPhonePrefix") String userPhonePrefix,
            @Field("userPhoneNumber") String userPhoneNumber,
            @Field("userGender") String userGender,
            @Field("countryID") String countryID,
            @Field("stateID") String stateID,
            @Field("cityID") String cityID,
            @Field("profileComplete") String profileComplete);

    /** FETCH USER'S PROFILE **/
    @GET("fetchProfile")
    Call<UserData> fetchProfile(@Query("userAuthID") String userAuthID);

    /** FETCH USER'S PROFILE DETAILS **/
    @GET("fetchUserDetails")
    Call<UserData> fetchUserDetails(@Query("userID") String userID);

    /** UPDATE THE USER'S RECORD **/
    @POST("updateProfile")
    @FormUrlEncoded
    Call<UserData> updateProfile(
            @Field("userEmail") String userEmail,
            @Field("userName") String userName,
            @Field("userPhonePrefix") String userPhonePrefix,
            @Field("userPhoneNumber") String userPhoneNumber,
            @Field("userGender") String userGender,
            @Field("countryID") String countryID,
            @Field("stateID") String stateID,
            @Field("cityID") String cityID);

    /** UPDATE THE USER'S DEVICE TOKEN **/
    @POST("updateUserToken")
    @FormUrlEncoded
    Call<UserData> updateUserToken(
            @Field("userID") String userID,
            @Field("userToken") String userToken);

    /** FETCH A USER'S TOKEN (FOR NOTIFICATIONS) **/
    @GET("fetchUserToken")
    Call<UserData> fetchUserToken(@Query("userID") String userID);
}