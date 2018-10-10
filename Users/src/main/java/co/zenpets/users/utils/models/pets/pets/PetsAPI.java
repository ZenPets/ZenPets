package co.zenpets.users.utils.models.pets.pets;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PetsAPI {

//    /** ADD A NEW PET TO THE USER'S ACCOUNT **/
//    @POST("newPet")
//    @FormUrlEncoded
//    Call<Pet> newPet(
//            @Field("userID") String userID,
//            @Field("petTypeID") String petTypeID,
//            @Field("breedID") String breedID,
//            @Field("petName") String petName,
//            @Field("petGender") String petGender,
//            @Field("petDOB") String petDOB,
//            @Field("petNeutered") String petNeutered,
//            @Field("petDisplayProfile") String petDisplayProfile,
//            @Field("petActive") String petActive);

    /** FETCH THE LIST OF A USER'S PETS **/
    @GET("fetchUserPets")
    Call<Pets> fetchUserPets(@Query("userID") String userID);

    /** FETCH THE PET'S DETAILS **/
    @GET("fetchPetDetails")
    Call<Pet> fetchPetDetails(@Query("petID") String petID);
}