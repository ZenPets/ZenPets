package biz.zenpets.users.utils.models.trainers.modules;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ModulesAPI {

    /** FETCH THE TRAINER'S TRAINING MODULES **/
    @GET("fetchTrainerModules")
    Call<Modules> fetchTrainerModules(@Query("trainerID") String trainerID);

    /** FETCH A SUBSET OF THE TRAINER'S TRAINING MODULES **/
    @GET("fetchTrainerModulesSubset")
    Call<Modules> fetchTrainerModulesSubset(@Query("trainerID") String trainerID);

    /** FETCH THE TRAINER'S TRAINING MODULES **/
    @GET("fetchModuleDetails")
    Call<Module> fetchModuleDetails(@Query("trainerModuleID") String trainerModuleID);

    /** FETCH THE TRAINERS TRAINING MODULES COUNT **/
    @GET("fetchTrainingModulesCount")
    Call<ModulesCount> fetchTrainingModulesCount(@Query("trainerID") String trainerID);

    /** GET THE TRAINERS TRAINING MODULES MINIMUM AND MAXIMUM FEES RANGE **/
    @GET("fetchTrainingModulesMinMax")
    Call<ModulesMinMax> fetchTrainingModulesMinMax(@Query("trainerID") String trainerID);
}