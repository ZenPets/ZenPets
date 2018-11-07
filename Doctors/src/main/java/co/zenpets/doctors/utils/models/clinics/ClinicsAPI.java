package co.zenpets.doctors.utils.models.clinics;

import co.zenpets.doctors.utils.models.doctors.clinic.Clinic;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ClinicsAPI {

    /* ADD A NEW CLINIC */
    @POST("newClinic")
    @FormUrlEncoded
    Call<Clinic> newClinic(
            @Field("doctorID") String doctorID,
            @Field("countryID") String countryID,
            @Field("stateID") String stateID,
            @Field("cityID") String cityID,
            @Field("localityID") String localityID,
            @Field("clinicName") String clinicName,
            @Field("clinicAddress") String clinicAddress,
            @Field("clinicLandmark") String clinicLandmark,
            @Field("clinicPinCode") String clinicPinCode,
            @Field("clinicLatitude") String clinicLatitude,
            @Field("clinicLongitude") String clinicLongitude,
            @Field("clinicLogo") String clinicLogo,
            @Field("clinicPhonePrefix1") String clinicPhonePrefix1,
            @Field("clinicPhone1") String clinicPhone1,
            @Field("clinicPhonePrefix2") String clinicPhonePrefix2,
            @Field("clinicPhone2") String clinicPhone2,
            @Field("clinicVerified") String clinicVerified);

    /* UPDATE THE CLINIC DETAILS */
    @POST("updateClinic")
    @FormUrlEncoded
    Call<Clinic> updateClinic(
            @Field("clinicID") String clinicID,
            @Field("clinicName") String clinicName,
            @Field("clinicAddress") String clinicAddress,
            @Field("clinicLandmark") String clinicLandmark,
            @Field("clinicPinCode") String clinicPinCode,
            @Field("clinicLatitude") String clinicLatitude,
            @Field("clinicLongitude") String clinicLongitude,
            @Field("clinicPhonePrefix1") String clinicPhonePrefix1,
            @Field("clinicPhone1") String clinicPhone1,
            @Field("clinicPhonePrefix2") String clinicPhonePrefix2,
            @Field("clinicPhone2") String clinicPhone2);
}