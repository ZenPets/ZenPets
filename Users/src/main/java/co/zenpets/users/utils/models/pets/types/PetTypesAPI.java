package co.zenpets.users.utils.models.pets.types;

import co.zenpets.users.utils.models.location.Countries;
import retrofit2.Call;
import retrofit2.http.GET;

public interface PetTypesAPI {

    /* GET ALL COUNTRIES */
    @GET("petTypes")
    Call<PetTypes> petTypes();
}