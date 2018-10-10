package co.zenpets.users.utils.models.trainers.reviews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TrainerReviewsAPI {

    /** FETCH THE TRAINER'S REVIEWS **/
    @GET("fetchTrainerReviews")
    Call<TrainerReviews> fetchTrainerReviews(@Query("trainerID") String trainerID);

    /** FETCH A SUBSET OF THE TRAINER'S REVIEWS **/
    @GET("fetchTrainerReviewsSubset")
    Call<TrainerReviews> fetchTrainerReviewsSubset(@Query("trainerID") String trainerID);

    /** FETCH THE TRAINER'S REVIEWS COUNT **/
    @GET("fetchTrainerReviewCount")
    Call<TrainerReviewsCount> fetchTrainerReviewCount(@Query("trainerID") String trainerID);

    /** FETCH THE TRAINER'S POSITIVE REVIEWS **/
    @GET("fetchPositiveTrainerReviews")
    Call<TrainerReviews> fetchPositiveTrainerReviews(
            @Query("trainerID") String trainerID,
            @Query("recommendStatus") String recommendStatus);

    /** FETCH THE TRAINER'S NEGATIVE REVIEWS **/
    @GET("fetchNegativeTrainerReviews")
    Call<TrainerReviews> fetchNegativeTrainerReviews(
            @Query("trainerID") String trainerID,
            @Query("recommendStatus") String recommendStatus);
}