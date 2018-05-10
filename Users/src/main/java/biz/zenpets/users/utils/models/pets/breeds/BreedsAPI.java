package biz.zenpets.users.utils.models.pets.breeds;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BreedsAPI {

    /** FETCH BREEDS FROM THE SELECTED PET TYPE **/
    @GET("allPetBreeds")
    Call<Breeds> allPetBreeds(@Query("petTypeID") String petTypeID);
}