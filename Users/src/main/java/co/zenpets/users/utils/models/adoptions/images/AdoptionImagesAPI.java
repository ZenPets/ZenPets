package co.zenpets.users.utils.models.adoptions.images;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AdoptionImagesAPI {

    /** UPLOAD A NEW ADOPTION IMAGE **/
    @POST("postAdoptionImages")
    @FormUrlEncoded
    Call<AdoptionImage> postAdoptionImages(
            @Field("adoptionID") String adoptionID,
            @Field("imageURL") String imageURL);

    /** FETCH AN ADOPTION'S IMAGES **/
    @GET("fetchAdoptionImages")
    Call<AdoptionImages> fetchAdoptionImages(@Query("adoptionID") String adoptionID);

    /** FETCH AN ADOPTION'S IMAGES (TEST) **/
    @GET("fetchTestAdoptionImages")
    Call<AdoptionImages> fetchTestAdoptionImages(@Query("adoptionID") String adoptionID);
}