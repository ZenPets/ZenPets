package biz.zenpets.users.utils.models.adoptions.adoption;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AdoptionsAPI {

    /** CREATE A NEW ADOPTION LISTING **/
    @POST("newAdoption")
    @FormUrlEncoded
    Call<Adoption> newAdoption(
            @Field("petTypeID") String petTypeID,
            @Field("breedID") String breedID,
            @Field("userID") String userID,
            @Field("cityID") String cityID,
            @Field("adoptionName") String adoptionName,
            @Field("adoptionDescription") String adoptionDescription,
            @Field("adoptionGender") String adoptionGender,
            @Field("adoptionVaccinated") String adoptionVaccinated,
            @Field("adoptionDewormed") String adoptionDewormed,
            @Field("adoptionNeutered") String adoptionNeutered,
            @Field("adoptionTimeStamp") String adoptionTimeStamp,
            @Field("adoptionStatus") String adoptionStatus);

    /** CREATE A NEW ADOPTION LISTING (TEST) **/
    @POST("newTestAdoption")
    @FormUrlEncoded
    Call<Adoption> newTestAdoption(
            @Field("petTypeID") String petTypeID,
            @Field("breedID") String breedID,
            @Field("userID") String userID,
            @Field("cityID") String cityID,
            @Field("adoptionName") String adoptionName,
            @Field("adoptionCoverPhoto") String adoptionCoverPhoto,
            @Field("adoptionDescription") String adoptionDescription,
            @Field("adoptionGender") String adoptionGender,
            @Field("adoptionTimeStamp") String adoptionTimeStamp,
            @Field("adoptionStatus") String adoptionStatus);

    /** FETCH ALL ADOPTION IN THE SELECTED CITY **/
    @GET("fetchAdoptions")
    Call<Adoptions> fetchAdoptions(
            @Query("cityID") String cityID,
            @Query("petTypeName") String petTypeName,
            @Query("adoptionGender") String adoptionGender,
            @Query("pageNumber") String pageNumber);

    /** FETCH ALL ADOPTION IN THE SELECTED CITY (TEST) **/
    @GET("fetchAdoptionPages")
    Call<AdoptionPages> fetchAdoptionPages(
            @Query("cityID") String cityID,
            @Query("petTypeName") String petTypeName,
            @Query("adoptionGender") String adoptionGender);

    /** FETCH ALL ADOPTION IN THE SELECTED CITY (TEST) **/
    @GET("fetchTestAdoptions")
    Call<Adoptions> fetchTestAdoptions(
            @Query("cityID") String cityID,
            @Query("petTypeName") String petTypeName,
            @Query("adoptionGender") String adoptionGender,
            @Query("pageNumber") String pageNumber);

    /** FETCH ALL USER ADOPTION LISTING **/
    @GET("listUserAdoptions")
    Call<Adoptions> listUserAdoptions(@Query("userID") String userID);

    /** FETCH ALL USER ADOPTION LISTING (TEST) **/
    @GET("listTestUserAdoptions")
    Call<Adoptions> listTestUserAdoptions(@Query("userID") String userID);

    /** FETCH AN ADOPTION'S DETAILS **/
    @GET("fetchAdoptionDetails")
    Call<Adoption> fetchAdoptionDetails(@Query("adoptionID") String adoptionID);

    /** FETCH AN ADOPTION'S DETAILS (TEST) **/
    @GET("fetchTestAdoptionDetails")
    Call<Adoption> fetchTestAdoptionDetails(@Query("adoptionID") String adoptionID);

    /** CHANGE AN ADOPTION'S STATUS **/
    @POST("changeAdoptionStatus")
    @FormUrlEncoded
    Call<Adoption> changeAdoptionStatus(
            @Field("adoptionID") String adoptionID,
            @Field("adoptionStatus") String adoptionStatus);
}