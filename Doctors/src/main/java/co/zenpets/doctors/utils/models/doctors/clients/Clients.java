package co.zenpets.doctors.utils.models.doctors.clients;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Clients {

    @SerializedName("error") private Boolean error;
    @SerializedName("clients") private ArrayList<Client> clients = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }
}