package biz.zenpets.users.utils.models.consultations.consultations;

import com.google.gson.annotations.SerializedName;

public class Consultation {

    @SerializedName("consultationID") private String consultationID;
    @SerializedName("userID") private String userID;
    @SerializedName("petID") private String petID;
    @SerializedName("petGender") private String petGender;
    @SerializedName("breedName") private String breedName;
    @SerializedName("petDOB") private String petDOB;
    @SerializedName("problemID") private String problemID;
    @SerializedName("consultationTitle") private String consultationTitle;
    @SerializedName("consultationDescription") private String consultationDescription;
    @SerializedName("consultationPicture") private String consultationPicture;
    @SerializedName("consultationTimestamp") private String consultationTimestamp;
    @SerializedName("consultationViews") private String consultationViews;
    @SerializedName("consultationReplies") private String consultationReplies;

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

    public String getPetID() {
        return petID;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }

    public String getPetGender() {
        return petGender;
    }

    public void setPetGender(String petGender) {
        this.petGender = petGender;
    }

    public String getBreedName() {
        return breedName;
    }

    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }

    public String getPetDOB() {
        return petDOB;
    }

    public void setPetDOB(String petDOB) {
        this.petDOB = petDOB;
    }

    public String getProblemID() {
        return problemID;
    }

    public void setProblemID(String problemID) {
        this.problemID = problemID;
    }

    public String getConsultationTitle() {
        return consultationTitle;
    }

    public void setConsultationTitle(String consultationTitle) {
        this.consultationTitle = consultationTitle;
    }

    public String getConsultationDescription() {
        return consultationDescription;
    }

    public void setConsultationDescription(String consultationDescription) {
        this.consultationDescription = consultationDescription;
    }

    public String getConsultationPicture() {
        return consultationPicture;
    }

    public void setConsultationPicture(String consultationPicture) {
        this.consultationPicture = consultationPicture;
    }

    public String getConsultationTimestamp() {
        return consultationTimestamp;
    }

    public void setConsultationTimestamp(String consultationTimestamp) {
        this.consultationTimestamp = consultationTimestamp;
    }

    public String getConsultationViews() {
        return consultationViews;
    }

    public void setConsultationViews(String consultationViews) {
        this.consultationViews = consultationViews;
    }

    public String getConsultationReplies() {
        return consultationReplies;
    }

    public void setConsultationReplies(String consultationReplies) {
        this.consultationReplies = consultationReplies;
    }
}