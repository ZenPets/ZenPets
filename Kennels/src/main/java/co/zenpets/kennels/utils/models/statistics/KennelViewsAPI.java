package co.zenpets.kennels.utils.models.statistics;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface KennelViewsAPI {

    /** FETCH THE NUMBER OF KENNEL VIEWS IN THE PAST 30 OR 60 OR 90 DAYS **/
    @GET("fetchDashboardKennelViews")
    Call<KennelViews> fetchDashboardKennelViews(
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("kennelID") String kennelID);

    /** FETCH THE TOTAL NUMBER OF KENNEL VIEWS ON THE CURRENT DATE **/
    @GET("fetchTodaysKennelViews")
    Call<KennelView> fetchTodaysKennelViews(@Query("kennelID") String kennelID);

    /** FETCH THE TOTAL NUMBER OF KENNEL VIEWS IN CURRENT MONTH **/
    @GET("fetchMonthKennelViews")
    Call<KennelView> fetchMonthKennelViews(@Query("kennelID") String kennelID);

    /** FETCH THE TOTAL NUMBER OF KENNEL VIEWS IN CURRENT MONTH **/
    @GET("fetchCurrentWeekKennelViews")
    Call<KennelView> fetchCurrentWeekKennelViews(@Query("kennelID") String kennelID);
}