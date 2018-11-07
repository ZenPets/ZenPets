package co.zenpets.doctors.utils.models.visit;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ReasonsAPI {

    /** GET ALL VISIT REASONS **/
    @GET("visitReasons")
    Call<Reasons> visitReasons();
}