package biz.zenpets.trainers.utils.models.trainers.reviews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ReviewsAPI {

    /** FETCH THE TRAINER'S REVIEWS **/
    @GET("fetchTrainerReviews")
    Call<Reviews> fetchTrainerReviews(@Query("trainerID") String trainerID);

    /** FETCH A SUBSET OF THE TRAINER'S REVIEWS **/
    @GET("fetchTrainerReviewsSubset")
    Call<Reviews> fetchTrainerReviewsSubset(@Query("trainerID") String trainerID);
}