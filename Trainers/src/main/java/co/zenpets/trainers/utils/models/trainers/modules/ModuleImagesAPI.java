package co.zenpets.trainers.utils.models.trainers.modules;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ModuleImagesAPI {

    /** ADD A NEW TRAINING MODULE IMAGE **/
    @POST("newTrainingModuleImage")
    @FormUrlEncoded
    Call<ModuleImage> newTrainingModuleImage(
            @Field("trainerModuleID") String trainerModuleID,
            @Field("trainerModuleImageURL") String trainerModuleImageURL,
            @Field("trainerModuleImageCaption") String trainerModuleImageCaption);

    /** FETCH THE TRAINING MODULE IMAGES **/
    @GET("fetchTrainingModuleImages")
    Call<ModuleImages> fetchTrainingModuleImages(@Query("trainerModuleID") String trainerModuleID);

    /** DELETE A MODULE TRAINING IMAGE **/
    @POST("deleteTrainingModuleImage")
    @FormUrlEncoded
    Call<ModuleImage> deleteTrainingModuleImage(@Field("trainerModuleImageID") String trainerModuleImageID);
}