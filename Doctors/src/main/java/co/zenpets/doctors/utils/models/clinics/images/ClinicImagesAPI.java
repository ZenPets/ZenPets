package co.zenpets.doctors.utils.models.clinics.images;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ClinicImagesAPI {
    /* FETCH THE CLINIC'S IMAGES */
    @GET("fetchClinicImages")
    Call<ImagesData> fetchClinicImages(@Query("clinicID") String clinicID);

    /* ADD A CLINIC IMAGE */
    @POST("postClinicImages")
    @FormUrlEncoded
    Call<ImageData> postClinicImages(
            @Field("clinicID") String clinicID,
            @Field("imageURL") String imageURL);
}