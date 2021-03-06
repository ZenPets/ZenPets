package co.zenpets.users.utils.models.appointment;

import co.zenpets.users.utils.models.appointment.client.Client;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AppointmentsAPI {

    /** FETCH THE USER'S UPCOMING APPOINTMENTS **/
    @GET("fetchUserUpcomingAppointments")
    Call<Appointments> fetchUserUpcomingAppointments(
            @Query("userID") String userID,
            @Query("appointmentDate") String appointmentDate);

    /** FETCH THE USER'S PAST APPOINTMENTS **/
    @GET("fetchUserPastAppointments")
    Call<Appointments> fetchUserPastAppointments(
            @Query("userID") String userID,
            @Query("appointmentDate") String appointmentDate);

    /** ADD A NEW APPOINTMENT (BY USER) **/
    @POST("newVetAppointment")
    @FormUrlEncoded
    Call<Appointment> newVetAppointment(
            @Field("doctorID") String doctorID/* SAVE THE NEW CLIENT RECORD */,
            @Field("clinicID") String clinicID,
            @Field("visitReasonID") String visitReasonID,
            @Field("userID") String userID,
            @Field("petID") String petID,
            @Field("appointmentDate") String appointmentDate,
            @Field("appointmentTime") String appointmentTime,
            @Field("appointmentStatus") String appointmentStatus,
            @Field("appointmentNote") String appointmentNote,
            @Field("appointmentTimestamp") String appointmentTimestamp);

    /** CHECK IF THE USER EXISTS IN THE DOCTOR'S CLIENTS RECORDS **/
    @GET("clientExists")
    Call<Client> clientExists(@Query("userID") String userID);

    /** ADD THE USER TO THE DOCTOR'S CLIENTS RECORD **/
    @POST("newClient")
    @FormUrlEncoded
    Call<Client> newClient(
            @Field("doctorID") String doctorID,
            @Field("userID") String userID);

    /** FETCH APPOINTMENT DETAILS **/
    @GET("fetchAppointmentDetails")
    Call<Appointment> fetchAppointmentDetails(@Query("appointmentID") String appointmentID);
}