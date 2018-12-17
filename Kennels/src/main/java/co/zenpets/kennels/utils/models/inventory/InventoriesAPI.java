package co.zenpets.kennels.utils.models.inventory;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface InventoriesAPI {

    /** CREATE A NEW KENNEL INVENTORY RECORD **/
    @POST("createKennelInventoryRecord")
    @FormUrlEncoded
    Call<Inventory> createKennelInventoryRecord(
            @Field("inventoryTypeID") String inventoryTypeID,
            @Field("kennelID") String kennelID,
            @Field("kennelInventoryName") String kennelInventoryName,
            @Field("kennelInventoryPhoto") String kennelInventoryPhoto,
            @Field("kennelInventoryCost") String kennelInventoryCost,
            @Field("kennelInventoryStatus") String kennelInventoryStatus);

    /** CHECK THE INVENTORY ITEM'S UNIQUE NAME **/
    @GET("checkUniqueKennelInventory")
    Call<Inventory> checkUniqueKennelInventory(
            @Query("kennelID") String kennelID,
            @Query("kennelInventoryName") String kennelInventoryName,
            @Query("inventoryTypeID") String inventoryTypeID);

    /** FETCH THE KENNEL'S INVENTORY LISTINGS **/
    @GET("fetchKennelInventory")
    Call<Inventories> fetchKennelInventory(@Query("kennelID") String kennelID);
}