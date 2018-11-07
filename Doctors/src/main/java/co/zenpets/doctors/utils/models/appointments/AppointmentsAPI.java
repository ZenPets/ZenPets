package co.zenpets.doctors.utils.models.appointments;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AppointmentsAPI {

    /** CREATE A NEW APPOINTMENT **/
    @POST("newDocAppointment")
    @FormUrlEncoded
    Call<AppointmentData> newDocAppointment(
            @Field("doctorID") String doctorID,
            @Field("clinicID") String clinicID,
            @Field("visitReasonID") String visitReasonID,
            @Field("userID") String userID,
            @Field("petID") String petID,
            @Field("clientID") String clientID,
            @Field("appointmentDate") String appointmentDate,
            @Field("appointmentTime") String appointmentTime,
            @Field("appointmentStatus") String appointmentStatus,
            @Field("appointmentNote") String appointmentNote,
            @Field("appointmentTimestamp") String appointmentTimestamp);

    @GET("fetchDoctorTodayAppointments")
    Call<AppointmentsData> fetchDoctorTodayAppointments(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID,
            @Query("appointmentDate") String appointmentDate);

    @GET("fetchMonthlyAppointments")
    Call<AppointmentsData> fetchMonthlyAppointments(
            @Query("doctorID") String doctorID,
            @Query("clinicID") String clinicID,
            @Query("appointmentYear") String appointmentYear,
            @Query("appointmentMonth") String appointmentMonth);

    /* CONFIRM AN APPOINTMENT */
    @POST("updateAppointmentStatus")
    @FormUrlEncoded
    Call<AppointmentData> updateAppointmentStatus(
            @Field("appointmentID") String appointmentID,
            @Field("appointmentStatus") String appointmentStatus);

    /* CANCEL AN APPOINTMENT */
    @POST("cancelAppointment")
    @FormUrlEncoded
    Call<AppointmentData> cancelAppointment(
            @Field("appointmentID") String appointmentID,
            @Field("appointmentNote") String appointmentNote,
            @Field("appointmentStatus") String appointmentStatus);

    /** FETCH APPOINTMENT DETAILS **/
    @GET("fetchAppointmentDetails")
    Call<AppointmentData> fetchAppointmentDetails(@Query("appointmentID") String appointmentID);
}