package biz.zenpets.users.utils.models.boarding;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BoardingsAPI {

    /** FETCH THE LIST OF HOME BOARDINGS IN A CITY **/
    @GET("fetchBoardingsList")
    Call<Boardings> fetchBoardingsList(
            @Query("cityID") String cityID,
            @Query("pageNumber") String pageNumber,
            @Query("originLat") String originLat,
            @Query("originLon") String originLon);

    /** FETCH THE TOTAL NUMBER OF BOARDING PAGES **/
    @GET("fetchBoardingPages")
    Call<BoardingPages> fetchBoardingPages(@Query("cityID") String cityID);
}