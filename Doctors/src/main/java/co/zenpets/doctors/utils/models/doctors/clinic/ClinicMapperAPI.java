package co.zenpets.doctors.utils.models.doctors.clinic;

import co.zenpets.doctors.utils.models.clinics.map.ClinicMapper;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ClinicMapperAPI {

    /** MAP A DOCTOR TO A CLINIC **/
    @POST("newDoctorClinic")
    @FormUrlEncoded
    Call<ClinicMapper> newDoctorClinic(
            @Field("doctorID") String doctorID,
            @Field("clinicID") String clinicID,
            @Field("clinicVerified") String clinicVerified);
}