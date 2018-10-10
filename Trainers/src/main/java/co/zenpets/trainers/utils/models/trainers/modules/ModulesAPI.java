package co.zenpets.trainers.utils.models.trainers.modules;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ModulesAPI {

    /** CREATE A NEW TRAINER MODULE **/
    @POST("newTrainerModule")
    @FormUrlEncoded
    Call<Module> newTrainerModule(
            @Field("trainerID") String trainerID,
            @Field("trainerModuleName") String trainerModuleName,
            @Field("trainerModuleDuration") String trainerModuleDuration,
            @Field("trainerModuleDurationUnit") String trainerModuleDurationUnit,
            @Field("trainerModuleSessions") String trainerModuleSessions,
            @Field("trainerModuleDetails") String trainerModuleDetails,
            @Field("trainerModuleFormat") String trainerModuleFormat,
            @Field("trainerModuleSize") String trainerModuleSize,
            @Field("trainerModuleFees") String trainerModuleFees);

    /** UPDATE A TRAINING MODULE **/
    @POST("updateTrainerModule")
    @FormUrlEncoded
    Call<Module> updateTrainerModule(
            @Field("trainerModuleID") String trainerModuleID,
            @Field("trainerModuleName") String trainerModuleName,
            @Field("trainerModuleDuration") String trainerModuleDuration,
            @Field("trainerModuleDurationUnit") String trainerModuleDurationUnit,
            @Field("trainerModuleSessions") String trainerModuleSessions,
            @Field("trainerModuleDetails") String trainerModuleDetails,
            @Field("trainerModuleFormat") String trainerModuleFormat,
            @Field("trainerModuleSize") String trainerModuleSize,
            @Field("trainerModuleFees") String trainerModuleFees);

    /** FETCH THE TRAINER'S TRAINING MODULES **/
    @GET("fetchTrainerModules")
    Call<Modules> fetchTrainerModules(@Query("trainerID") String trainerID);

    /** FETCH A SUBSET OF THE TRAINER'S TRAINING MODULES **/
    @GET("fetchTrainerModulesSubset")
    Call<Modules> fetchTrainerModulesSubset(@Query("trainerID") String trainerID);

    /** FETCH THE TRAINER'S TRAINING MODULES **/
    @GET("fetchModuleDetails")
    Call<Module> fetchModuleDetails(@Query("trainerModuleID") String trainerModuleID);

    /** DELETE A TRAINING MODULE **/
    @POST("deleteTrainingModule")
    @FormUrlEncoded
    Call<Module> deleteTrainingModule(@Field("trainerModuleID") String trainerModuleID);
}