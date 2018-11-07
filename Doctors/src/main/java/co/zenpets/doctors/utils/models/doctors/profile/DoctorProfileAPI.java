package co.zenpets.doctors.utils.models.doctors.profile;

import co.zenpets.doctors.utils.models.doctors.account.AccountData;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DoctorProfileAPI {
    @GET("fetchDoctorProfile")
    Call<DoctorProfileData> fetchDoctorProfile(@Query("doctorID") String doctorID);

    /* UPDATE A DOCTOR'S PROFILE */
    @POST("updateDoctorProfile")
    @FormUrlEncoded
    Call<AccountData> updateDoctorProfile(
            @Field("doctorID") String doctorID,
            @Field("doctorPrefix") String doctorPrefix,
            @Field("doctorName") String doctorName,
            @Field("doctorEmail") String doctorEmail,
            @Field("doctorPhonePrefix") String doctorPhonePrefix,
            @Field("doctorPhoneNumber") String doctorPhoneNumber,
            @Field("doctorAddress") String doctorAddress,
            @Field("countryID") String countryID,
            @Field("stateID") String stateID,
            @Field("cityID") String cityID,
            @Field("doctorGender") String doctorGender,
            @Field("doctorSummary") String doctorSummary,
            @Field("doctorExperience") String doctorExperience,
            @Field("doctorCharges") String doctorCharges);

    /* UPDATE A DOCTOR'S DISPLAY PROFILE */
    @POST("updateDoctorDisplayProfile")
    @FormUrlEncoded
    Call<AccountData> updateDoctorDisplayProfile(
            @Field("doctorID") String doctorID,
            @Field("doctorDisplayProfile") String doctorDisplayProfile);
}