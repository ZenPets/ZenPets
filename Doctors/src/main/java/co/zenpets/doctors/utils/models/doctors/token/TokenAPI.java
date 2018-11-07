package co.zenpets.doctors.utils.models.doctors.token;

import co.zenpets.doctors.utils.models.doctors.profile.DoctorProfileData;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface TokenAPI {

    /* UPDATE A DOCTOR'S DEVICE TOKEN */
    @POST("updateDoctorToken")
    @FormUrlEncoded
    Call<DoctorProfileData> updateDoctorToken(
            @Field("doctorID") String doctorID,
            @Field("doctorToken") String doctorToken);
}