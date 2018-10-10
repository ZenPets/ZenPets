package co.zenpets.users.utils.models.groomers.enquiry;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EnquiryMessages {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("messages") private ArrayList<EnquiryMessage> messages = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<EnquiryMessage> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<EnquiryMessage> messages) {
        this.messages = messages;
    }
}