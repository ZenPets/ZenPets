package biz.zenpets.users.utils.models.doctors.timings;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TimingsAPI {

    /** FETCH THE DOCTOR'S TIMINGS AT A CLINIC **/
    @GET("fetchDoctorTimings")
    Call<Timing> fetchDoctorTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S SUNDAY MORNING TIMINGS **/
    @GET("fetchSundayMorningTimings")
    Call<Timing> fetchSundayMorningTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S SUNDAY AFTERNOON TIMINGS **/
    @GET("fetchSundayAfternoonTimings")
    Call<Timing> fetchSundayAfternoonTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S MONDAY MORNING TIMINGS **/
    @GET("fetchMondayMorningTimings")
    Call<Timing> fetchMondayMorningTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S MONDAY AFTERNOON TIMINGS **/
    @GET("fetchMondayAfternoonTimings")
    Call<Timing> fetchMondayAfternoonTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S TUESDAY MORNING TIMINGS **/
    @GET("fetchTuesdayMorningTimings")
    Call<Timing> fetchTuesdayMorningTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S TUESDAY AFTERNOON TIMINGS **/
    @GET("fetchTuesdayAfternoonTimings")
    Call<Timing> fetchTuesdayAfternoonTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S WEDNESDAY MORNING TIMINGS **/
    @GET("fetchWednesdayMorningTimings")
    Call<Timing> fetchWednesdayMorningTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S WEDNESDAY AFTERNOON TIMINGS **/
    @GET("fetchWednesdayAfternoonTimings")
    Call<Timing> fetchWednesdayAfternoonTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S THURSDAY MORNING TIMINGS **/
    @GET("fetchThursdayMorningTimings")
    Call<Timing> fetchThursdayMorningTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S THURSDAY AFTERNOON TIMINGS **/
    @GET("fetchThursdayAfternoonTimings")
    Call<Timing> fetchThursdayAfternoonTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S FRIDAY MORNING TIMINGS **/
    @GET("fetchFridayMorningTimings")
    Call<Timing> fetchFridayMorningTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S FRIDAY AFTERNOON TIMINGS **/
    @GET("fetchFridayAfternoonTimings")
    Call<Timing> fetchFridayAfternoonTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S SATURDAY MORNING TIMINGS **/
    @GET("fetchSaturdayMorningTimings")
    Call<Timing> fetchSaturdayMorningTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** FETCH THE DOCTOR'S SATURDAY AFTERNOON TIMINGS **/
    @GET("fetchSaturdayAfternoonTimings")
    Call<Timing> fetchSaturdayAfternoonTimings(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID);

    /** CHECK SLOT AVAILABILITY **/
    @GET("checkAvailability")
    Call<TimeSlot> checkAvailability(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID,
            @Query("appointmentDate") String appointmentDate,
            @Query("appointmentTime") String appointmentTime);
}