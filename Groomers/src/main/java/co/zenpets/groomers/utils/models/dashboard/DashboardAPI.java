package co.zenpets.groomers.utils.models.dashboard;

import co.zenpets.groomers.utils.models.enquiries.Enquiries;
import co.zenpets.groomers.utils.models.reviews.Reviews;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DashboardAPI {

    /** FETCH A SUBSET OF A GROOMER'S REVIEWS **/
    @GET("fetchGroomerDashboardReviews")
    Call<Reviews> fetchGroomerDashboardReviews(@Query("groomerID") String groomerID);

    /** FETCH A SUBSET OF A GROOMER'S REVIEWS **/
    @GET("fetchGroomerDashboardEnquiries")
    Call<Enquiries> fetchGroomerDashboardEnquiries(@Query("groomerID") String groomerID);
}