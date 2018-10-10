package co.zenpets.users.utils.models.adoptions.messages;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AdoptionMessages {

    @SerializedName("error") private Boolean error;
    @SerializedName("messages") private ArrayList<AdoptionMessage> messages = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<AdoptionMessage> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<AdoptionMessage> messages) {
        this.messages = messages;
    }
}