package co.zenpets.kennels.utils.models.bookings;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BookingsAPI {

    /** FETCH THE KENNEL BOOKINGS **/
    @GET("fetchKennelBookings")
    Call<Booking> fetchKennelBookings(@Query("kennelInventoryID") String kennelInventoryID);

    /** FETCH THE LAST KENNEL INVENTORY BOOKING DETAILS **/
    @GET("fetchLastInventoryBooking")
    Call<Booking> fetchLastInventoryBooking(@Query("kennelInventoryID") String kennelInventoryID);
}