package co.zenpets.users.utils.models.consultations.votes;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Votes {

    @SerializedName("error") private Boolean error;
    @SerializedName("votes") private ArrayList<Vote> votes = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Vote> getVotes() {
        return votes;
    }

    public void setVotes(ArrayList<Vote> votes) {
        this.votes = votes;
    }
}