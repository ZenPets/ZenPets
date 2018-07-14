package biz.zenpets.kennels.utils.models.images;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface KennelImagesAPI {

    /** FETCH ALL KENNEL IMAGES **/
    @GET("fetchKennelImages")
    Call<KennelImages> fetchKennelImages(@Query("kennelID") String kennelID);

    /** PUBLISH A NEW KENNEL IMAGE **/
    @POST("newKennelImage")
    @FormUrlEncoded
    Call<KennelImage> newKennelImage(
            @Field("kennelID") String kennelID,
            @Field("kennelImageURL") String kennelImageURL);

    /** UPDATE KENNEL IMAGE URL **/
    @POST("updateKennelImage")
    @FormUrlEncoded
    Call<KennelImage> updateKennelImage(
            @Field("kennelID") String kennelID,
            @Field("kennelImageURL") String kennelImageURL);

    /** DELETE A KENNEL IMAGE **/
    @POST("deleteKennelImage")
    @FormUrlEncoded
    Call<KennelImage> deleteKennelImage(@Field("kennelImageID") String kennelImageID);
}