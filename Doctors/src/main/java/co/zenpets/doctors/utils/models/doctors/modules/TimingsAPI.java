package co.zenpets.doctors.utils.models.doctors.modules;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TimingsAPI {

    /* FETCH THE DOCTOR'S TIMINGS */
    @GET("fetchDoctorTimings")
    Call<Timings> fetchDoctorTimings(@Query("doctorID") String doctorID, @Query("clinicID") String clinicID);

    /* ADD A NEW TIMINGS */
    @POST("newDoctorTimings")
    @FormUrlEncoded
    Call<Timings> newDoctorTimings(
            @Field("doctorID") String doctorID,
            @Field("clinicID") String clinicID,
            @Field("sunMorFrom") String sunMorFrom,
            @Field("sunMorTo") String sunMorTo,
            @Field("sunAftFrom") String sunAftFrom,
            @Field("sunAftTo") String sunAftTo,
            @Field("monMorFrom") String monMorFrom,
            @Field("monMorTo") String monMorTo,
            @Field("monAftFrom") String monAftFrom,
            @Field("monAftTo") String monAftTo,
            @Field("tueMorFrom") String tueMorFrom,
            @Field("tueMorTo") String tueMorTo,
            @Field("tueAftFrom") String tueAftFrom,
            @Field("tueAftTo") String tueAftTo,
            @Field("wedMorFrom") String wedMorFrom,
            @Field("wedMorTo") String wedMorTo,
            @Field("wedAftFrom") String wedAftFrom,
            @Field("wedAftTo") String wedAftTo,
            @Field("thuMorFrom") String thuMorFrom,
            @Field("thuMorTo") String thuMorTo,
            @Field("thuAftFrom") String thuAftFrom,
            @Field("thuAftTo") String thuAftTo,
            @Field("friMorFrom") String friMorFrom,
            @Field("friMorTo") String friMorTo,
            @Field("friAftFrom") String friAftFrom,
            @Field("friAftTo") String friAftTo,
            @Field("satMorFrom") String satMorFrom,
            @Field("satMorTo") String satMorTo,
            @Field("satAftFrom") String satAftFrom,
            @Field("satAftTo") String satAftTo);

    /* ADD A NEW TIMINGS */
    @POST("updateDoctorTimings")
    @FormUrlEncoded
    Call<Timings> updateDoctorTimings(
            @Field("doctorID") String doctorID,
            @Field("clinicID") String clinicID,
            @Field("sunMorFrom") String sunMorFrom,
            @Field("sunMorTo") String sunMorTo,
            @Field("sunAftFrom") String sunAftFrom,
            @Field("sunAftTo") String sunAftTo,
            @Field("monMorFrom") String monMorFrom,
            @Field("monMorTo") String monMorTo,
            @Field("monAftFrom") String monAftFrom,
            @Field("monAftTo") String monAftTo,
            @Field("tueMorFrom") String tueMorFrom,
            @Field("tueMorTo") String tueMorTo,
            @Field("tueAftFrom") String tueAftFrom,
            @Field("tueAftTo") String tueAftTo,
            @Field("wedMorFrom") String wedMorFrom,
            @Field("wedMorTo") String wedMorTo,
            @Field("wedAftFrom") String wedAftFrom,
            @Field("wedAftTo") String wedAftTo,
            @Field("thuMorFrom") String thuMorFrom,
            @Field("thuMorTo") String thuMorTo,
            @Field("thuAftFrom") String thuAftFrom,
            @Field("thuAftTo") String thuAftTo,
            @Field("friMorFrom") String friMorFrom,
            @Field("friMorTo") String friMorTo,
            @Field("friAftFrom") String friAftFrom,
            @Field("friAftTo") String friAftTo,
            @Field("satMorFrom") String satMorFrom,
            @Field("satMorTo") String satMorTo,
            @Field("satAftFrom") String satAftFrom,
            @Field("satAftTo") String satAftTo);

    /** FETCH THE DOCTOR'S SUNDAY MORNING TIMINGS **/
    @GET("fetchSundayMorningTimings")
    Call<Timings> fetchSundayMorningTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S SUNDAY AFTERNOON TIMINGS **/
    @GET("fetchSundayAfternoonTimings")
    Call<Timings> fetchSundayAfternoonTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S MONDAY MORNING TIMINGS **/
    @GET("fetchMondayMorningTimings")
    Call<Timings> fetchMondayMorningTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S MONDAY AFTERNOON TIMINGS **/
    @GET("fetchMondayAfternoonTimings")
    Call<Timings> fetchMondayAfternoonTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S TUESDAY MORNING TIMINGS **/
    @GET("fetchTuesdayMorningTimings")
    Call<Timings> fetchTuesdayMorningTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S TUESDAY AFTERNOON TIMINGS **/
    @GET("fetchTuesdayAfternoonTimings")
    Call<Timings> fetchTuesdayAfternoonTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S WEDNESDAY MORNING TIMINGS **/
    @GET("fetchWednesdayMorningTimings")
    Call<Timings> fetchWednesdayMorningTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S WEDNESDAY AFTERNOON TIMINGS **/
    @GET("fetchWednesdayAfternoonTimings")
    Call<Timings> fetchWednesdayAfternoonTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S THURSDAY MORNING TIMINGS **/
    @GET("fetchThursdayMorningTimings")
    Call<Timings> fetchThursdayMorningTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S THURSDAY AFTERNOON TIMINGS **/
    @GET("fetchThursdayAfternoonTimings")
    Call<Timings> fetchThursdayAfternoonTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S FRIDAY MORNING TIMINGS **/
    @GET("fetchFridayMorningTimings")
    Call<Timings> fetchFridayMorningTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S FRIDAY AFTERNOON TIMINGS **/
    @GET("fetchFridayAfternoonTimings")
    Call<Timings> fetchFridayAfternoonTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S SATURDAY MORNING TIMINGS **/
    @GET("fetchSaturdayMorningTimings")
    Call<Timings> fetchSaturdayMorningTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S SATURDAY AFTERNOON TIMINGS **/
    @GET("fetchSaturdayAfternoonTimings")
    Call<Timings> fetchSaturdayAfternoonTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** CHECK THE AVAILABILITY OF THE TIMING SLOTS **/
    @GET("checkAvailability")
    Call<TimeSlot> checkAvailability(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID,
            @Query("appointmentDate") String appointmentDate,
            @Query("appointmentTime") String appointmentTime);
}