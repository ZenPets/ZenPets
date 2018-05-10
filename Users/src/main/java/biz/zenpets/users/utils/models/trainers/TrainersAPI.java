package biz.zenpets.users.utils.models.trainers;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TrainersAPI {

    /** FETCH THE LIST OF TRAINERS IN THE SELECTED CITY **/
    @GET("fetchTrainersList")
    Call<Trainers> fetchTrainersList(@Query("cityID") String cityID);

    /** FETCH THE TRAINER'S DETAILS **/
    @GET("fetchTrainerDetails")
    Call<Trainer> fetchTrainerDetails(@Query("trainerID") String trainerID);
}