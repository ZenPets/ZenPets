package biz.zenpets.users.utils.models.kennels.enquiry;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EnquiryMessages {

    @SerializedName("error") private Boolean error;
    @SerializedName("messages") private ArrayList<EnquiryMessage> messages = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<EnquiryMessage> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<EnquiryMessage> messages) {
        this.messages = messages;
    }
}