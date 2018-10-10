package co.zenpets.users.utils.models.pets.records;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MedicalRecordsAPI {

    /** ADD A NEW MEDICAL RECORD FOR A PET **/
    @POST("newMedicalRecord")
    @FormUrlEncoded
    Call<MedicalRecord> newMedicalRecord(
            @Field("recordTypeID") String recordTypeID,
            @Field("userID") String userID,
            @Field("petID") String petID,
            @Field("medicalRecordNotes") String medicalRecordNotes,
            @Field("medicalRecordDate") String medicalRecordDate);

//    /** FETCH A SUBSET OF THE PETS'S MEDICAL RECORDS **/
//    @GET("fetchPetMedicalRecordsSubset")
//    Call<MedicalRecords> fetchPetMedicalRecordsSubset(@Query("petID") String petID);

    /** FETCH THE PETS'S MEDICAL RECORDS **/
    @GET("fetchPetMedicalRecords")
    Call<MedicalRecords> fetchPetMedicalRecords(
            @Query("petID") String petID,
            @Query("recordTypeID") String recordTypeID);

    /** FETCH THE DOCTOR'S DETAILS **/
    @GET("fetchRecordDetails")
    Call<MedicalRecord> fetchRecordDetails(
            @Query("medicalRecordID") String medicalRecordID);

//    /** UPLOAD A NEW MEDICAL RECORD IMAGE **/
//    @POST("newMedicalImage")
//    @FormUrlEncoded
//    Call<RecordImagesData> newMedicalImage(
//            @Field("medicalRecordID") String medicalRecordID,
//            @Field("recordImageURL") String recordImageURL);

    /** FETCH THE MEDICAL RECORD'S IMAGES **/
    @GET("fetchMedicalImages")
    Call<MedicalImages> fetchMedicalImages(@Query("medicalRecordID") String medicalRecordID);

    /** UPDATE MEDICAL RECORD **/
    @POST("updateMedicalRecord")
    @FormUrlEncoded
    Call<MedicalRecord> updateMedicalRecord(
            @Field("medicalRecordID") String medicalRecordID,
            @Field("recordTypeID") String recordTypeID,
            @Field("userID") String userID,
            @Field("petID") String petID,
            @Field("medicalRecordNotes") String medicalRecordNotes,
            @Field("medicalRecordDate") String medicalRecordDate);

    /** DELETE A MEDICAL RECORD **/
    @POST("deleteRecord")
    @FormUrlEncoded
    Call<MedicalRecord> deleteRecord(@Field("medicalRecordID") String medicalRecordID);

    /** FETCH ALL MEDICAL RECORD TYPES **/
    @GET("allRecordTypes")
    Call<RecordTypes> allRecordTypes();
}