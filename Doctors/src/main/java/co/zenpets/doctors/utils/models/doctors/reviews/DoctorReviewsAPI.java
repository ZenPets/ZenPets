package co.zenpets.doctors.utils.models.doctors.reviews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DoctorReviewsAPI {

    /* GET ALL DOCTOR REVIEWS */
    @GET("fetchDoctorReviews")
    Call<ReviewsData> fetchDoctorReviews(@Query("doctorID") String doctorID);

    /* GET ALL DOCTOR POSITIVE REVIEWS */
    @GET("fetchPositiveReviews")
    Call<ReviewsData> fetchPositiveReviews(
            @Query("doctorID") String doctorID,
            @Query("recommendStatus") String recommendStatus);

    /* GET ALL DOCTOR POSITIVE REVIEWS */
    @GET("fetchNegativeReviews")
    Call<ReviewsData> fetchNegativeReviews(
            @Query("doctorID") String doctorID,
            @Query("recommendStatus") String recommendStatus);
}