package biz.zenpets.users.utils.models.kennels.reviews;

import com.google.gson.annotations.SerializedName;

public class KennelRating {

    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("avgKennelRating") private String avgKennelRating;

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

    public String getAvgKennelRating() {
        return avgKennelRating;
    }

    public void setAvgKennelRating(String avgKennelRating) {
        this.avgKennelRating = avgKennelRating;
    }
}