package co.zenpets.doctors.utils.models.consultations.replies;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ConsultationReplies {

    @SerializedName("error") private Boolean error;
    @SerializedName("replies") private ArrayList<ConsultationReply> replies = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<ConsultationReply> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<ConsultationReply> replies) {
        this.replies = replies;
    }
}