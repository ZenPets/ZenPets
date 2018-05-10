package biz.zenpets.users.utils.models.adoptions.messages;

import com.google.gson.annotations.SerializedName;

public class AdoptionMessage {

    @SerializedName("messageID") private String messageID;
    @SerializedName("adoptionID") private String adoptionID;
    @SerializedName("userID") private String userID;
    @SerializedName("messageText") private String messageText;
    @SerializedName("messageTimeStamp") private String messageTimeStamp;

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getAdoptionID() {
        return adoptionID;
    }

    public void setAdoptionID(String adoptionID) {
        this.adoptionID = adoptionID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageTimeStamp() {
        return messageTimeStamp;
    }

    public void setMessageTimeStamp(String messageTimeStamp) {
        this.messageTimeStamp = messageTimeStamp;
    }
}