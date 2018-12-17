package co.zenpets.users.utils.models.kennels.bookings;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BookingsAPI {

//    /** FETCH THE LIST OF AVAILABLE INVENTORY ITEMS ON THE SELECTED DATE / DATE RANGE **/
//    @GET("checkInventoryAvailability")
//    Call<Units> checkInventoryAvailability(
//            @Query("bookingFrom") String bookingFrom,
//            @Query("bookingTo") String bookingTo,
//            @Query("kennelID") String kennelID,
//            @Query("inventoryTypeID") String inventoryTypeID);

    /** FETCH THE LIST OF AVAILABLE INVENTORY ITEMS ON THE SELECTED DATE / DATE RANGE **/
    @GET("checkInventoryAvailability")
    Call<KennelAvailability> checkInventoryAvailability(
            @Query("bookingFrom") String bookingFrom,
            @Query("bookingTo") String bookingTo,
            @Query("kennelID") String kennelID,
            @Query("inventoryTypeID") String inventoryTypeID);
}