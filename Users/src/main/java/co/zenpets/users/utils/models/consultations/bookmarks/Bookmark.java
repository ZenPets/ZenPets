package co.zenpets.users.utils.models.consultations.bookmarks;

import com.google.gson.annotations.SerializedName;

public class Bookmark {

    @SerializedName("error") private Boolean error;
    @SerializedName("bookmarkID") private String bookmarkID;
    @SerializedName("consultationID") private String consultationID;
    @SerializedName("userID") private String userID;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getBookmarkID() {
        return bookmarkID;
    }

    public void setBookmarkID(String bookmarkID) {
        this.bookmarkID = bookmarkID;
    }

    public String getConsultationID() {
        return consultationID;
    }

    public void setConsultationID(String consultationID) {
        this.consultationID = consultationID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}