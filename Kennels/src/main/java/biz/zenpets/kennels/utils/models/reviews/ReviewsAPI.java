package biz.zenpets.kennels.utils.models.reviews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ReviewsAPI {

    /** FETCH ALL REVIEWS OF A KENNEL **/
    @GET("fetchKennelReviews")
    Call<Reviews> fetchKennelReviews(@Query("kennelID") String kennelID);
}