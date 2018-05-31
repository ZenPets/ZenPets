package biz.zenpets.users.utils.models.kennels.reviews;

import com.google.gson.annotations.SerializedName;

public class KennelReview {
    @SerializedName("reviewID") private String reviewID;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("doctorPrefix") private String doctorPrefix;
    @SerializedName("doctorName") private String doctorName;
    @SerializedName("doctorDisplayProfile") private String doctorDisplayProfile;
    @SerializedName("userID") private String userID;
    @SerializedName("userName") private String userName;
    @SerializedName("userDisplayProfile") private String userDisplayProfile;
    @SerializedName("visitReasonID") private String visitReasonID;
    @SerializedName("visitReason") private String visitReason;
    @SerializedName("recommendStatus") private String recommendStatus;
    @SerializedName("appointmentStatus") private String appointmentStatus;
    @SerializedName("doctorExperience") private String doctorExperience;
    @SerializedName("reviewTimestamp") private String reviewTimestamp;
}