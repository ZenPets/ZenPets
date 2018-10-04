package biz.zenpets.groomers.utils.models.images;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GroomerImagesAPI {

    /** FETCH ALL GROOMER IMAGES **/
    @GET("fetchGroomerImages")
    Call<GroomerImages> fetchGroomerImages(@Query("groomerID") String groomerID);

    /** PUBLISH A NEW GROOMER IMAGE **/
    @POST("newGroomerImage")
    @FormUrlEncoded
    Call<GroomerImage> newGroomerImage(
            @Field("groomerID") String groomerID,
            @Field("imageURL") String imageURL);

    /** UPDATE GROOMER IMAGE URL **/
    @POST("updateGroomerImage")
    @FormUrlEncoded
    Call<GroomerImage> updateGroomerImage(
            @Field("groomerID") String groomerID,
            @Field("imageURL") String imageURL);

    /** DELETE A GROOMER IMAGE **/
    @POST("deleteGroomerImage")
    @FormUrlEncoded
    Call<GroomerImage> deleteGroomerImage(@Field("imageID") String imageID);
}