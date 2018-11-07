package co.zenpets.doctors.utils.models.doctors.clients;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ClientAppointments {

    @SerializedName("appointments") private ArrayList<ClientAppointment> appointments;

    public ArrayList<ClientAppointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(ArrayList<ClientAppointment> appointments) {
        this.appointments = appointments;
    }
}