package co.zenpets.doctors.utils.models.doctors.account;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SignUpAPI {

    /* CREATE A NEW DOCTOR ACCOUNT */
    @POST("newDoctor")
    @FormUrlEncoded
    Call<SignUp> newDoctor(
            @Field("doctorAuthID") String doctorAuthID,
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
            @Field("doctorCharges") String doctorCharges,
            @Field("doctorDisplayProfile") String doctorDisplayProfile);

    /* UPDATE A DOCTOR'S ACCOUNT */
    @POST("updateDoctor")
    @FormUrlEncoded
    Call<AccountData> updateDoctor(
            @Field("doctorID") String doctorID,
            @Field("doctorAuthID") String doctorAuthID,
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
            @Field("doctorCharges") String doctorCharges,
            @Field("doctorDisplayProfile") String doctorDisplayProfile,
            @Field("doctorClaimed") String doctorClaimed,
            @Field("doctorToken") String doctorToken);
}