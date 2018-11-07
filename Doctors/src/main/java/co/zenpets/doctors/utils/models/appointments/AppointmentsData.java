package co.zenpets.doctors.utils.models.appointments;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AppointmentsData {

    @SerializedName("appointments") private ArrayList<AppointmentData> appointments;

    public ArrayList<AppointmentData> getAppointments() {
        return appointments;
    }

    public void setAppointments(ArrayList<AppointmentData> appointments) {
        this.appointments = appointments;
    }
}