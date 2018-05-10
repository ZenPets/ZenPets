package biz.zenpets.users.utils.models.pets.vaccination;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface VaccinationsAPI {

    /** ADD A NEW VACCINATION RECORD FOR A PET **/
    @POST("newVaccination")
    @FormUrlEncoded
    Call<Vaccination> newVaccination(
            @Field("petID") String petID,
            @Field("vaccineID") String vaccineID,
            @Field("vaccinationDate") String vaccinationDate,
            @Field("vaccinationNextDate") String vaccinationNextDate,
            @Field("vaccinationReminder") String vaccinationReminder,
            @Field("vaccinationNotes") String vaccinationNotes,
            @Field("vaccinationPicture") String vaccinationPicture);

    /** FETCH A SUBSET OF THE PETS'S VACCINATION RECORDS **/
    @GET("fetchPetVaccinationsSubset")
    Call<Vaccinations> fetchPetVaccinationsSubset(@Query("petID") String petID);

    /** FETCH THE PETS'S VACCINATION RECORDS **/
    @GET("fetchPetVaccinations")
    Call<Vaccinations> fetchPetVaccinations(
            @Query("petID") String petID,
            @Query("vaccineID") String vaccineID);

    /** FETCH VACCINATION DETAILS **/
    @GET("fetchVaccinationDetails")
    Call<Vaccination> fetchVaccinationDetails(@Query("vaccinationID") String vaccinationID);

    /** UPDATE VACCINATION RECORD **/
    @POST("updateVaccinationRecord")
    @FormUrlEncoded
    Call<Vaccination> updateVaccinationRecord(
            @Field("vaccinationID") String vaccinationID,
            @Field("vaccineID") String vaccineID,
            @Field("vaccinationDate") String vaccinationDate,
            @Field("vaccinationNextDate") String vaccinationNextDate,
            @Field("vaccinationReminder") String vaccinationReminder,
            @Field("vaccinationNotes") String vaccinationNotes);

//    /** UPLOAD A NEW VACCINATION RECORD IMAGE **/
//    @POST("newVaccinationImage")
//    @FormUrlEncoded
//    Call<VaccinationImage> newVaccinationImage(
//            @Field("vaccinationID") String vaccinationID,
//            @Field("vaccinationImageURL") String vaccinationImageURL);

    /** FETCH THE VACCINATION RECORD IMAGES **/
    @GET("fetchVaccinationImages")
    Call<VaccinationImages> fetchVaccinationImages(@Query("vaccinationID") String vaccinationID);

    /** DELETE A VACCINATION RECORD **/
    @POST("deleteVaccination")
    @FormUrlEncoded
    Call<Vaccination> deleteVaccination(@Field("vaccinationID") String vaccinationID);
}