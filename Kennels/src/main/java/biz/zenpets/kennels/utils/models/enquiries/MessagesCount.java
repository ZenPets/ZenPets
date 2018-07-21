package biz.zenpets.kennels.utils.models.enquiries;

import com.google.gson.annotations.SerializedName;

public class MessagesCount {

    @SerializedName("error") private Boolean error;
    @SerializedName("totalMessages") private String totalMessages;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(String totalMessages) {
        this.totalMessages = totalMessages;
    }
}