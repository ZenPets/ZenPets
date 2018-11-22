package co.zenpets.kennels.utils.models.inventory;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TypesAPI {

    /** FETCH THE KENNEL'S INVENTORY LISTINGS **/
    @GET("fetchInventoryTypes")
    Call<Types> fetchInventoryTypes();
}