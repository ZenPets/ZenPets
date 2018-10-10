package co.zenpets.groomers.utils.models.enquiries;

import com.google.gson.annotations.SerializedName;

public class UnreadCount {

    @SerializedName("error") private Boolean error;
    @SerializedName("unreadMessages") private String unreadMessages;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(String unreadMessages) {
        this.unreadMessages = unreadMessages;
    }
}