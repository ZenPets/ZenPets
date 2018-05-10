package biz.zenpets.users.utils.models.pets.prescriptions;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PrescriptionsAPI {

    /** FETCH THE PETS'S PRESCRIPTION RECORDS **/
    @GET("fetchPetPrescriptions")
    Call<Prescriptions> fetchPetPrescriptions(@Query("petID") String petID);

    /** DELETE A PRESCRIPTION RECORD **/
    @POST("deletePrescription")
    @FormUrlEncoded
    Call<Prescription> deletePrescription(@Field("medicalRecordID") String medicalRecordID);
}