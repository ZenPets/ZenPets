package co.zenpets.users.utils.models.appointment;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Appointments {

    @SerializedName("error") private Boolean error;
    @SerializedName("appointments") private ArrayList<Appointment> appointments = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(ArrayList<Appointment> appointments) {
        this.appointments = appointments;
    }
}