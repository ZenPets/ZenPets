package co.zenpets.users.utils.models.kennels.inventory;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface KennelInventoryAPI {

    /** FETCH THE INVENTORY ITEM DETAILS **/
    @GET("fetchInventoryDetails")
    Call<KennelInventory> fetchInventoryDetails(@Query("kennelInventoryID") String kennelInventoryID);
}