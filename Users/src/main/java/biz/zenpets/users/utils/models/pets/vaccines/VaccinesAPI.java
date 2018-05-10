package biz.zenpets.users.utils.models.pets.vaccines;

import retrofit2.Call;
import retrofit2.http.GET;

public interface VaccinesAPI {

    /** FETCH ALL VACCINE TYPES **/
    @GET("allVaccines")
    Call<Vaccines> allVaccines();
}