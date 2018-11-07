package co.zenpets.doctors.utils.models.consultations.votes;

import com.google.gson.annotations.SerializedName;

public class Vote {

    @SerializedName("error") private Boolean error;
    @SerializedName("voteID") private String voteID;
    @SerializedName("replyID") private String replyID;
    @SerializedName("userID") private String userID;
    @SerializedName("voteStatus") private String voteStatus;
    @SerializedName("voteTimestamp") private String voteTimestamp;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getVoteID() {
        return voteID;
    }

    public void setVoteID(String voteID) {
        this.voteID = voteID;
    }

    public String getReplyID() {
        return replyID;
    }

    public void setReplyID(String replyID) {
        this.replyID = replyID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getVoteStatus() {
        return voteStatus;
    }

    public void setVoteStatus(String voteStatus) {
        this.voteStatus = voteStatus;
    }

    public String getVoteTimestamp() {
        return voteTimestamp;
    }

    public void setVoteTimestamp(String voteTimestamp) {
        this.voteTimestamp = voteTimestamp;
    }
}