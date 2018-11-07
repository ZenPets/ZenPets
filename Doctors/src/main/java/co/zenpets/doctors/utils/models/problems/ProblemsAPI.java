package co.zenpets.doctors.utils.models.problems;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ProblemsAPI {

    /* GET ALL PROBLEM TYPES */
    @GET("allProblemTypes")
    Call<Problems> allProblemTypes();
}