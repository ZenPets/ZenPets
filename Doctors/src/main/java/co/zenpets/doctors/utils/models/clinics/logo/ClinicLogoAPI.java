package co.zenpets.doctors.utils.models.clinics.logo;

import co.zenpets.doctors.utils.models.clinics.ClinicData;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ClinicLogoAPI {

    /* UPDATE THE CLINIC'S LOGO */
    @POST("updateClinicLogo")
    @FormUrlEncoded
    Call<ClinicData> updateClinicLogo(
            @Field("clinicID") String clinicID,
            @Field("clinicLogo") String clinicLogo);
}